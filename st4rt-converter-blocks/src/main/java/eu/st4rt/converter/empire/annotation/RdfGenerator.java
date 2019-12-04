/*
 * Copyright (c) 2009-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Some modifications added:
 * IT2Rail. Contract H2020 - N. 636078
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the IT2Rail Consortium Agreement for the specific language governing permissions and
 * limitations of use.
 */

package eu.st4rt.converter.empire.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.base.Dates;
import com.complexible.common.base.Strings2;
import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.openrdf.util.ModelBuilder;
import com.complexible.common.openrdf.util.ResourceBuilder;
import com.complexible.common.util.PrefixMapping;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.inject.ConfigurationException;
import com.google.inject.ProvisionException;

import eu.st4rt.converter.empire.Dialect;
import eu.st4rt.converter.empire.Empire;
import eu.st4rt.converter.empire.EmpireGenerated;
import eu.st4rt.converter.empire.EmpireOptions;
import eu.st4rt.converter.empire.annotation.runtime.Proxy;
import eu.st4rt.converter.empire.annotation.xsdgenerated.QueryType;
import eu.st4rt.converter.empire.codegen.InstanceGenerator;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.DataSourceUtil;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.util.BeanReflectUtil;
import eu.st4rt.converter.empire.util.EmpireUtil;
import eu.st4rt.converter.org.it2rail.semanticgraphmanager.RdfTypeConverter;
import eu.st4rt.converter.org.it2rail.semanticgraphmanager.SuperClassNamedGraph;
import eu.st4rt.converter.util.QueryUtil;
import eu.st4rt.converter.util.StackTraceUtil;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import st4rt.convertor.empire.annotation.Link;
import st4rt.convertor.empire.annotation.Namespaces;
import st4rt.convertor.empire.annotation.Query;
import st4rt.convertor.empire.annotation.RdfId;
import st4rt.convertor.empire.annotation.RdfProperty;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.SupportsRdfId;
import sun.reflect.generics.reflectiveObjects.WildcardTypeImpl;

/**
 * <p>Description: Utility for creating RDF from a compliant Java Bean, and for turning RDF (the results of a describe
 * on a given rdf:ID into a KB) into a Java bean.</p>
 * <p>Usage:<br/>
 * <code><pre>
 *   MyClass aObj = new MyClass();
 * <p>
 *   // set some data on the object
 *   KB.add(RdfGenerator.toRdf(aObj));
 * <p>
 *   MyClass aObjCopy = RdfGenerator.fromRdf(MyClass.class, aObj.getRdfId(), KB);
 * <p>
 *   // this will print true
 *   System.out.println(aObj.equals(aObjCopy));
 * </pre>
 * </code>
 * </p>
 * <p>
 * Compliant classes must be annotated with the {@link Entity} JPA annotation, the {@link RdfsClass} annotation,
 * and must implement the {@link SupportsRdfId} interface.</p>
 *
 * @author Michael Grove
 * @version 1.0
 * @since 0.1
 */
public final class RdfGenerator {

	/**
	 * Global ValueFactory to use for converting Java values into sesame objects for serialization to RDF
	 */
	private static final ValueFactory FACTORY = SimpleValueFactory.getInstance();

	private static final ContainsResourceValues CONTAINS_RESOURCES = new ContainsResourceValues();

	private static final LanguageFilter LANG_FILTER = new LanguageFilter(getLanguageForLocale());

	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RdfGenerator.class);
	private static final Logger DEBUG_LOGGER = LoggerFactory.getLogger("debug_logger");

	/**
	 * Map from rdf:type URI's to the Java class which corresponds to that resource.
	 */
	private final static Multimap<IRI, Class> TYPE_TO_CLASS = HashMultimap.create();

	/**
	 * Map to keep a record of what instances are currently being created in order to prevent cycles.  Keys are the
	 * identifiers of the instances and the values are the instances
	 */
	private final static Map<Object, Object> OBJECT_M = Maps.newHashMap();



	private final static Set<Class<?>> REGISTERED_FOR_NS = Sets.newHashSet();

	/**
	 * Cache the AccessibleObjects to avoid repeated inspections
	 */
	private final static Map<Class<?>, Set<Field>> DECLARED_FIELDS_CACHE = Maps.newHashMap();
	private final static Map<Class<?>, PropertyDescriptor[]> PROPERTY_DESCRIPTORS_CACHE = Maps.newHashMap();
	private final static Map<String, QueryType> QUERIES_MAP = QueryUtil.loadQueriesFromFile();


	/**
	 * Initialize some parameters in the RdfGenerator.  This caches namespace and type mapping information locally
	 * which will be used in subsequent rdf generation requests.
	 *
	 * @param theClasses the list of classes to be handled by the RdfGenerator
	 */
	public static synchronized void init(Collection<Class<?>> theClasses) {
		for (Class<?> aClass : theClasses) {
			RdfsClass aAnnotation = aClass.getAnnotation(RdfsClass.class);

			if (aAnnotation != null) {
				addNamespaces(aClass);

				TYPE_TO_CLASS.put(FACTORY.createIRI(PrefixMapping.GLOBAL.uri(aAnnotation.value())), aClass);
			}
		}
	}



	/**
	 * Riccardo Santoro for IT2Rail.
	 * If the object's superclass has a @NamedGraph annotation return the superclass' RdfsClass
	 * annotation to be used in asRdf method to create a rdfs:subClassOf property
	 *
	 * @param obj object whose superclass RdfsClass annotation is to be returned
	 *            return annotation or null if the superclass does not have one
	 *            throw exception if the superclass' RdfsClass is the same as the objects
	 */

	private static <T> T setObjectXmlID(T instance) {

		if(instance == null) return instance;

		try {

			Field  xmlIdField = BeanReflectUtil.getXSDIDField(instance.getClass());

			if(BeanReflectUtil.safeGet(xmlIdField, instance)== null)
				BeanReflectUtil.safeSet(xmlIdField, instance,"ID"+UUID.randomUUID());
			return instance;
		} catch (InvalidRdfException | InvocationTargetException e) {

			DEBUG_LOGGER.debug(e.getMessage());

		}
		finally  {
			return instance;
		}


	}

	@SuppressWarnings("unchecked")
	private static void alterRdfsClassAnnotation(Class<?> targetClass, Class<? extends Annotation> targetAnnotation, Annotation targetValue) {
		try {
			Method method = Class.class.getDeclaredMethod("annotationData", null);
			method.setAccessible(true);

			Object annotationData = method.invoke(targetClass);

			Field annotations = annotationData.getClass().getDeclaredField("annotations");
			annotations.setAccessible(true);

			Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
			map.put(targetAnnotation, targetValue);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String superClass(Object obj) throws InvalidRdfException {

		String superClassName = null;
		Class<?> aSuperClass = null;
		Class<?> thisClass = null;

		try {
			thisClass = ((EmpireGenerated) obj).getInterfaceClass();
		} catch (ClassCastException e) {
			thisClass = obj.getClass();
		}

		aSuperClass = thisClass.getSuperclass();

		if (SuperClassNamedGraph.hasMappedSuperClassSpecified(aSuperClass)) {


			superClassName = PrefixMapping.GLOBAL.uri(aSuperClass.getAnnotation(RdfsClass.class).value());

			if (superClassName.equals(PrefixMapping.GLOBAL.uri(thisClass.getAnnotation(RdfsClass.class).value()))) {
				throw new InvalidRdfException(thisClass.getSimpleName() + " RdfsClass: " + thisClass.getAnnotation(RdfsClass.class).value() + " is identical with superclass " + aSuperClass.getSimpleName());
			}

		}

		return superClassName;

	}

	public static <T> T fromRdf(Class<T> theClass, SupportsRdfId.RdfKey theId, DataSource theSource) throws InvalidRdfException, DataSourceException {
		return fromRdf(theClass, theId, theSource,OBJECT_M);
	}

	public static <T> T fromRdf(Class<T> theClass, String theKey, DataSource theSource) throws InvalidRdfException, DataSourceException {
		return fromRdf( theClass, theKey,  theSource, OBJECT_M);
	}
	/**
	 * Create an instance of the specified class and instantiate it's data from the given data source using the RDF
	 * instance specified by the given URI
	 *
	 * @param theClass  the class to create
	 * @param theKey    the id of the RDF individual containing the data for the new instance
	 * @param theSource the KB to get the RDF data from
	 * @param <T>       the type of the instance to create
	 * @return a new instance
	 * @throws InvalidRdfException thrown if the class does not support RDF JPA operations, or does not provide sufficient access to its fields/data.
	 * @throws DataSourceException thrown if there is an error while retrieving data from the graph
	 */
	public static <T> T fromRdf(Class<T> theClass, String theKey, DataSource theSource, Map<Object, Object> oMap) throws InvalidRdfException, DataSourceException {
		return fromRdf(theClass, EmpireUtil.asPrimaryKey(theKey), theSource, oMap);
	}

	/**
	 * Create an instance of the specified class and instantiate it's data from the given data source using the RDF
	 * instance specified by the given URI
	 *
	 * @param theClass  the class to create
	 * @param theURI    the id of the RDF individual containing the data for the new instance
	 * @param theSource the KB to get the RDF data from
	 * @param <T>       the type of the instance to create
	 * @return a new instance
	 * @throws InvalidRdfException thrown if the class does not support RDF JPA operations, or does not provide sufficient access to its fields/data.
	 * @throws DataSourceException thrown if there is an error while retrieving data from the graph
	 */
	private static <T> T fromRdf(Class<T> theClass, java.net.URI theURI, DataSource theSource, Map<Object, Object> oMap) throws InvalidRdfException, DataSourceException {
		return fromRdf(theClass, new SupportsRdfId.URIKey(theURI), theSource, oMap);
	}

	/**
	 * Create an instance of the specified class and instantiate it's data from the given data source using the RDF
	 * instance specified by the given URI
	 *
	 * @param theClass  the class to create
	 * @param theId     the id of the RDF individual containing the data for the new instance
	 * @param theSource the KB to get the RDF data from
	 * @param <T>       the type of the instance to create
	 * @return a new instance
	 * @throws InvalidRdfException thrown if the class does not support RDF JPA operations, or does not provide sufficient access to its fields/data.
	 * @throws DataSourceException thrown if there is an error while retrieving data from the graph
	 */
	public static <T> T fromRdf(Class<T> theClass, SupportsRdfId.RdfKey theId, DataSource theSource, Map<Object, Object> oMap) throws InvalidRdfException, DataSourceException {
		T aObj;

		long start = System.currentTimeMillis();
		try {
			aObj = Empire.get().instance(theClass);
		} catch (ConfigurationException | ProvisionException ex) {
			aObj = null;
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tried to get instance of class in {} ms ", (System.currentTimeMillis() - start));
		}

		start = System.currentTimeMillis();

		if (aObj == null) {
			// this means Guice construction failed, which is not surprising since that's not going to be the default.
			// so we'll try our own reflect based creation or create bytecode for an interface.

			try {
				long istart = System.currentTimeMillis();


				if (theClass.isInterface() || Modifier.isAbstract(theClass.getModifiers())) {
					aObj = InstanceGenerator.generateInstanceClass(theClass).newInstance();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("CodeGenerated instance in {} ms. ", (System.currentTimeMillis() - istart));
					}
				} else {
					aObj = theClass.newInstance();

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("CodeGenerated instance in {} ms. ", (System.currentTimeMillis() - istart));
					}
				}
			} catch (InstantiationException e) {
				throw new InvalidRdfException("Cannot create instance of bean, should have a default constructor.", e);
			} catch (IllegalAccessException e) {
				throw new InvalidRdfException("Could not access default constructor for class: " + theClass, e);
			} catch (Exception e) {
				throw new InvalidRdfException("Cannot create an instance of bean", e);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Got reflect instance of class {} ms ", (System.currentTimeMillis() - start));
			}

			start = System.currentTimeMillis();
		}

		if (theId != null) {
			asSupportsRdfId(aObj).setRdfId(theId);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Has rdfId {} ms", (System.currentTimeMillis() - start));
			}

			Class<T> aNewClass = determineClass(theClass, aObj, theSource);

			if (!aNewClass.equals(aObj.getClass())) {
				try {
					aObj = aNewClass.newInstance();
				} catch (InstantiationException e) {
					throw new InvalidRdfException("Cannot create instance of bean, should have a default constructor.", e);
				} catch (IllegalAccessException e) {
					throw new InvalidRdfException("Could not access default constructor for class: " + theClass, e);
				} catch (Exception e) {
					throw new InvalidRdfException("Cannot create an instance of bean", e);
				}

				asSupportsRdfId(aObj).setRdfId(theId);
			}
		}

		return fromRdf(aObj, theSource, oMap);
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> determineClass(Class<T> theOrigClass, T theObj, DataSource theSource) throws InvalidRdfException, DataSourceException {
		Class aResult = theOrigClass;

		//		ExtGraph aGraph = new ExtGraph(DataSourceUtil.describe(theSource, theObj));
		final Collection<Value> aTypes = DataSourceUtil.getValues(theSource, EmpireUtil.asResource(EmpireUtil.asSupportsRdfId(theObj)), RDF.TYPE);

		// right now, our best match is the original class (we will refine later)

		//		final Resource aTmpRes = EmpireUtil.asResource(aTmpSupportsRdfId);

		// iterate for all rdf:type triples in the data
		// There may be multiple rdf:type triples, which can then translate onto multiple candidate Java classes
		// some of the Java classes may belong to the same class hierarchy, whereas others can have no common
		// super class (other than java.lang.Object)
		for (Value aValue : aTypes) {
			if (!(aValue instanceof IRI)) {
				// there is no URI in the object position of rdf:type
				// ignore that data
				continue;
			}

			IRI aType = (IRI) aValue;

			for (Class aCandidateClass : TYPE_TO_CLASS.get(aType)) {
				if (aCandidateClass.equals(aResult)) {
					// it is mapped to the same Java class, that we have; ignore
					continue;
				}

				// at this point we found an rdf:type triple that resolves to a different Java class than we have
				// we are only going to accept this candidate class if it is a subclass of the current Java class
				// (doing otherwise, may cause class cast exceptions)

				if (aResult.isAssignableFrom(aCandidateClass)) {
					aResult = aCandidateClass;
				}
			}
		}

		try {
			if (aResult.isInterface() || Modifier.isAbstract(aResult.getModifiers()) || !EmpireGenerated.class.isAssignableFrom(aResult)) {
				aResult = InstanceGenerator.generateInstanceClass(aResult);
			}
		} catch (Exception e) {
			throw new InvalidRdfException("Cannot generate a class for a bean", e);
		}

		return aResult;
	}

	private static Set<Field> getCachedDeclaredFields(Class clazz) {
		Set<Field> declaredFields = DECLARED_FIELDS_CACHE.get(clazz);

		if (declaredFields == null) {
			declaredFields = BeanReflectUtil.getAllDeclaredFields(clazz);
			DECLARED_FIELDS_CACHE.put(clazz, declaredFields);
		}
		return declaredFields;
	}

	private static PropertyDescriptor[] getCachedPropertyDescriptors(Class clazz) {
		PropertyDescriptor[] propertyDescriptors = PROPERTY_DESCRIPTORS_CACHE.get(clazz);

		if (propertyDescriptors == null) {
			propertyDescriptors = PropertyUtils.getPropertyDescriptors(clazz);
			PROPERTY_DESCRIPTORS_CACHE.put(clazz, propertyDescriptors);
		}
		return propertyDescriptors;
	}

	private static RdfProperty getRdfPropertyAnnotation(AccessibleObject aAccess) {
		return (aAccess != null ? aAccess.getAnnotation(RdfProperty.class) : null);
	}

	private static Collection<Value> getValuesFromRdfProperty(AccessibleObject aAccess, RdfProperty rdfProperty, Model aGraph, Resource aRes, DataSource theSource) {
		Resource currentResource = aRes;
		Model subGraph = aGraph;

		Link[] links = rdfProperty.links();
		if (links != null && links.length != 0) {
			for (Link link : rdfProperty.links()) {
				currentResource = Models2.getResource(subGraph, currentResource, iri(link.propertyName())).orElse(null);

				if (currentResource == null) {
					DEBUG_LOGGER.error("Invalid link chain for rdfProperty " + rdfProperty.propertyName() + " of class " + BeanReflectUtil.classFrom(aAccess));
					return null;
				}

				try {
					subGraph = DataSourceUtil.describe(theSource, currentResource);

					// list -> only first element is processed
					if (Models2.isList(subGraph, currentResource)) {
						currentResource = Models2.getResource(subGraph, currentResource, RDF.FIRST).orElse(null);
						subGraph = DataSourceUtil.describe(theSource, currentResource);
					}
				} catch (QueryException e) {
					DEBUG_LOGGER.error("Sub-graph error: " + StackTraceUtil.getStackTrace(e));
					return null;
				}
			}
		}

		if (!StringUtils.isEmpty(rdfProperty.value())) {
			AsValueFunction aFunctions = new AsValueFunction(aAccess);
			return subGraph.filter(currentResource, iri(rdfProperty.propertyName()), aFunctions.apply(rdfProperty.value())).stream().map(Statement::getSubject).collect(Collectors.toSet());
		} else {
			return subGraph.filter(currentResource, iri(rdfProperty.propertyName()), null).stream().map(Statement::getObject).collect(Collectors.toSet());
		}
	}

	private synchronized static <T> T processClassAccessors(Class<T> theClass, final Resource aRes, DataSource theSource, Map<Object, Object> oMap) throws InvalidRdfException, IllegalAccessException, InstantiationException {
		Model aGraph = null;
		try {
			aGraph = DataSourceUtil.describe(theSource, aRes);
		} catch (QueryException e) {
			e.printStackTrace();
		}

		if (aGraph == null || aGraph.size() == 0) {
			return null;
		}

		return processClassAccessors(null, theClass, aRes, theSource, aGraph, oMap);
	}

	private synchronized static <T> T processClassAccessors(T theObj, Class<T> theClass, final Resource aRes, DataSource theSource, Model aGraph, Map<Object, Object> oMap) throws InvalidRdfException {

		Resource currentResource = aRes;

		HashMap<String, Collection<Value>> classQueriesOutputs = Maps.newHashMap();
		List<Query> classQueries = QueryUtil.getClassQueryAnnotations(theClass, Query.QueryType.Lowering);

		if (classQueries != null) {
			for (Query annotationQuery : classQueries) {
				DEBUG_LOGGER.info("2: Found lowering @Query with name: " + annotationQuery.name()+" on class: "+theClass.getSimpleName());

				try {
					classQueriesOutputs.putAll(QueryUtil.executeQuery(QUERIES_MAP, annotationQuery, currentResource, aGraph, theSource));
				} catch (QueryException e) {
					LOGGER.warn(e.getMessage());
					continue;
				}
			}
		}


		PropertyDescriptor[] propertyDescriptors = getCachedPropertyDescriptors(theClass);
		Set<Field> declaredFields = getCachedDeclaredFields(theClass);


		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			if (isIgnored(propertyDescriptor)) continue;

			AccessibleObject aAccess;
			IRI aProp = null;

			// is value set by class select @Query??
			Collection<Value> aValues = classQueriesOutputs.get(propertyDescriptor.getName());

			if (aValues != null) {
				try {
					aAccess = BeanReflectUtil.getFieldByName(declaredFields, propertyDescriptor.getName(), theClass);
				} catch (IllegalStateException e) {
					//TODO: exception??
					LOGGER.warn(e.getMessage());
					continue;
				}
			} else {
				// find setter
				aAccess = propertyDescriptor.getWriteMethod();

				RdfProperty rdfPropertyAnnotation = getRdfPropertyAnnotation(aAccess);
				Query queryAnnotation = QueryUtil.getQueryAnnotation(aAccess, Query.QueryType.Lowering);

				if (queryAnnotation == null && rdfPropertyAnnotation == null) {
					// setter has no @Query and no @RdfProperty
					try {
						aAccess = BeanReflectUtil.getFieldByName(declaredFields, propertyDescriptor.getName(), theClass);
					} catch (IllegalStateException e) {
						//TODO: exception??
						LOGGER.warn(e.getMessage());
						continue;
					}

					rdfPropertyAnnotation = getRdfPropertyAnnotation(aAccess);
					queryAnnotation = QueryUtil.getQueryAnnotation(aAccess, Query.QueryType.Lowering);
				}

				if (queryAnnotation != null) {
					// attribute has @Query
					DEBUG_LOGGER.info("2: Found lowering @Query on attribute: " +propertyDescriptor.getShortDescription()+" with name: " + queryAnnotation.name() +" on class: "+theClass.getSimpleName());
					aValues = QueryUtil.getSingleValueFromQuery(QUERIES_MAP, queryAnnotation, currentResource, theSource);
				} else if (rdfPropertyAnnotation != null) {
					// attribute has @RdfProperty
					aProp = iri(rdfPropertyAnnotation.propertyName());
					aValues = getValuesFromRdfProperty(aAccess, rdfPropertyAnnotation, aGraph, currentResource, theSource);
				} else {
					// attribute has no @Query and no @RdfProperty
					try {
						if (aAccess.isAnnotationPresent(XmlIDREF.class)) {
							continue;
						}

						Class aAccessClazz = BeanReflectUtil.classFrom(aAccess);

						if (isPrimitive(aAccessClazz) || Enum.class.isAssignableFrom(aAccessClazz) || JAXBElement.class.isAssignableFrom(aAccessClazz)) {
							continue;
						}

						Object aValue;

						if (Collection.class.isAssignableFrom(aAccessClazz)) {
							Class clazz = refineClass(aAccess, aAccessClazz, theSource, currentResource);

							if (isPrimitive(clazz) || Enum.class.isAssignableFrom(clazz) || JAXBElement.class.isAssignableFrom(clazz)) {
								continue;
							}

							aValue = processClassAccessors(clazz, aRes, theSource, oMap);

							if (aValue != null) {
								Collection col = BeanReflectUtil.instantiateCollectionFromField(aAccessClazz);
								col.add(aValue);
								aValue = col;
							}

						} else {
							aValue = processClassAccessors(aAccessClazz, aRes, theSource, oMap);
						}

						if (aValue == null) continue;

						if (theObj == null) theObj = theClass.newInstance();
						BeanReflectUtil.safeSet(aAccess, theObj, aValue);
						continue;
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}

			if (aValues == null || aValues.isEmpty()) continue;

			ToObjectFunction aFunc2 = new ToObjectFunction(theSource, currentResource, aAccess, aProp, oMap);

			Object aValue = aFunc2.apply(aValues);


			try {
				if (theObj == null) theObj = theClass.newInstance();

				BeanReflectUtil.safeSet(aAccess, theObj, RdfTypeConverter.convert(aAccess, aValue));
			} catch (InvocationTargetException | IllegalAccessException e) {
				// oh crap
				throw new InvalidRdfException(e);
			} catch (IllegalArgumentException e) {
				// this is "likely" to happen.  we'll get this exception if the rdf does not match the java.  for example
				// if something is specified to be an int in the java class, but it typed as a float (though down conversion
				// in that case might work) the set call will fail.
				// TODO: shouldnt this be an error?

				LOGGER.warn(e.getMessage());
				LOGGER.warn("Probable type mismatch: {} {}", aValue, aAccess);
			} catch (RuntimeException e) {
				// TODO: i dont like keying on a RuntimeException here to get the error condition, but since the
				// Function interface does not throw anything, this is the best we can do.  maybe consider a
				// version of the Function interface that has a throws clause, it would make this more clear.

				// this was probably an error converting from a Value to an Object
				throw new InvalidRdfException(e);
			} catch (InstantiationException e) {
				throw new InvalidRdfException(e);
			}
		}

		T theResult = setObjectXmlID(theObj);
		oMap.put(aRes, theResult);

		return theResult;


	}

	/**
	 * Populate the fields of the current instance from the RDF indiviual with the given URI
	 *
	 * @param theObj    the Java object to populate
	 * @param theSource the KB to get the RDF data from
	 * @param <T>       the type of the class being populated
	 * @return theObj, populated from the specified DataSource
	 * @throws InvalidRdfException thrown if the object does not support the RDF JPA API.
	 * @throws DataSourceException thrown if there is an error retrieving data from the database
	 */
	@SuppressWarnings("unchecked")
	private synchronized static <T> T fromRdf(T theObj, DataSource theSource, Map<Object, Object> oMap) throws
	InvalidRdfException, DataSourceException {
		final SupportsRdfId aSupportsRdfId = asSupportsRdfId(theObj);
		final SupportsRdfId.RdfKey theKeyObj = aSupportsRdfId.getRdfId();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Converting {} to RDF.", theObj);
		}



		if (oMap.containsKey(theKeyObj)) {
			// TODO: this is probably a safe cast, i dont see how something w/ the same URI, which should be the same
			// object would change types

			return (T) oMap.get(theKeyObj);
		}

		try {


			oMap.put(theKeyObj, theObj);

			Model aGraph = DataSourceUtil.describe(theSource, theObj);

			if (aGraph.size() == 0) {
				return theObj;
			}

			addNamespaces(theObj.getClass());

			final Resource aRes = EmpireUtil.asResource(aSupportsRdfId);

			processClassAccessors(theObj, (Class<T>) theObj.getClass(), aRes, theSource, aGraph,  oMap);

			return theObj;
		} finally {

			oMap.remove(theKeyObj);
		}
	}


	/**
	 * Return the RdfClass annotation on the object.
	 *
	 * @param theObj the object to get that annotation from
	 * @return the objects' RdfClass annotation
	 * @throws InvalidRdfException thrown if the object does not have the required annotation, does not have an @Entity
	 *                             annotation, or does not {@link SupportsRdfId support Rdf Id's}
	 */

	private static RdfsClass asValidRdfClass(Object theObj) throws InvalidRdfException {
		if (!BeanReflectUtil.hasAnnotation(theObj.getClass(), RdfsClass.class)) {
			throw new InvalidRdfException("Specified value is not an RdfsClass object");
		}

		if (EmpireOptions.ENFORCE_ENTITY_ANNOTATION && !BeanReflectUtil.hasAnnotation(theObj.getClass(), Entity.class)) {
			throw new InvalidRdfException("Specified value is not a JPA Entity object");
		}

		// verify that it supports rdf id's
		asSupportsRdfId(theObj);

		return BeanReflectUtil.getAnnotation(theObj.getClass(), RdfsClass.class);
	}

	/**
	 * Return the object casted to {@link SupportsRdfId}
	 *
	 * @param theObj the object to cast
	 * @return the object, casted to the interface
	 * @throws InvalidRdfException thrown if the object does not implement the interface
	 */
	private static SupportsRdfId asSupportsRdfId(Object theObj) throws InvalidRdfException {
		if (!(theObj instanceof SupportsRdfId)) {
			throw new InvalidRdfException("Object of type '" + (theObj.getClass().getName()) + "' does not implements SupportsRdfId.");
		} else {
			return (SupportsRdfId) theObj;
		}
	}

	private static EmpireGenerated asEmpireGenerated(Object theObj) throws InvalidRdfException {
		if (!(theObj instanceof EmpireGenerated)) {
			throw new InvalidRdfException("Object of type '" + (theObj.getClass().getName()) + "' does not implements EmpireGenerated.");
		} else {
			return (EmpireGenerated) theObj;
		}
	}

	/**
	 * Given an object, return it's rdf:ID.  If it already has an id, that will be returned, otherwise the id
	 * will either be generated from the data, using the {@link RdfId} annotation as a guide, or it will auto-generate one.
	 *
	 * @param theObj the object
	 * @return the object's rdf:Id
	 * @throws InvalidRdfException thrown if the object does not support the minimum to create or retrieve an rdf:ID
	 * @see SupportsRdfId
	 */
	public static Resource id(Object theObj) throws InvalidRdfException {
		SupportsRdfId aSupport = asSupportsRdfId(theObj);

		if (aSupport.getRdfId() != null) {
			return EmpireUtil.asResource(aSupport);
		}

		Field aIdField = BeanReflectUtil.getIdField(theObj.getClass());

		String aValue = hash(Strings2.getRandomString(10));
		String aNS = RdfId.DEFAULT;

		IRI aURI = FACTORY.createIRI(aNS + aValue);

		if (aIdField != null && !aIdField.getAnnotation(RdfId.class).namespace().equals("")) {
			aNS = aIdField.getAnnotation(RdfId.class).namespace();
		}

		if (aIdField != null) {
			boolean aOldAccess = aIdField.isAccessible();
			aIdField.setAccessible(true);

			try {
				if (aIdField.get(theObj) == null) {
					throw new InvalidRdfException("id field must have a value");
				}

				Object aValObj = aIdField.get(theObj);

				aValue = Strings2.urlEncode(aValObj.toString());

				if (EmpireUtil.isURI(aValObj)) {
					try {
						aURI = FACTORY.createIRI(aValObj.toString());
					} catch (IllegalArgumentException e) {
						// sometimes sesame disagrees w/ Java about what a valid URI is.  so we'll have to try
						// and construct a URI from the possible fragment
						aURI = FACTORY.createIRI(aNS + aValue);
					}
				} else {
					//aValue = hash(aValObj);
					aURI = FACTORY.createIRI(aNS + aValue);
				}
			} catch (IllegalAccessException ex) {
				throw new InvalidRdfException(ex);
			}

			aIdField.setAccessible(aOldAccess);
		}

		aSupport.setRdfId(new SupportsRdfId.URIKey(java.net.URI.create(aURI.toString())));

		return aURI;
	}

	/**
	 * Scan the object for {@link Namespaces} annotations and add them to the current list of known namespaces
	 *
	 * @param theObj the object to scan.
	 */
	public static void addNamespaces(Class<?> theObj) {
		if (theObj == null || REGISTERED_FOR_NS.contains(theObj)) {
			return;
		}

		REGISTERED_FOR_NS.add(theObj);

		Namespaces aNS = BeanReflectUtil.getAnnotation(theObj, Namespaces.class);

		if (aNS == null) {
			return;
		}

		int aIndex = 0;
		while (aIndex + 1 < aNS.value().length) {
			String aPrefix = aNS.value()[aIndex];
			String aURI = aNS.value()[aIndex + 1];

			// TODO: maybe have a local version of this, this will add a global namespace, and could potentially
			// overwrite global things that use the same prefix but different uris, which would be bad
			PrefixMapping.GLOBAL.addMapping(aPrefix, aURI);
			aIndex += 2;
		}
	}

	/**
	 * Return the given Java bean as a set of RDF triples
	 *
	 * @param theObj the object
	 * @return the object represented as RDF triples
	 * @throws InvalidRdfException thrown if the object cannot be transformed into RDF.
	 */
	public static Model asRdf(final Object theObj, DataSource theSource ) throws InvalidRdfException {
		if (theObj == null) {
			return null;
		}

		Object aObj = theObj;

		if (aObj instanceof ProxyHandler) {
			aObj = ((ProxyHandler) aObj).mProxy.value();
		} else {
			try {
				if (aObj.getClass().getDeclaredField("handler") != null) {
					Field aProxy = aObj.getClass().getDeclaredField("handler");
					aObj = ((ProxyHandler) BeanReflectUtil.safeGet(aProxy, aObj)).mProxy.value();
				}
			} catch (InvocationTargetException e) {
				// this is probably an error, we know its a proxy object, but can't get the proxied object
				throw new InvalidRdfException("Could not access proxy object", e);
			} catch (NoSuchFieldException e) {
				// this is probably ok.
			}
		}

		ModelBuilder aBuilder = new ModelBuilder();

		try {
			processRdfsClass(aBuilder, aObj, theSource);
		} catch (IllegalAccessException | RuntimeException e) {
			throw new InvalidRdfException(e);
		} catch (InvocationTargetException e) {
			throw new InvalidRdfException("Cannot invoke method", e);
		}

		return aBuilder.model();
	}

	private static void /*ResourceBuilder */processRdfsClass(ModelBuilder aBuilder, Object aObj, DataSource theSource) throws InvalidRdfException, InvocationTargetException, IllegalAccessException {
		RdfsClass aClass = asValidRdfClass(aObj);
		Resource aSubj = id(aObj);

		addNamespaces(aObj.getClass());

		boolean existsObjectInGraph = !aBuilder.model()
				.filter(aSubj, RDF.TYPE, FACTORY.createIRI(PrefixMapping.GLOBAL.uri(aClass.value()))).isEmpty();


		/* Riccardo Santoro for IT2Rail
		 * Add a rdfs:subClassOf property if the object has a superclass
		 *                                                             */
		if(!existsObjectInGraph) {
			//  return aRes;
			ResourceBuilder aRes = aBuilder.instance(FACTORY.createIRI(PrefixMapping.GLOBAL.uri(aClass.value())),
					aSubj);

			String superClass = superClass(aObj);
			if (superClass != null) {
				IRI subTypeOf = aBuilder.getValueFactory().createIRI("http://www.w3.org/2000/01/rdf-schema#subClassOf");
				aRes.addProperty(subTypeOf, aBuilder.getValueFactory().createIRI(superClass));
			}

			processAccessors(aBuilder, aObj, aRes, theSource);
		}
		// return aRes;
	}

	private static boolean isIgnored(final PropertyDescriptor thePropertyDescriptor) {
		Method readMethod = thePropertyDescriptor.getReadMethod();

		return (readMethod == null ||
				(thePropertyDescriptor.getName().equals("class")
						&& readMethod.getDeclaringClass() == Object.class
						&& readMethod.getReturnType().equals(Class.class)) ||
				(readMethod.getReturnType().equals(SupportsRdfId.class)) ||
				(readMethod.getReturnType().equals(SupportsRdfId.RdfKey.class))
				|| (thePropertyDescriptor.getName().equals("instanceTriples"))
				|| (thePropertyDescriptor.getName().equals("allTriples"))
				|| (thePropertyDescriptor.getName().equals("interfaceClass")));
	}

	private static boolean isPrimitive(Class theObj) {
		return (BeanReflectUtil.isPrimitive(theObj) || BigInteger.class.equals(theObj) || BigDecimal.class.equals(theObj) || int.class.equals(theObj) || float.class.equals(theObj) || boolean.class.equals(theObj)
				|| XMLGregorianCalendar.class.equals(theObj) || (theObj.getSuperclass() != null && XMLGregorianCalendar.class.equals(theObj.getSuperclass())) || Duration.class.equals(theObj));
	}

	private static void processAccessors(ModelBuilder aBuilder, Object aObj, ResourceBuilder aRes, DataSource theSource) throws InvocationTargetException, IllegalAccessException, InvalidRdfException {

		Set<Field> declaredFields = getCachedDeclaredFields(aObj.getClass());
		PropertyDescriptor[] propertyDescriptors = getCachedPropertyDescriptors(aObj.getClass());

		// find all accessible objects (fields, methods) for Clazz of aObj
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			// find getter
			AccessibleObject aAccess = propertyDescriptor.getReadMethod();

			if (aAccess == null) {
				LOGGER.warn("No getter found for property descriptor {" + propertyDescriptor.getName() + "} of class {" + aObj.getClass() + "}");
				continue;
			}

			// Class and SupportsRdfId is ignored
			if (isIgnored(propertyDescriptor)) continue;

			// is getter annotated?
			RdfProperty rdfPropertyAnnotation = getRdfPropertyAnnotation(aAccess);

			// getter is not annotated
			if (rdfPropertyAnnotation == null) {
				// find attribute
				try {
					aAccess = BeanReflectUtil.getFieldByName(declaredFields, propertyDescriptor.getName(), aObj.getClass());
				} catch (IllegalStateException e) {
					//TODO: exception??
					LOGGER.warn(e.getMessage());
					continue;
				}

				// is an attribute annotated?
				rdfPropertyAnnotation = getRdfPropertyAnnotation(aAccess);
			}
			//
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Getting rdf for : {}", aAccess);
			}

			if (aAccess.isAnnotationPresent(Transient.class)
					|| (aAccess instanceof Field
							&& Modifier.isTransient(((Field) aAccess).getModifiers()))) {
				// transient fields or accessors with the Transient annotation do not get converted.
				continue;
			}

			ResourceBuilder currentResourceBuilder = aRes;

			if (rdfPropertyAnnotation != null) {
				Link[] links = rdfPropertyAnnotation.links();

				/**
				 * 1. if the @Link annotation is specified,
				 * then the triples corresponding to the chain of links are added,
				 * and the property with its corresponding object) belongs to the element at the end of the chain
				 */
				if (links != null) {
					for (Link link : links) {
						IRI linkProperty = iri(link.propertyName());

						IRI rdfType = Strings.isNullOrEmpty(link.rdfType()) ? null : iri(link.rdfType());
						// TODO: type load from ontology?

						ResourceBuilder nodeResourceBuilder = aBuilder.instance(rdfType, link.nodeType().equals(Link.NodeType.Anonymous) ? FACTORY.createIRI(RdfId.DEFAULT+UUID.randomUUID()) : FACTORY.createIRI(RdfId.DEFAULT+link.sharedID()));
						currentResourceBuilder.addProperty(linkProperty, nodeResourceBuilder.getResource());
						currentResourceBuilder = nodeResourceBuilder;
					}
				}

				IRI aProperty = iri(rdfPropertyAnnotation.propertyName());
				AsValueFunction aFunc = new AsValueFunction(aAccess);

				/**
				 * 2. If the @RdfProperty has the "value" parameter, the object is given by the specified value
				 */
				if (!StringUtils.isEmpty(rdfPropertyAnnotation.value())) {
					Object aValue = BeanReflectUtil.safeGet(aAccess, aObj);

					if (aValue == null || aValue.toString().equals("")) continue;

					Value val = aFunc.apply(rdfPropertyAnnotation.value());
					currentResourceBuilder.addProperty(aProperty, val);



					if (!isPrimitive(aValue.getClass())) {
						processAccessors(aBuilder, aValue, aRes, theSource);
					}
				} else {
					Query queryAnnotation = QueryUtil.getQueryAnnotation(aAccess, Query.QueryType.Lifting);

					/**
					 * 3. Else, if there is an @Query annotation, then the object is the value which is the result of the query (which must select a single value)
					 */
					if (queryAnnotation != null) {
						// nothing, RDF graph is not created yet
					} else {
						Object aValue = BeanReflectUtil.safeGet(aAccess, aObj);

						/**
						 * 4. if the attribute has a complex type, the annotations of the corresponding Java class
						 * (which must be annotated with an @RdfsClass annotation) are processed recursively
						 * and the object is the instance created by the recursive process
						 *
						 * 5. else, the object is the value of the attribute
						 */
						if (aValue == null || aValue.toString().equals("")) continue;

						if (Collection.class.isAssignableFrom(aValue.getClass())) {
							List<Value> aValueList = asList(aAccess, (Collection<?>) Collection.class.cast(aValue));

							if (aValueList.isEmpty()) {
								continue;
							}

							if (rdfPropertyAnnotation.isList()) {
								currentResourceBuilder.addProperty(aProperty, aValueList);
							} else {
								for (Value aVal : aValueList) {

									currentResourceBuilder.addProperty(aProperty, aVal);
								}
							}

							Class<?> aClass = ((Collection) aValue).iterator().next().getClass();
							if (isRdfsClass(aClass)) {
								for (Object aVal : (Collection<?>) Collection.class.cast(aValue)) {
									/*
                                	if(!Strings.isNullOrEmpty(rdfPropertyAnnotation.datatype()) && !aVal.getClass().isAnnotationPresent(RdfsClass.class)) {
                                	    RdfsClass rdfsClass = new DynamicRdfsClass(rdfPropertyAnnotation.datatype());
                                	    alterRdfsClassAnnotation(aVal.getClass(),RdfsClass.class,rdfsClass);
                                 }
									 */
									processRdfsClass(aBuilder, aVal, theSource);
								}
							}
							/*
                            if (!isRdfsClass(aClass) && !isPrimitive(aClass)) {
                                for (Object aVal : (Collection<?>) Collection.class.cast(aValue)) {

                                 if(!Strings.isNullOrEmpty(rdfPropertyAnnotation.datatype()) ) {
                                	    RdfsClass rdfsClass = new DynamicRdfsClass(rdfPropertyAnnotation.datatype());
                                	    alterRdfsClassAnnotation(aVal.getClass(),RdfsClass.class,rdfsClass);
                                	    RdfsClass theAnn = aVal.getClass().getAnnotation(RdfsClass.class);
                                	    System.out.println(theAnn.value());
                                 }

                                	processRdfsClass(aBuilder, aVal, theSource);
                                }
                            }
							 */
						} else {
							currentResourceBuilder.addProperty(aProperty, aFunc.apply(aValue));


							if (isRdfsClass(aValue.getClass())) {
								processRdfsClass(aBuilder, aValue, theSource);
							}
						}
					}
				}
			} else {
				Object aValue = BeanReflectUtil.safeGet(aAccess, aObj);
				if (aValue == null || aValue.toString().equals("")) {
					continue;
				}
				if (Collection.class.isAssignableFrom(aValue.getClass())) {

					if (!isPrimitive(((Collection) aValue).iterator().next().getClass())) {
						for (Object aVal : (Collection<?>) Collection.class.cast(aValue)) {

							processAccessors(aBuilder, aVal, aRes, theSource);
						}
					}
				} else {
					if (!isPrimitive(aValue.getClass())) {

						processAccessors(aBuilder, aValue, aRes, theSource);
					}
				}
				continue;
			}
		}

		List<Query> classQueries = QueryUtil.getClassQueryAnnotations(aObj.getClass(), Query.QueryType.Lifting);

		if (classQueries == null) return;

		for (Query annotationQuery : classQueries) {
			DEBUG_LOGGER.info("2: Found lifting @Query annotation with name: " + annotationQuery.name()+" on class: "+aObj.getClass().getSimpleName());

			try {
				QueryUtil.executeConstructQuery(QUERIES_MAP, getCachedDeclaredFields(aObj.getClass()), annotationQuery, aRes, aObj, theSource);
			} catch (QueryException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isRdfsClass(Class clazz) {
		return BeanReflectUtil.hasAnnotation(clazz, RdfsClass.class);
	}

	/**
	 * Transform a list of Java Objects into the corresponding RDF values
	 *
	 * @param theAccess     the accessor for the value
	 * @param theCollection the collection to transform
	 * @return the collection as a list of RDF values
	 * @throws InvalidRdfException thrown if any of the values cannot be transformed
	 */
	private static List<Value> asList(AccessibleObject theAccess, Collection<?> theCollection) throws InvalidRdfException {
		try {
			return theCollection.stream().map(new AsValueFunction(theAccess)).collect(Collectors.toList());
		} catch (RuntimeException e) {
			throw new InvalidRdfException(e.getMessage());
		}
	}



	/**
	 * Return a base64 encoded md5 hash of the given object
	 *
	 * @param theObj the object to hash
	 * @return the hashed version of the object.
	 */
	private static String hash(Object theObj) {
		return Strings2.hex(Strings2.md5(theObj.toString()));
	}

	/**
	 * Javassist {@link MethodHandler} implementation for method proxying.
	 */
	private static class CollectionProxyHandler implements MethodHandler {

		/**
		 * The proxy object which wraps the instance being proxied.
		 */
		private CollectionProxy mProxy;

		/**
		 * Create a new ProxyHandler
		 *
		 * @param theProxy the proxy object
		 */
		private CollectionProxyHandler(final CollectionProxy theProxy) {
			mProxy = theProxy;
		}

		/**
		 * Delegates the methods to the Proxy
		 *
		 * @inheritDoc
		 */
		public Object invoke(final Object theThis, final Method theMethod, final Method theProxyMethod, final Object[] theArgs) throws Throwable {
			return theMethod.invoke(mProxy.value(), theArgs);
		}
	}

	private static class CollectionProxy {
		private Collection mCollection;
		private AccessibleObject mField;
		private Collection<Value> theList;
		private ValueToObject valueToObject;

		public CollectionProxy(final AccessibleObject theField, final Collection<Value> theTheList, final ValueToObject theValueToObject) {
			mField = theField;
			theList = theTheList;
			valueToObject = theValueToObject;

		}

		private void init() {
			Collection<Object> aValues = BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(mField));

			for (Value aValue : theList) {
				Object aListValue = valueToObject.apply(aValue);

				if (aListValue == null) {
					throw new RuntimeException("Error converting a list value.");
				}

				aValues.add(aListValue);
			}

			mCollection = aValues;
		}

		public Collection value() {
			if (mCollection == null) {
				init();
				theList.clear();

				theList = null;
				mField = null;
				valueToObject = null;
			}

			return mCollection;
		}
	}

	/**
	 * Enabling this seems to use more memory than per-object proxying (or none at all).  Is javassist leaking memory?
	 * Experimental option, not currently used.
	 */
	@Deprecated
	public static final boolean PROXY_COLLECTIONS = false;

	/**
	 * Implementation of the function interface to turn a Collection of RDF values into Java bean(s).
	 */
	private static class ToObjectFunction implements Function<Collection<Value>, Object> {
		/**
		 * Function to turn a single value into an object
		 */
		private ValueToObject valueToObject;

		/**
		 * Reference to the Type which the values will be assigned
		 */
		private AccessibleObject mField;

		public ToObjectFunction(final DataSource theSource, Resource theResource, final AccessibleObject theField, final IRI theProp, Map<Object, Object> oMap) {
			valueToObject = new ValueToObject(theSource, theResource, theField, theProp, oMap);
			mField = theField;
		}

		public Object apply(final Collection<Value> theList) {
			if (theList == null || theList.isEmpty()) {
				return BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(mField));
			}
			if (Collection.class.isAssignableFrom(BeanReflectUtil.classFrom(mField))) {
				try {

					if (PROXY_COLLECTIONS && !BeanReflectUtil.isPrimitive(refineClass(mField, BeanReflectUtil.classFrom(mField), null, null))) {
						Object aColType = BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(mField));

						ProxyFactory aFactory = new ProxyFactory();
						aFactory.setInterfaces(aColType.getClass().getInterfaces());
						aFactory.setSuperclass(aColType.getClass());
						aFactory.setFilter(METHOD_FILTER);

						Object aResult = aFactory.createClass().newInstance();
						((ProxyObject) aResult).setHandler(new CollectionProxyHandler(new CollectionProxy(mField, theList, valueToObject)));
						return aResult;
					} else {
						Collection<Object> aValues = BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(mField));

						for (Value aValue : theList) {
							Object aListValue = valueToObject.apply(aValue);

							if (aListValue == null) {
								throw new RuntimeException("Error converting a list value.");
							}

							if (aListValue instanceof Collection) {
								aValues.addAll(((Collection) aListValue));
							} else {
								aValues.add(aListValue);
							}
						}

						return aValues;
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			/**
			 * if not list all literals
			 *   proceed
			 * else
			 *  if not lang aware
			 * 	 find non lang typed literals
			 *   if >= 1 non lang typed literals
			 *     proceed
			 *   else get language based on locale
			 *     find literals based on local lang
			 *
			 *   if == 0 literals
			 *     use original list
			 *   else use filtered list
			 *
			 * else if lang aware
			 */

			Collection<Value> aList = Sets.newHashSet(theList);

			if (!aList.stream().anyMatch(CONTAINS_RESOURCES)) {
				if (!EmpireOptions.ENABLE_LANG_AWARE) {
					Collection<Value> aLangFiltered = aList.stream().filter(theLit -> !Literals.isLanguageLiteral((Literal) theLit)).collect(Collectors.toList());

					if (aLangFiltered.isEmpty()) {
						LANG_FILTER.setLangCode(getLanguageForLocale());
						aLangFiltered = aList.stream().filter(LANG_FILTER).collect(Collectors.toList());
					}

					if (!aLangFiltered.isEmpty()) {
						aList = aLangFiltered;
					}
				} else {
					LANG_FILTER.setLangCode(getRdfPropertyAnnotation(mField).language());
					aList = aList.stream().filter(LANG_FILTER).collect(Collectors.toList());
				}
			}

			if (aList.isEmpty()) {
				// yes, we checked for emptiness to begin the method, but we might have done some filtering based on the
				// language tags, so we need to check again.
				return BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(mField));
			} else if ((aList.size() == 1) || (!EmpireOptions.STRICT_MODE)) {
				// collection of one element, just convert the single element and send that back
				return valueToObject.apply(aList.iterator().next());
			} else {
				throw new RuntimeException("Cannot convert list of values to anything meaningful for the field. " + mField + " " + aList);
			}
		}
	}

	private static String getLanguageForLocale() {
		return Locale.getDefault() == null || Locale.getDefault().toString().equals("")
				? "en"
						: (Locale.getDefault().toString().indexOf("_") != -1
						? Locale.getDefault().toString().substring(0, Locale.getDefault().toString().indexOf("_"))
								: Locale.getDefault().toString());
	}

	private static Class refineClass(final Object theAccessor, final Class theClass, final DataSource theSource, final Resource theId) {
		Class aClass = theClass;

		if (Collection.class.isAssignableFrom(aClass)) {
			// if the field we're assigning from is a collection, try and figure out the type of the thing
			// we're creating from the collection

			Type[] aTypes = null;

			if (theAccessor instanceof Field && ((Field) theAccessor).getGenericType() instanceof ParameterizedType) {
				aTypes = ((ParameterizedType) ((Field) theAccessor).getGenericType()).getActualTypeArguments();
			} else if (theAccessor instanceof Method) {
				aTypes = ((Method) theAccessor).getGenericParameterTypes();
			}

			if (aTypes != null && aTypes.length >= 1) {
				// first type argument to a collection is usually the one we care most about
				if (aTypes[0] instanceof ParameterizedType && ((ParameterizedType) aTypes[0]).getActualTypeArguments().length > 0) {
					Type aType = ((ParameterizedType) aTypes[0]).getActualTypeArguments()[0];

					if (aType instanceof Class) {
						aClass = (Class) aType;
					} else if (aType instanceof WildcardTypeImpl) {
						WildcardTypeImpl aWildcard = (WildcardTypeImpl) aType;
						// trying to suss out super v extends w/o resorting to string munging.
						if (aWildcard.getLowerBounds().length == 0 && aWildcard.getUpperBounds().length > 0) {
							// no lower bounds afaik indicates ? extends Foo
							aClass = ((Class) aWildcard.getUpperBounds()[0]);
						} else if (aWildcard.getLowerBounds().length > 0) {
							// lower & upper bounds I believe indicates something of the form Foo super Bar
							aClass = ((Class) aWildcard.getLowerBounds()[0]);
						} else {
							// shoot, we'll try the string hack that Adrian posted on the mailing list.
							try {
								aClass = Class.forName(aType.toString().split(" ")[2].substring(0, aTypes[0].toString().split(" ")[2].length() - 1));
							} catch (Exception e) {
								// everything has failed, let aClass be the default (theClass) and hope for the best
							}
						}
					} else {
						// punt? wtf else could it be?
						try {
							aClass = Class.forName(aType.toString());
						} catch (ClassNotFoundException e) {
							// oh well, we did the best we can
						}
					}
				} else if (aTypes[0] instanceof Class) {
					aClass = (Class) aTypes[0];
				}
			} else {
				// could not figure out the type from the generics assertions on the Collection, they are either
				// not present, or my algorithm is not bullet proof.  So lets try checking on the annotations
				// for a type hint.

				Class aTarget = BeanReflectUtil.getTargetEntity(theAccessor);
				if (aTarget != null) {
					aClass = aTarget;
				}
			}
		}

		if (!BeanReflectUtil.hasAnnotation(aClass, RdfsClass.class) || aClass.isInterface()) {
			// k, so either the parameter of the collection or the declared type of the field does
			// not map to an instance/bean type.  this is most likely an error, but lets try and find
			// the rdf:type of the field, and see if we can map that to a class in the path and we'll
			// create an instance of that.  that will work, and pushes the likely failure back off to
			// the assignment of the created instance

			Iterable<Resource> aTypes = DataSourceUtil.getTypes(theSource, theId);

			// k, so now we know the type, if we can match the type to a class then we're in business
			for (Resource aType : aTypes) {
				if (aType instanceof IRI) {
					for (Class aTypeClass : TYPE_TO_CLASS.get((IRI) aType)) {
						if ((BeanReflectUtil.hasAnnotation(aTypeClass, RdfsClass.class)) &&
								(aClass.isAssignableFrom(aTypeClass))) {
							// lets try this one
							aClass = aTypeClass;
							break;
						}
					}
				}
			}
		}

		if (aClass.isInterface()) {
			if (BeanReflectUtil.hasAnnotation(aClass, RdfsClass.class)) {
				IRI aType = FACTORY.createIRI(((RdfsClass) aClass.getAnnotation(RdfsClass.class)).value());
				for (Class aTypeClass : TYPE_TO_CLASS.get(aType)) {
					if ((BeanReflectUtil.hasAnnotation(aTypeClass, RdfsClass.class)) &&
							(aClass.isAssignableFrom(aTypeClass))) {
						// lets try this one
						aClass = aTypeClass;
						return aClass;
					}
				}
			}
		}

		return aClass;
	}

	public static class ValueToObject implements Function<Value, Object> {
		static final List<IRI> integerTypes = Arrays.asList(XMLSchema.INT, XMLSchema.INTEGER, XMLSchema.POSITIVE_INTEGER,
				XMLSchema.NEGATIVE_INTEGER, XMLSchema.NON_NEGATIVE_INTEGER,
				XMLSchema.NON_POSITIVE_INTEGER, XMLSchema.UNSIGNED_INT);
		static final List<IRI> longTypes = Arrays.asList(XMLSchema.LONG, XMLSchema.UNSIGNED_LONG);
		static final List<IRI> floatTypes = Arrays.asList(XMLSchema.FLOAT, XMLSchema.DECIMAL);
		static final List<IRI> shortTypes = Arrays.asList(XMLSchema.SHORT, XMLSchema.UNSIGNED_SHORT);
		static final List<IRI> byteTypes = Arrays.asList(XMLSchema.BYTE, XMLSchema.UNSIGNED_BYTE);

		private IRI mProperty;
		private Object mAccessor;
		private DataSource mSource;
		private Resource mResource;
		private Map<Object, Object> mOMap;

		public ValueToObject(final DataSource theSource, Resource theResource, final Object theAccessor, final IRI theProp) {
			new ValueToObject(theSource, theResource,theAccessor, theProp, OBJECT_M);
		}
		public ValueToObject(final DataSource theSource, Resource theResource, final Object theAccessor, final IRI theProp, Map<Object, Object> oMap) {
			mResource = theResource;
			mSource = theSource;
			mAccessor = theAccessor;
			mProperty = theProp;
			mOMap = oMap;
		}

		public Object apply(final Value theValue) {
			if (mAccessor == null) {
				throw new RuntimeException("Null accessor is not permitted");
			}

			if (theValue instanceof Literal) {
				Literal aLit = (Literal) theValue;
				IRI aDatatype = aLit != null ? aLit.getDatatype() : null;
				Class accessorsClazz = BeanReflectUtil.classFrom(mAccessor);

				accessorsClazz = refineClass(mAccessor, accessorsClazz, mSource, mResource);

				if ((aDatatype == null || RDFS.LITERAL.equals(aDatatype)) || String.class.isAssignableFrom(accessorsClazz) || (XMLSchema.STRING.equals(aDatatype) && String.class.isAssignableFrom(accessorsClazz))) {
					return aLit.getLabel();
				}

				if (XMLSchema.STRING.equals(aDatatype)) {
					if (Boolean.class.isAssignableFrom(accessorsClazz) || boolean.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.BOOLEAN;
					if (BigInteger.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.INTEGER;
					if (Integer.class.isAssignableFrom(accessorsClazz) || int.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.INTEGER;
					if (Long.class.isAssignableFrom(accessorsClazz) || long.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.LONG;
					if (Double.class.isAssignableFrom(accessorsClazz) || double.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.DOUBLE;
					if (BigDecimal.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.DECIMAL;
					if (Float.class.isAssignableFrom(accessorsClazz) || float.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.FLOAT;
					if (Short.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.SHORT;
					if (Byte.class.isAssignableFrom(accessorsClazz))
						aDatatype = XMLSchema.BYTE;
				}

				if (XMLSchema.BOOLEAN.equals(aDatatype)) {
					return Boolean.valueOf(aLit.getLabel());
				}
				if (integerTypes.contains(aDatatype)) {
					if (BigDecimal.class.isAssignableFrom(accessorsClazz)) {
						return BigDecimal.valueOf(Long.parseLong(aLit.getLabel()));
					} else if (BigInteger.class.isAssignableFrom(accessorsClazz)) {
						return BigInteger.valueOf(Long.parseLong(aLit.getLabel()));
					} else return Integer.parseInt(aLit.getLabel());
				}
				if (longTypes.contains(aDatatype)) {
					return Long.parseLong(aLit.getLabel());
				}
				if (XMLSchema.DOUBLE.equals(aDatatype)) {
					return Double.valueOf(aLit.getLabel());
				}
				if (floatTypes.contains(aDatatype)) {
					if (Integer.class.isAssignableFrom(accessorsClazz) || int.class.isAssignableFrom(accessorsClazz)) {
						return Float.valueOf(aLit.getLabel()).intValue();
					} else if (BigDecimal.class.isAssignableFrom(accessorsClazz)) {
						return BigDecimal.valueOf(Float.valueOf(aLit.getLabel()));
					} else return Float.valueOf(aLit.getLabel());
				}
				if (shortTypes.contains(aDatatype)) {
					return Short.valueOf(aLit.getLabel());
				}
				if (byteTypes.contains(aDatatype)) {
					return Byte.valueOf(aLit.getLabel());
				}
				if (XMLSchema.ANYURI.equals(aDatatype)) {
					try {
						return new java.net.URI(aLit.getLabel());
					} catch (URISyntaxException e) {
						LOGGER.warn("URI syntax exception converting literal value which is not a valid URI {} ", aLit.getLabel());
						return null;
					}
				}
				if (XMLSchema.DATE.equals(aDatatype) || XMLSchema.DATETIME.equals(aDatatype)) {
					if (XMLGregorianCalendar.class.isAssignableFrom(accessorsClazz)) {
						try {
							return DatatypeFactory.newInstance().newXMLGregorianCalendar(aLit.getLabel());
						} catch (DatatypeConfigurationException e) {
							throw new RuntimeException("Unsupported or unknown literal datatype");
						}
					} else return Dates.asDate(aLit.getLabel());
				}
				if (XMLSchema.TIME.equals(aDatatype)) {
					return new Date(Long.parseLong(aLit.getLabel()));
				}

				if (Enum.class.isAssignableFrom(BeanReflectUtil.classFrom(mAccessor))) {
					Class clazz = BeanReflectUtil.classFrom(mAccessor);

					Object[] aEnums = clazz.getEnumConstants();
					for (Object aObj : aEnums) {
						if (((Enum) aObj).name().equals(aLit.getLabel())) {
							return aObj;
						}
					}

					LOGGER.info("{} maps to the enum {}, but does not correspond to any of the values of the enum.",
							aLit.getLabel(), clazz);

					return null;
				}

				// no idea what this value is from its data type.  if the field takes a string
				// we'll just assign the plain string, otherwise its an error
				throw new RuntimeException("Unsupported or unknown literal datatype");
			} else if (Enum.class.isAssignableFrom(BeanReflectUtil.classFrom(mAccessor))) {
				IRI aURI = (IRI) theValue;

				Class clazz = BeanReflectUtil.classFrom(mAccessor);

				Object[] aEnums = clazz.getEnumConstants();
				for (Object aObj : aEnums) {
					if (((Enum) aObj).name().equals(aURI.getLocalName())) {
						return aObj;
					}
				}

				LOGGER.info("{} maps to the enum {}, but does not correspond to any of the values of the enum.",
						aURI, clazz);

				return null;
			} else if (theValue instanceof BNode) {
				// TODO: this is not bulletproof, clean this up

				BNode aBNode = (BNode) theValue;

				// we need to figure out what type of bean this instance maps to.
				Class<?> aClass = BeanReflectUtil.classFrom(mAccessor);

				aClass = refineClass(mAccessor, aClass, mSource, aBNode);

				if (Collection.class.isAssignableFrom(BeanReflectUtil.classFrom(mAccessor))) {
					AccessibleObject aAccess = (AccessibleObject) mAccessor;
					RdfProperty aPropAnnotation = getRdfPropertyAnnotation(aAccess);

					// the field takes a collection, lets create a new instance of said collection, and hopefully the
					// bnode is a list.  this approach will only work if the property is a singleton value, eg
					// :inst someProperty _:a where _:a is the head of a list.  if you have another value _:b for
					// some property on :inst, we don't have any way of figuring out which one you're talking about
					// since bnode id references are not guaranteed to be stable in SPARQL, ie just because its id "a"
					// in the result set, does not mean i can do another query for _:a and get the expected results.
					// and you can't do a describe for the same reason.

					try {
						String aQuery = getBNodeConstructQuery(mSource, mResource, mProperty);

						Model aGraph = mSource.graphQuery(aQuery);

						Optional<Resource> aPossibleListHead = Models2.getResource(aGraph, mResource, mProperty);

						if (aPossibleListHead.isPresent() && Models2.isList(aGraph, aPossibleListHead.get())) {
							List<Value> aList;

							// getting the list is only safe the the query dialect supports stable bnode ids in the query language.
							if (aPropAnnotation != null && aPropAnnotation.isList() && mSource.getQueryFactory().getDialect().supportsStableBnodeIds()) {
								try {
									aList = asList(mSource, aPossibleListHead.get());
								} catch (DataSourceException e) {
									throw new RuntimeException(e);
								}
							} else {
								aList = new ArrayList<>(aGraph.filter(mResource, mProperty, null).objects());
							}

							Collection<Object> aValues = BeanReflectUtil.instantiateCollectionFromField(BeanReflectUtil.classFrom(aAccess));

							for (Value aValue : aList) {
								Object aListValue = null;

								try {
									aListValue = getProxyOrDbObject(mAccessor, aClass, aValue, mResource, mSource, mOMap);
								} catch (Exception e) {
									// we'll throw an error in a second...
								}

								if (aListValue == null) {
									throw new RuntimeException("Error converting a list value: " + aValue + " -> " + aClass);
								}

								aValues.add(aListValue);

							}

							return aValues;
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}

				try {
					return getProxyOrDbObject(mAccessor, aClass, aBNode, mResource, mSource, mOMap);
				} catch (Exception e) {
					if (EmpireOptions.STRICT_MODE) {
						throw new RuntimeException(e);
					} else {
						return null;
					}
				}
			} else if (theValue instanceof IRI) {
				IRI aURI = (IRI) theValue;
				try {
					// we need to figure out what type of bean this instance maps to.
					Class<?> aClass = BeanReflectUtil.classFrom(mAccessor);

					/* IT2Rail: if the bean is a org.eclipse.rdf4j.model.Resource then just return its IRI*/
					if (aClass.getCanonicalName().contains("org.eclipse.rdf4j.model.Resource")) {

						return aURI;
					}
					/* IT2RAIL */

					aClass = refineClass(mAccessor, aClass, mSource, aURI);


					if (aClass.isAssignableFrom(java.net.URI.class)) {
						// if (((AccessibleObject) mAccessor).isAnnotationPresent(XmlIDREF.class)) {
						Object stored = mOMap.get(theValue);
						return stored != null ? stored : java.net.URI.create(aURI.toString());

						// }
						// return java.net.URI.create(aURI.toString());
					} else {
						/* IT2RAIL if the bean is a List of org.eclipse.rdf4j.model.Resource then just return its IRI* */
						if (aClass.getCanonicalName().contains("org.eclipse.rdf4j.model.Resource"))
							return aURI;
						else {
							return getProxyOrDbObject(mAccessor, aClass, java.net.URI.create(aURI.toString()), mResource, mSource, mOMap);
						}
					}
				} catch (Exception e) {
					if (EmpireOptions.STRICT_MODE) {
						throw new RuntimeException(e);
					} else {
						LOGGER.warn("Problem applying value {}, {} ", e.toString(), e.getCause());
						return null;
					}
				}
			} else {
				if (EmpireOptions.STRICT_MODE) {
					throw new RuntimeException("Unexpected Value type");
				} else {
					LOGGER.warn("Problem applying value : Unexpected Value type");
					return null;
				}
			}
		}
	}

	private static List<Value> asList(DataSource theSource, Resource theRes) throws DataSourceException {
		List<Value> aList = Lists.newArrayList();

		Resource aListRes = theRes;

		while (aListRes != null) {

			Resource aFirst = (Resource) DataSourceUtil.getValue(theSource, aListRes, RDF.FIRST);
			Resource aRest = (Resource) DataSourceUtil.getValue(theSource, aListRes, RDF.REST);

			if (aFirst != null) {
				aList.add(aFirst);
			}

			if (aRest == null || aRest.equals(RDF.NIL)) {
				aListRes = null;
			} else {
				aListRes = aRest;
			}
		}

		return aList;
	}

	private static IRI iri(String qname) {
		return FACTORY.createIRI(PrefixMapping.GLOBAL.uri(qname));
	}

	private static final MethodFilter METHOD_FILTER = theMethod -> !theMethod.getName().equals("finalize");

	@SuppressWarnings("unchecked")
	private static <T> T getProxyOrDbObject(Object theAccessor, Class<T> theClass, Object theKey, Resource previousRes, DataSource theSource, Map<Object, Object> oMap) throws Exception {
		if (BeanReflectUtil.isFetchTypeLazy(theAccessor)) {
			eu.st4rt.converter.empire.annotation.runtime.Proxy<T> aProxy = new eu.st4rt.converter.empire.annotation.runtime.Proxy<T>(theClass, EmpireUtil.asPrimaryKey(theKey), theSource);

			ProxyFactory aFactory = new ProxyFactory();
			if (!theClass.isInterface()) {
				aFactory.setSuperclass(theClass);
				aFactory.setInterfaces(ObjectArrays.concat(theClass.getInterfaces(), EmpireGenerated.class));
			} else {
				aFactory.setInterfaces(ObjectArrays.concat(theClass, ObjectArrays.concat(theClass.getInterfaces(), EmpireGenerated.class)));
			}

			aFactory.setFilter(METHOD_FILTER);
			final ProxyHandler<T> aHandler = new ProxyHandler<T>(aProxy);

			Object aObj = aFactory.createClass(METHOD_FILTER).newInstance();

			((ProxyObject) aObj).setHandler(aHandler);

			return (T) aObj;
		} else {
			if (getRdfPropertyAnnotation((AccessibleObject) theAccessor) != null && theClass.isAnnotationPresent(RdfsClass.class)) {
				return fromRdf(theClass, EmpireUtil.asPrimaryKey(theKey), theSource, oMap);
			} else {
				return processClassAccessors(theClass, previousRes, theSource, oMap);
			}
		}
	}

	/**
	 * Javassist {@link MethodHandler} implementation for method proxying.
	 *
	 * @param <T> the proxy class type
	 */
	public static class ProxyHandler<T> implements MethodHandler {
		/**
		 * The proxy object which wraps the instance being proxied.
		 */
		private eu.st4rt.converter.empire.annotation.runtime.Proxy<T> mProxy;

		/**
		 * Create a new ProxyHandler
		 *
		 * @param theProxy the proxy object
		 */
		private ProxyHandler(final eu.st4rt.converter.empire.annotation.runtime.Proxy<T> theProxy) {
			mProxy = theProxy;
		}

		public Proxy<T> getProxy() {
			return mProxy;
		}

		/**
		 * Delegates the methods to the Proxy
		 *
		 * @inheritDoc
		 */
		public Object invoke(final Object theThis, final Method theMethod, final Method theProxyMethod, final Object[] theArgs) throws Throwable {

			return theMethod.invoke(mProxy.value(), theArgs);
		}
	}

	private static String getBNodeConstructQuery(DataSource theSource, Resource theRes, IRI theProperty) {
		Dialect aDialect = theSource.getQueryFactory().getDialect();

		String aSparqlQuery = "CONSTRUCT  { " + aDialect.asQueryString(theRes) + " <" + theProperty.toString() + "> ?o . ?o ?po ?oo  } \n" +
				"WHERE\n" +
				"{ " + aDialect.asQueryString(theRes) + " <" + theProperty.toString() + "> ?o.\n" +
				"?o ?po ?oo. }";


		// TODO: we're just assuming/hoping at this point that they support sparql.  which
		// will most likely be the case, but possibly not always.
		return aSparqlQuery;
	}

	public static class AsValueFunction implements Function<Object, Value> {
		private AccessibleObject mField;
		private RdfProperty annotation;

		public AsValueFunction() {
		}

		public AsValueFunction(final AccessibleObject theField) {
			mField = theField;
			annotation = mField == null ? null : getRdfPropertyAnnotation(mField);
		}

		public Value apply(final Object theIn) {
			if (theIn == null) {
				return null;
			}
			if (annotation != null && !StringUtils.isEmpty(annotation.datatype()) ) {
				try {
					final IRI aURI = FACTORY.createIRI(PrefixMapping.GLOBAL.uri(annotation.datatype()));

					return FACTORY.createLiteral(theIn.toString(), aURI);
				} catch (IllegalArgumentException e) {
					final String aMsg = String.format("An invalid uri \"%s\" was used, ignoring property with annotation", annotation.datatype());

					throw new RuntimeException(aMsg);
				}
			} else if (!EmpireOptions.STRONG_TYPING && BeanReflectUtil.isPrimitive(theIn)) {
				return FACTORY.createLiteral(theIn.toString());
			} else if (Boolean.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Boolean.class.cast(theIn));
			} else if (Integer.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Integer.class.cast(theIn));
			} else if (Long.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Long.class.cast(theIn));
			} else if (Short.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Short.class.cast(theIn));
			} else if (Double.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Double.class.cast(theIn));
			} else if (Float.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Float.class.cast(theIn));
			} else if (BigDecimal.class.isInstance(theIn)) {
				return FACTORY.createLiteral(BigDecimal.class.cast(theIn));
			} else if (BigInteger.class.isInstance(theIn)) {
				return FACTORY.createLiteral(BigInteger.class.cast(theIn));
			} else if (Date.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Dates.datetime(Date.class.cast(theIn)), XMLSchema.DATETIME);
			} else if (XMLGregorianCalendar.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Dates.datetime(((XMLGregorianCalendar) theIn).toGregorianCalendar().getTime()), XMLSchema.DATETIME);
			} else if (String.class.isInstance(theIn)) {
				if (annotation != null && !annotation.language().equals("")) {
					return FACTORY.createLiteral(String.class.cast(theIn), annotation.language());
				} else {
					return FACTORY.createLiteral(String.class.cast(theIn), XMLSchema.STRING);
				}
			} else if (Character.class.isInstance(theIn)) {
				return FACTORY.createLiteral(Character.class.cast(theIn));
			} else if (java.net.URI.class.isInstance(theIn)) {
				if (annotation != null && annotation.isXsdUri()) {
					return FACTORY.createLiteral(theIn.toString(), XMLSchema.ANYURI);
				} else {
					return FACTORY.createIRI(theIn.toString());
				}
			} else if (Enum.class.isAssignableFrom(theIn.getClass())) {
				return FACTORY.createLiteral(theIn.toString());
			} else if (Value.class.isAssignableFrom(theIn.getClass())) {
				return Value.class.cast(theIn);
			} else if (BeanReflectUtil.hasAnnotation(theIn.getClass(), RdfsClass.class)) {
				try {
					return id(theIn);
				} catch (InvalidRdfException e) {
					throw new RuntimeException(e);
				}
			} else if (theIn instanceof ProxyHandler) {
				return this.apply(((ProxyHandler) theIn).mProxy.value());
			} else {
				try {
					Field aProxy = theIn.getClass().getDeclaredField("handler");
					return this.apply(((ProxyHandler) BeanReflectUtil.safeGet(aProxy, theIn)).mProxy.value());
				} catch (Exception e) {
					throw new RuntimeException("Unknown type conversion: " + theIn.getClass() + " " + theIn + " " + mField);
				}
			}
		}
	}

	private static class ContainsResourceValues implements Predicate<Value> {
		public boolean test(final Value theValue) {
			return theValue instanceof Resource;
		}
	}

	private static class LanguageFilter implements Predicate<Value> {
		private String mLangCode;

		private LanguageFilter(final String theLangCode) {
			mLangCode = theLangCode;
		}

		public void setLangCode(final String theLangCode) {
			mLangCode = theLangCode;
		}

		public boolean test(final Value theValue) {
			return theValue instanceof Literal && mLangCode.equals(((Literal) theValue).getLanguage());
		}
	}

}
