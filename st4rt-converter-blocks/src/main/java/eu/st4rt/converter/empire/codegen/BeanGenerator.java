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

package eu.st4rt.converter.empire.codegen;

import com.complexible.common.openrdf.model.Statements;
import com.complexible.common.openrdf.repository.Repositories;
import com.complexible.common.openrdf.util.AdunaIterations;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eu.st4rt.converter.empire.util.EmpireUtil;
import eu.st4rt.converter.empire.util.Repositories2;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Generate a set of Java beans which are compatible with Empire from a given RDF schema, OWL ontology, or blob
 * of RDF data.  The generated source code will map to the domain represented in the RDF.</p>
 *
 * @author	Michael Grove
 * @since	0.5.1
 * @version	1.0
 */
public final class BeanGenerator {
	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanGenerator.class);

	/**
	 * String URI constant for the owl:Thing conccept
	 */
	private static final IRI OWL_THING = SimpleValueFactory.getInstance().createIRI(OWL.NAMESPACE + "Thing");

	/**
	 * The list of xsd datatypes which map to Integer
	 */
	private static final List<IRI> integerTypes = Arrays.asList(XMLSchema.INT, XMLSchema.INTEGER, XMLSchema.POSITIVE_INTEGER,
														   XMLSchema.NEGATIVE_INTEGER, XMLSchema.NON_NEGATIVE_INTEGER,
														   XMLSchema.NON_POSITIVE_INTEGER, XMLSchema.UNSIGNED_INT);

	/**
	 * The list of xsd datatypes which map to Long
	 */
	private static final List<IRI> longTypes = Arrays.asList(XMLSchema.LONG, XMLSchema.UNSIGNED_LONG);

	/**
	 * The list of xsd datatypes which map to Float
	 */
	private static final List<IRI> floatTypes = Arrays.asList(XMLSchema.FLOAT, XMLSchema.DECIMAL);

	/**
	 * The list of xsd datatypes which map to Short
	 */
	private static final List<IRI> shortTypes = Arrays.asList(XMLSchema.SHORT, XMLSchema.UNSIGNED_SHORT);

	/**
	 * The list of xsd datatypes which map to Byte
	 */
	private static final List<IRI> byteTypes = Arrays.asList(XMLSchema.BYTE, XMLSchema.UNSIGNED_BYTE);

	private static final Map<Resource, String> NAMES = new HashMap<>();
	private static final Map<String, Integer> NAMES_TO_COUNT = new HashMap<>();

	/**
	 * NO instances
	 */
	private BeanGenerator() {
	}

	/**
	 * Return the Java bean source code that represents the given RDF class
	 * @param thePackageName the name of the package the source will be in
	 * @param theGraph the repository containing information about the class
	 * @param theClass the class that is to be turned into Java source
	 * @param theMap the map of classes to the properties in their domain
	 * @return a string of the source code of the equivalent Java bean
	 * @throws Exception if there is an error while converting
	 */
	private static String toSource(final String thePackageName, final Repository theGraph, final Resource theClass, final Map<Resource, Collection<IRI>> theMap) throws Exception {
		StringBuffer aSrc = new StringBuffer();

		aSrc.append("package ").append(thePackageName).append(";\n\n");

		aSrc.append("import java.util.*;\n");
		aSrc.append("import javax.persistence.Entity;\n");
		aSrc.append("import st4rt.convertor.empire.SupportsRdfId;\n");
		aSrc.append("import st4rt.convertor.empire.annotation.*;\n\n");

		// TODO: more imports? less?

		Stream<Resource> aSupers = Iterations.stream(Repositories.getStatements(theGraph, theClass, RDFS.SUBCLASSOF, null))
		                                     .map(Statement::getObject)
		                                     .filter(theValue -> theValue instanceof Resource)
		                                     .map(theValue -> (Resource) theValue);

		aSrc.append("@Entity\n");
		aSrc.append("@RdfsClass(\"").append(theClass).append("\")\n");
		aSrc.append("public interface ").append(className(theClass));

		aSupers = aSupers.filter(theValue -> theValue != null
		                                     && !theValue.toString().startsWith(OWL.NAMESPACE)
		                                     && !theValue.toString().startsWith(RDFS.NAMESPACE)
		                                     && !theValue.toString().startsWith(RDF.NAMESPACE));

		boolean aNeedsComma = false;

		aSrc.append(Stream.concat(Stream.of("SupportsRdfId"),
		                          aSupers.map(BeanGenerator::className))
		                  .collect(Collectors.joining(", ", " extends ", "")));

		aSrc.append(" { \n\n");

		Collection<IRI> aProps = props(theClass, theMap);

		for (IRI aProp : aProps) {
			aSrc.append("@RdfProperty(\"").append(aProp).append("\")\n");
			aSrc.append("public ").append(functionType(theGraph, aProp)).append(" get").append(functionName(aProp)).append("();\n");
			aSrc.append("public void set").append(functionName(aProp)).append("(").append(functionType(theGraph, aProp)).append(" theValue);\n\n");
		}

		aSrc.append("}");

		return aSrc.toString();
	}

	/**
	 * Return the type of the function (getter & setter), i.e. the bean property type, for the given rdf:Property
	 * @param theRepo the graph of the ontology/data
	 * @param theProp the property
	 * @return the String representation of the property type
	 * @throws Exception if there is an error querying the data
	 */
	private static String functionType(final Repository theRepo, final IRI theProp) throws Exception {
		String aType;

		Resource aRangeRes = null;
		RepositoryResult<Statement> result = Repositories.getStatements(theRepo, theProp, RDFS.RANGE, null);
		Statement optional = AdunaIterations.singleResult(result).orElse(null);
		if(optional != null)
		     aRangeRes = Statements.objectAsResource().apply(optional).orElse(null);
		
	//	Resource aRangeRes = Statements.objectAsResource().apply(AdunaIterations.singleResult(Repositories.getStatements(theRepo, theProp, RDFS.RANGE, null)).orElse(null)).orElse(null);

		if (aRangeRes instanceof BNode) {
			// we can't handle bnodes very well, so we're just going to assume Object
			return "Object";
		}

		IRI aRange = (IRI) aRangeRes;

		if (aRange == null) {
			// no explicit range, try to infer it...
			try {
				TupleQueryResult aResults = Repositories.selectQuery(theRepo, QueryLanguage.SERQL, "select distinct r from {s} <"+theProp+"> {o}, {o} rdf:type {r}");

				if (aResults.hasNext()) {
					IRI aTempRange = (IRI) aResults.next().getValue("r");
					if (!aResults.hasNext()) {
						aRange = aTempRange;
					}
					else {
						// TODO: leave range as null, the property is used for things of multiple different values.  so here
						// we should try and find the superclass of all the values and use that as the range.
					}
				}

				aResults.close();

				if (aRange == null) {
					// could not get it from type usage, so maybe its a literal and we can guess it from datatype

					aResults = Repositories.selectQuery(theRepo, QueryLanguage.SERQL, "select distinct datatype(o) as dt from {s} <"+theProp+"> {o} where isLiteral(o)");

					if (aResults.hasNext()) {
						IRI aTempRange = null;
						while (aTempRange == null && aResults.hasNext()) {
							Literal aLit = (Literal) aResults.next().getValue("o");
							if (aLit != null){
								aTempRange = aLit.getDatatype();
							}
						}
						
						if (!aResults.hasNext()) {
							aRange = aTempRange;
						}
						else {
							// TODO: do something here, literals of multiple types used
						}
					}

					aResults.close();
				}
			}
			catch (Exception e) {
				// don't worry about it
				e.printStackTrace();
			}
		}

		if (XMLSchema.STRING.equals(aRange) || RDFS.LITERAL.equals(aRange)) {
			aType = "String";
		}
		else if (XMLSchema.BOOLEAN.equals(aRange)) {
			aType = "Boolean";
		}
		else if (integerTypes.contains(aRange)) {
			aType = "Integer";
		}
		else if (longTypes.contains(aRange)) {
			aType = "Long";
		}
		else if (XMLSchema.DOUBLE.equals(aRange)) {
			aType = "Double";
		}
		else if (floatTypes.contains(aRange)) {
			aType = "Float";
		}
		else if (shortTypes.contains(aRange)) {
			aType = "Short";
		}
		else if (byteTypes.contains(aRange)) {
			aType = "Byte";
		}
		else if (XMLSchema.ANYURI.equals(aRange)) {
			aType = "java.net.URI";
		}
		else if (XMLSchema.DATE.equals(aRange) || XMLSchema.DATETIME.equals(aRange)) {
			aType = "Date";
		}
		else if (XMLSchema.TIME.equals(aRange)) {
			aType = "Date";
		}
		else if (aRange == null || aRange.equals(OWL_THING)) {
			aType = "Object";
		}
		else {
			aType = className(aRange);
		}

		if (isCollection(theRepo, theProp)) {
			aType = "Collection<? extends " + aType + ">";
		}

		return aType;
	}

	/**
	 * Determine whether or not the property's range is a collection.  This will inspect both the ontology, for cardinality
	 * restrictions, and when that is not available, it will use the actual structure of the data.
	 * @param theRepo the graph of the ontology/data
	 * @param theProp the property
	 * @return true if the property has a collection as it's value, false if it's just a single valued property
	 * @throws Exception if there is an error querying the data
	 */
	private static boolean isCollection(final Repository theRepo, final IRI theProp) throws Exception {
		// TODO: this is not fool proof.

		String aCardQuery = "select distinct ?card where {\n" +
					   "?s rdf:type owl:Restriction.\n" +
					   "?s owl:onProperty <"+theProp+">.\n" +
					   "?s ?cardProp ?card.\n" +
					   "FILTER (?cardProp = owl:cardinality || ?cardProp = owl:minCardinality || ?cardProp = owl:maxCardinality)\n" +
					   "}";

		TupleQueryResult aResults = Repositories.selectQuery(theRepo, QueryLanguage.SPARQL ,aCardQuery);
		if (aResults.hasNext()) {
			Literal aCard = (Literal) aResults.next().getValue("card") ;

			try {
				return Integer.parseInt(aCard.getLabel()) > 1;
			}
			catch (NumberFormatException e) {
				LOGGER.error("Unparseable cardinality value for '" + theProp + "' of '" + aCard + "'", e);
			}
		}

		aResults.close();

		try {
			aResults = Repositories.selectQuery(theRepo, QueryLanguage.SPARQL, "select distinct ?s where  { ?s <"+theProp+"> ?o}");
			return Iterations.stream(aResults)
			                 .flatMap(theBinding -> Iterations.stream(Repositories.getStatements(theRepo, (Resource) theBinding.getValue("s"), theProp, null)).map(Statement::getObject))
			                 .findFirst().isPresent();
		}
		finally {
			aResults.close();
		}
	}

	/**
	 * Return the name of the function (the bean property) for this rdf:Property
	 * @param theProp the rdf:Property
	 * @return the name of the Java property/function name
	 */
	private static String functionName(final IRI theProp) {
		return className(theProp);
	}

	/**
	 * Return all the properties for the given resource.  This will return only the properties which are directly
	 * associated with the class, not any properties from its parent, or otherwise inferred from the data.
	 * @param theRes the resource
	 * @param theMap the map of resources to properties
	 * @return a collection of the proeprties associated with the class
	 */
	private static Collection<IRI> props(final Resource theRes, final Map<Resource, Collection<IRI>> theMap) {
		Collection<IRI> aProps = new HashSet<>();

		if (theMap.containsKey(theRes)) {
			aProps.addAll(theMap.get(theRes));
		}

		return aProps;
	}

	/**
	 * Given a Resource, return the Java class name for that resource
	 * @param theClass the resource
	 * @return the name of the Java class
	 */
	private static String className(Resource theClass) {
		if (NAMES.containsKey(theClass)) {
			return NAMES.get(theClass);
		}

		String aLabel;

		if (theClass instanceof IRI) {
			aLabel = ((IRI) theClass).getLocalName();
		}
		else {
			aLabel = theClass.stringValue();
		}

		aLabel = String.valueOf(aLabel.charAt(0)).toUpperCase() + aLabel.substring(1);

		aLabel = aLabel.replaceAll(" ", "");

		if (NAMES_TO_COUNT.containsKey(aLabel)) {
			String aNewLabel = aLabel + NAMES_TO_COUNT.get(aLabel);

			NAMES_TO_COUNT.put(aLabel, NAMES_TO_COUNT.get(aLabel)+1);

			aLabel = aNewLabel;
		}
		else {
			NAMES_TO_COUNT.put(aLabel, 0);
		}

		NAMES.put(theClass, aLabel);

		return aLabel;
	}

	/**
	 * Given an ontology/schema, generate Empire compatible Java beans for each class in the ontology.
	 * @param thePackageName the name of the packages the source should belong to
	 * @param theOntology the location of the ontology to load
	 * @param theFormat the RDF format the ontology is in
	 * @param theDirToSave where to save the generated source code
	 * @throws Exception if there is an error while generating the source
	 */
	public static void generateSourceFiles(String thePackageName, URL theOntology, RDFFormat theFormat, File theDirToSave) throws Exception {
		NAMES_TO_COUNT.clear();

		Repository aRepository = Repositories2.createInMemoryRepo();

		Repositories.add(aRepository, theOntology.openStream(), theFormat);

		Collection<Resource> aClasses = Stream.concat(Iterations.stream(Repositories.getStatements(aRepository, null, RDF.TYPE, RDFS.CLASS)),
		                                              Iterations.stream(Repositories.getStatements(aRepository, null, RDF.TYPE, OWL.CLASS)))
		                                      .map(Statement::getSubject)
		                                      .filter(theRes -> theRes instanceof IRI)
		                                      .collect(Collectors.toSet());

		Collection<Resource> aIndClasses = Iterations.stream(Repositories.getStatements(aRepository, null, RDF.TYPE, null))
		                                             .map(Statement::getObject)
		                                             .filter(theValue -> theValue instanceof Resource)
		                                             .map(theValue -> (Resource) theValue)
		                                             .collect(Collectors.toSet());
		aClasses.addAll(aIndClasses);

		aClasses = aClasses.stream().filter(theValue ->
			                                    !theValue.stringValue().startsWith(RDFS.NAMESPACE)
			                                    && !theValue.stringValue().startsWith(RDF.NAMESPACE)
			                                    && !theValue.stringValue().startsWith(OWL.NAMESPACE))
		                   .collect(Collectors.toSet());

		Map<Resource, Collection<IRI>> aMap = new HashMap<>();

		for (Resource aClass : aClasses) {
			if (aClass instanceof BNode) { continue; }
			Collection<IRI> aProps = Iterations.stream(Repositories.getStatements(aRepository, null, RDFS.DOMAIN, aClass))
			                                   .map(Statement::getSubject) //getting subject instead of object
			                                   .filter(theObj -> theObj instanceof IRI)
			                                   .map(theObj -> (IRI) theObj)
			                                   .collect(Collectors.toSet());

			// infer properties based on usage in actual instance data
			/*
			Iterations.stream(Repositories.selectQuery(aRepository, QueryLanguage.SPARQL, "select distinct ?p where { ?s rdf:type <" + aClass + ">. ?s ?p ?o }"))
			          .map(theBindingSet -> theBindingSet.getValue("p"))
			          .filter(theValue -> !RDF.TYPE.equals(theValue))
			          .filter(theObj -> theObj instanceof IRI)
			          .map(theObj -> (IRI) theObj)
			          .forEach(aProps::add);
			 */
			Iterations.stream(Repositories.selectQuery(aRepository, QueryLanguage.SPARQL, "select distinct ?p where { ?p rdfs:domain <" + aClass + ">.} "))
	          .map(theBindingSet -> theBindingSet.getValue("p"))
	          .filter(theValue -> !RDF.TYPE.equals(theValue))
	          .filter(theObj -> theObj instanceof IRI)
	          .map(theObj -> (IRI) theObj)
	          .forEach(aProps::add);
	         
			aMap.put(aClass, aProps);
			 
		}

		if (!theDirToSave.exists()) {
			if (!theDirToSave.mkdirs()) {
				throw new IOException("Could not create output directory");
			}
		}

		for (Resource aClass :  aMap.keySet()) {
			String aSrc = toSource(thePackageName, aRepository, aClass, aMap);

			if (aSrc == null) {
				continue;
			}

			File aFile = new File(theDirToSave, className(aClass) + ".java");

			System.out.println("Writing source to file: " + aFile.getName());

			Files.write(aSrc, aFile, Charsets.UTF_8);
		}
	}

	public static void main(String[] args) throws Exception {
		//aGraph.read(new URL("http://xmlns.com/foaf/spec/index.rdf").openStream());
//		File aOut = new File("/Users/mhgrove/work/GitHub/empire/core/src/empire.codegen/test/");
//
//		generateSourceFiles("empire.codegen.test", new File("test/data/nasa.nt").toURI().toURL(), RDFFormat.NTRIPLES, aOut);

		if (args.length < 4) {
			System.err.println("Must provide three arguments to the program, the package name, ontology URL, and the output directory for the source code.\n");
			System.err.println("For example:\n");
			System.err.println("\tBeanGenerator my.package.domain /usr/local/files/myontology.ttl /usr/local/code/src/my/package/domain");

			return;
		}

		URL aURL;

		if (EmpireUtil.isURI(args[1])) {
			aURL = new URL(args[1]);
		}
		else {
			aURL = new File(args[1]).toURI().toURL();
		}

		generateSourceFiles(args[0], aURL, Rio.getParserFormatForFileName(args[1]).orElse(null), new File(args[2]));
	}
}
