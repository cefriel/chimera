package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraResourceBean;
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
import java.util.Objects;
import java.util.Set;

/**
 * Operation to perform SPARQL SELECT queries.
 *
 * A query can be supplied in the following ways:
 * <ul>
 *   <li>Using the 'query' endpoint parameter and passing the query as a String</li>
 *   <li>Using the 'chimeraResource' endpoint parameter and passing it in as a ChimeraResourceBean</li>
 * </ul>
 *
 * At least one of these queries must be not null. If both are specified then the 'query' String parameter has priority.
 *
 * Results from the query can be obtained in the outgoing exchange body in the following formats:
 * <ul>
 *   <li>json, a json formatted string</li>
 *   <li>csv, a csv formatted string</li>
 *   <li>xml, a xml formatted string</li>
 *   <li>tsv, a tsv formatted string</li>
 *   <li>memory, an RDF4J specific List&lt;BindingSet&gt;. This result can then, for example, be further processed by a custom Camel Processor.</li>
 * </ul>
 * *
 */
public class GraphSparql {
    private static final Logger LOG = LoggerFactory.getLogger(GraphSparql.class);
    private static final Set<String> validOutputFormats = Set.of("json", "csv", "xml", "tsv", "memory");
    private record EndpointParams (String literalQuery, ChimeraResourceBean resourceQuery, String outputFormat) {
        EndpointParams(GraphBean operationConfig) {
            this(operationConfig.getQuery(),
                    operationConfig.getChimeraResource(),
                    operationConfig.getDumpFormat());
        }
        EndpointParams {
            Objects.requireNonNull(outputFormat, "format for sparql query result must be specified using dumpFormat endpoint option");

            if (!validOutputFormats.contains(outputFormat))
                throw new IllegalArgumentException("unsupported format " + outputFormat + ", must be one of: " + validOutputFormats);

            if (literalQuery == null&& resourceQuery == null)
                throw new NullPointerException("both sparql queries query cannot be null");
        }
    }
    private record OperationParams (Exchange exchange, RDFGraph graph, String query, String outputFormat) {
        OperationParams(Exchange exchange, EndpointParams endpointParams) throws Exception {
            this(exchange,
                    exchange.getMessage().getBody(RDFGraph.class),
                    Utils.resolveQuery(endpointParams.literalQuery, endpointParams.resourceQuery, exchange),
                    endpointParams.outputFormat);
        }

        OperationParams {
            Objects.requireNonNull(exchange, "Incoming exchange cannot be null");
            Objects.requireNonNull(graph, "RDFGraph cannot be null");

            if (query.isBlank())
                throw new IllegalArgumentException("SPARQL query cannot be blank");
        }
    }
    public static void graphSparql(Exchange exchange, GraphBean operationConfig) throws Exception {
        EndpointParams endpointParams = new EndpointParams(operationConfig);
        OperationParams operationParams = new OperationParams(exchange, endpointParams);
        graphSparql(exchange, operationParams.graph, operationParams.query, operationParams.outputFormat);
    }
    private static void graphSparql (Exchange exchange, RDFGraph graph, String query, String outputFormat) {
        Repository repo = graph.getRepository();
        RepositoryConnection connection = repo.getConnection();
        TupleQuery tupleQuery = connection.prepareTupleQuery(query);
        LOG.info("Executing sparql query...");

        if (outputFormat.equals("memory")) {
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                // this is needed because TupleQueryResult is lazy and keeps a connection open to the repository.
                // we want to get the results and then not care about the repository.
                List<BindingSet> resultList = QueryResults.asList(result);
                exchange.getMessage().setBody(resultList, List.class);
            }
        }
        else {
            StringWriter stringWriter = new StringWriter();
            switch (outputFormat) {
                case "json" -> tupleQuery.evaluate(new SPARQLResultsJSONWriter(stringWriter));
                case "csv" -> tupleQuery.evaluate(new SPARQLResultsCSVWriter(stringWriter));
                case "xml" -> tupleQuery.evaluate(new SPARQLResultsXMLWriter(stringWriter));
                case "tsv" -> tupleQuery.evaluate(new SPARQLResultsTSVWriter(stringWriter));
                default -> throw new UnsupportedOperationException("cannot return SPARQL query result in format: " + outputFormat);
            }
            String result = stringWriter.toString();
            exchange.getMessage().setBody(result, String.class);
        }
    }
}
