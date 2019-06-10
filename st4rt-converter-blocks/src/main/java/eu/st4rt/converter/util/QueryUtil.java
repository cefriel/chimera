package eu.st4rt.converter.util;

import com.complexible.common.openrdf.util.ResourceBuilder;
import com.google.common.collect.Maps;

import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.annotation.xsdgenerated.QueriesType;
import eu.st4rt.converter.empire.annotation.xsdgenerated.QueryType;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.MutableDataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.ResultSet;
import eu.st4rt.converter.empire.impl.sparql.ARQSPARQLDialect;
import eu.st4rt.converter.empire.util.BeanReflectUtil;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import st4rt.convertor.empire.annotation.Queries;
import st4rt.convertor.empire.annotation.Query;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


public class QueryUtil {

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryUtil.class);

    public static Map<String, QueryType> loadQueriesFromFile() {

        try {
            InputStream is = QueryUtil.class.getResourceAsStream("/sparql_queries.xml");

            JAXBContext jaxbContext = JAXBContext.newInstance(QueriesType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            QueriesType queries = (QueriesType) JAXBIntrospector.getValue(jaxbUnmarshaller.unmarshal(is));

            return queries.getQuery().stream().collect(Collectors.toMap(x -> x.getName(), x -> x));
        } catch (Exception e) {
            LOGGER.error("sparql_queries.xml file not loaded");
            e.printStackTrace();
        }
        return Maps.newHashMap();
    }

    public static Query getQueryAnnotation(AccessibleObject aAccess, Query.QueryType queryType) {
        Query queryAnnotation = aAccess != null ? aAccess.getAnnotation(Query.class) : null;
        return ((queryAnnotation == null || !queryAnnotation.type().equals(queryType)) ? null : queryAnnotation);
    }

    public static List<Query> getClassQueryAnnotations(Class clazz, Query.QueryType queryType) {
        Queries aQueriesAnnotation = BeanReflectUtil.getAnnotation(clazz, Queries.class);
        if (aQueriesAnnotation == null || aQueriesAnnotation.value() == null) return null;

        LOGGER.info("1: Found @Queries annotation for class:" + clazz);
        Query[] classQueries = aQueriesAnnotation.value();

        return Arrays.stream(classQueries).filter(query -> query.type().equals(queryType)).collect(Collectors.toList());
    }

    /*
        URIs from with scheme name "_" (which is illegal) are created as blank node labels for directly accessing a blank node in the queried graph or dataset.
        This are constant terms in the query - not unnamed variables. Do not confuse these with the standard qname-like notation for blank nodes in queries.
        This is not portable - use with care.
    */
    private static String asQueryString(Value theValue) {
        return ARQSPARQLDialect.instance().asQueryString(theValue);
    }

    public static String findQueryByName(String name, Map<String, QueryType> queriesMap, Resource aRes) throws QueryException {
        QueryType query = queriesMap.get(name);

        if (query == null) {
            throw new QueryException("Query with name {" + name + "} not found in sparql_query.xml.");
        }

        if (StringUtils.isEmpty(query.getSparqlQuery())) {
            throw new QueryException("SPARQL is empty for query: " + name);
        }

        return query.getSparqlQuery().replace("??this", asQueryString(aRes));
    }

    public static Collection<Value> getSingleValueFromQuery(Map<String, QueryType> queriesMap, Query queryAnnotation, Resource aRes, DataSource theSource) {
        try {
            String queryName = queryAnnotation.name();
            String sparqlQuery = findQueryByName(queryAnnotation.name(), queriesMap, aRes);
            return getSingleValueFromQuery(queryAnnotation.outputs(), sparqlQuery, queryName, theSource)
                    .stream().limit(queryAnnotation.limit()).collect(Collectors.toList());
        } catch (QueryException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    private static Collection<Value> getSingleValueFromQuery(String[] outputs, String sparqlQuery, String queryName, DataSource theSource) {
        try {
            String output = outputs[0];
            ResultSet m = executeSelectQuery(new String[]{output}, sparqlQuery, queryName, theSource);
            return m.stream().map(theIn -> theIn.getValue(output)).collect(Collectors.toList());
        } catch (QueryException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public static HashMap<String, Collection<Value>> getMultipleValuesFromQuery(String sparqlQuery, Query queryAnnotation, DataSource theSource) throws QueryException {
        return getMultipleValuesFromQuery(queryAnnotation.outputs(), sparqlQuery, queryAnnotation.name(), theSource);
    }

    private static HashMap<String, Collection<Value>> getMultipleValuesFromQuery(String[] outputs, String sparqlQuery, String queryName, DataSource theSource) throws QueryException {
        if (outputs == null || outputs.length == 0) {
            throw new QueryException("Outputs are empty for select query: " + queryName);
        }

        HashMap<String, Collection<Value>> queryOutputs = new HashMap<>(outputs.length);

        ResultSet m = executeSelectQuery(outputs, sparqlQuery, queryName, theSource);

        if (m.hasNext()) {
            BindingSet bindingSet = m.next();

            for (String outputName : outputs) {
                Collection<Value> aValue = Arrays.asList(bindingSet.getValue(outputName));

                if (aValue == null || aValue.isEmpty()) {
                    LOGGER.info("Empty value for output {" + outputName + "} for multi-select {" + queryName + "} query.");
                    continue;
                }
                queryOutputs.put(outputName, aValue);
            }
        }
        return queryOutputs;
    }

    private static ResultSet executeSelectQuery(String[] outputs, String sparqlQuery, String queryName, DataSource theSource) throws QueryException {
        if (outputs == null || outputs.length == 0) {
            throw new QueryException("Outputs are empty for select query: " + queryName);
        }
        return theSource.selectQuery(sparqlQuery);
    }

    public static void executeConstructQuery(Map<String, QueryType> queriesMap, Set<Field> declaredFields, Query queryAnnotation, ResourceBuilder aRes, Object aObj, DataSource theSource) throws QueryException, InvocationTargetException {
        String sparqlQuery = findQueryByName(queryAnnotation.name(), queriesMap, aRes.getResource());
        executeConstructQuery(sparqlQuery, declaredFields, queryAnnotation.inputs(), aObj, aRes.model(), theSource);
    }

    public static void executeConstructQuery(String sparqlQuery, Set<Field> declaredFields, String[] inputs, Object aObj, Model model, DataSource theSource) throws QueryException, InvocationTargetException {
        if (inputs != null && declaredFields != null && aObj != null) {
            for (String annotationInput : inputs) {
                try {
                    Field field = BeanReflectUtil.getFieldByName(declaredFields, annotationInput, aObj == null ? Object.class : aObj.getClass());
                    Object value = BeanReflectUtil.safeGet(field, aObj);

                    if (value == null) {
                        continue;
                    } else if (Collection.class.isAssignableFrom(value.getClass())) {
                        //TODO: collection as input?
                        continue;
                    } else {
                        RdfGenerator.AsValueFunction aFunc = new RdfGenerator.AsValueFunction(field);
                        String input = asQueryString(aFunc.apply(value));
                        sparqlQuery = sparqlQuery.replace("??" + annotationInput, input);
                    }

                } catch (IllegalStateException e) {
                    //TODO: defined input not found -> exception or not?
                    LOGGER.warn(e.getMessage());
                }
            }
        }

        try {
            MutableDataSource src=(MutableDataSource)theSource;
            src.add(model);
            // construct
            if (sparqlQuery.toLowerCase().contains("construct")) {
                Model m = src.graphQuery(sparqlQuery);
                model.addAll(m);
                src.add(model);
            } else {
                // update
            	src.updateQuery(sparqlQuery);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static HashMap<String, Collection<Value>> executeQuery(Map<String, QueryType> queriesMap, Query queryAnnotation, Resource aRes, Model model, DataSource theSource) throws QueryException {
        String sparqlQuery = findQueryByName(queryAnnotation.name(), queriesMap, aRes);

        if (sparqlQuery.toLowerCase().contains("select")) {
            return getMultipleValuesFromQuery(sparqlQuery, queryAnnotation, theSource);
        } else {
            try {
                executeConstructQuery(sparqlQuery, null, null, null, model, theSource);
            } catch (InvocationTargetException e) {
                LOGGER.error(e.getMessage());
            }
            return Maps.newHashMap();
        }
    }
}
