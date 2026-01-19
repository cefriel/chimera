package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides operations for executing SPARQL ASK queries on RDF graphs.
 * <p>
 * This class handles the execution of SPARQL ASK queries, which test whether a query
 * pattern has at least one solution in the graph. The result is a boolean value
 * indicating whether the pattern matches. The query can be provided either as a
 * direct string parameter or loaded from an external resource.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 */
public class GraphSparqlAsk {
    private static final Logger LOG = LoggerFactory.getLogger(GraphSparqlAsk.class);

    /**
     * Executes a SPARQL ASK query on an RDF graph and returns the boolean result.
     * <p>
     * This method retrieves the RDF graph from the exchange, resolves the ASK query
     * from the configuration, executes it, and sets the boolean result as the exchange
     * message body. The query can be supplied in the following ways:
     * </p>
     * <ul>
     *   <li>Using the {@code query} configuration parameter as a direct string</li>
     *   <li>Using the {@code chimeraResource} configuration parameter to load from an external resource</li>
     * </ul>
     * <p>
     * If both are specified, the {@code query} string parameter takes priority.
     * At least one must be provided and the query cannot be null or blank.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph to query
     * @param config the configuration bean containing the query or resource location
     * @throws IllegalArgumentException if the query is null or blank
     * @throws Exception if the graph cannot be retrieved or query execution fails
     */
    public static void graphAsk(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);
        String query = Utils.resolveQuery(config.getQuery(), config.getChimeraResource(), exchange);

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("SPARQL query cannot be null or blank");
        }

        executeAskQuery(exchange, graph, query);
    }

    /**
     * Executes a SPARQL ASK query and sets the boolean result as the exchange message body.
     * <p>
     * This method prepares a boolean query from the provided query string, executes it
     * against the RDF graph's repository, and sets the result (true or false) as the
     * exchange message body with Boolean type.
     * </p>
     *
     * @param exchange the Camel exchange where the query result will be set
     * @param graph the RDF graph to query
     * @param query the SPARQL ASK query string to execute
     */
    private static void executeAskQuery(Exchange exchange, RDFGraph graph, String query) {
        Repository repo = graph.getRepository();

        try (RepositoryConnection connection = repo.getConnection()) {
            BooleanQuery booleanQuery = connection.prepareBooleanQuery(query);
            LOG.info("Executing sparql query...");
            boolean queryResult = booleanQuery.evaluate();
            exchange.getMessage().setBody(queryResult, Boolean.class);
        }

    }
}