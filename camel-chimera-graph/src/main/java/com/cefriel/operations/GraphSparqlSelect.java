package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.List;
import java.util.Set;

/**
 * Provides operations for executing SPARQL SELECT queries on RDF graphs.
 * <p>
 * This class handles the execution of SPARQL SELECT queries, which retrieve variable
 * bindings matching a query pattern. Results can be returned in various serialization
 * formats (JSON, CSV, XML, TSV) or as an in-memory RDF4J data structure. The query
 * can be provided either as a direct string parameter or loaded from an external resource.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see BindingSet
 */
public class GraphSparqlSelect {
    private static final Logger LOG = LoggerFactory.getLogger(GraphSparqlSelect.class);
    private static final Set<String> VALID_OUTPUT_FORMATS = Set.of("json", "csv", "xml", "tsv", "memory");

    /**
     * Executes a SPARQL SELECT query on an RDF graph and returns the results in the specified format.
     * <p>
     * This method retrieves the RDF graph from the exchange, resolves the SELECT query
     * from the configuration, executes it, and sets the results as the exchange message body.
     * The query can be supplied in the following ways:
     * </p>
     * <ul>
     *   <li>Using the {@code query} configuration parameter as a direct string</li>
     *   <li>Using the {@code chimeraResource} configuration parameter to load from an external resource</li>
     * </ul>
     * <p>
     * If both are specified, the {@code query} string parameter takes priority.
     * At least one must be provided and the query cannot be null or blank.
     * </p>
     * <p>
     * Results are serialized according to the {@code dumpFormat} configuration parameter:
     * </p>
     * <ul>
     *   <li>{@code json} - SPARQL JSON format as a string</li>
     *   <li>{@code csv} - CSV format as a string</li>
     *   <li>{@code xml} - SPARQL XML format as a string</li>
     *   <li>{@code tsv} - TSV format as a string</li>
     *   <li>{@code memory} (default) - In-memory List&lt;BindingSet&gt; data structure</li>
     * </ul>
     *
     * @param exchange the Camel exchange containing the RDF graph to query
     * @param config the configuration bean containing the query, resource location, and output format
     * @throws IllegalArgumentException if the query is null/blank or the output format is invalid
     * @throws Exception if the graph cannot be retrieved or query execution fails
     */
    public static void graphSparql(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);
        String query = Utils.resolveQuery(config.getQuery(), config.getChimeraResource(), exchange);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("SPARQL query cannot be null or blank");
        }

        String outputFormat = config.getDumpFormat() != null ? config.getDumpFormat() : "memory";
        if (!VALID_OUTPUT_FORMATS.contains(outputFormat)) {
            throw new IllegalArgumentException("Unsupported format " + outputFormat + ", must be one of: " + VALID_OUTPUT_FORMATS);
        }

        executeSelectQuery(exchange, graph, query, outputFormat);
    }

    /**
     * Executes a SPARQL SELECT query and serializes the results in the specified format.
     * <p>
     * This method prepares a tuple query from the provided query string, executes it
     * against the RDF graph's repository, and sets the results as the exchange message body.
     * The result type depends on the output format:
     * </p>
     * <ul>
     *   <li>{@code memory} - Returns a List&lt;BindingSet&gt; object</li>
     *   <li>All other formats - Returns a serialized String</li>
     * </ul>
     *
     * @param exchange the Camel exchange where the query results will be set
     * @param graph the RDF graph to query
     * @param query the SPARQL SELECT query string to execute
     * @param outputFormat the desired output format (json, csv, xml, tsv, or memory)
     * @throws UnsupportedOperationException if an invalid output format is provided
     */
    private static void executeSelectQuery(Exchange exchange, RDFGraph graph, String query, String outputFormat) {
        Repository repo = graph.getRepository();

        try (RepositoryConnection connection = repo.getConnection()) {
            TupleQuery tupleQuery = connection.prepareTupleQuery(query);
            LOG.info("Executing sparql query...");

            if (outputFormat.equals("memory")) {
                try (TupleQueryResult result = tupleQuery.evaluate()) {
                    List<BindingSet> resultList = QueryResults.asList(result);
                    exchange.getMessage().setBody(resultList, List.class);
                }
            } else {
                StringWriter stringWriter = new StringWriter();
                switch (outputFormat) {
                    case "json" -> tupleQuery.evaluate(new SPARQLResultsJSONWriter(stringWriter));
                    case "csv" -> tupleQuery.evaluate(new SPARQLResultsCSVWriter(stringWriter));
                    case "xml" -> tupleQuery.evaluate(new SPARQLResultsXMLWriter(stringWriter));
                    case "tsv" -> tupleQuery.evaluate(new SPARQLResultsTSVWriter(stringWriter));
                    default -> throw new UnsupportedOperationException("Cannot return SPARQL query result in format: " + outputFormat);
                }
                exchange.getMessage().setBody(stringWriter.toString(), String.class);
            }
        }
    }

}
