package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.query.BooleanQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
public class GraphAsk {
    private static final Logger LOG = LoggerFactory.getLogger(GraphAsk.class);
    private record EndpointParams (String literalQuery, ChimeraResourceBean resourceQuery) {
        EndpointParams(GraphBean operationConfig) {
            this(operationConfig.getQuery(),
                    operationConfig.getChimeraResource());
        }
        EndpointParams {
            if (literalQuery == null && resourceQuery == null)
                throw new NullPointerException("both sparql queries query cannot be null");
        }
    }

    private record OperationParams (Exchange exchange, RDFGraph graph, String query) {
        OperationParams(Exchange exchange, EndpointParams endpointParams) throws Exception {
            this(exchange,
                    exchange.getMessage().getBody(RDFGraph.class),
                    Utils.resolveQuery(endpointParams.literalQuery, endpointParams.resourceQuery, exchange));
        }
        OperationParams {
            Objects.requireNonNull(exchange, "Incoming exchange cannot be null");
            Objects.requireNonNull(graph, "RDFGraph cannot be null");

            if (query.isBlank())
                throw new IllegalArgumentException("SPARQL query cannot be blank");
        }
    }

    public static void graphAsk(Exchange exchange, GraphBean operationConfig) throws Exception {
        EndpointParams endpointParams = new EndpointParams(operationConfig);
        OperationParams operationParams = new OperationParams(exchange, endpointParams);
        graphAsk(exchange, operationParams.graph, operationParams.query);
    }

    private static void graphAsk(Exchange exchange, RDFGraph graph, String query) {
        Repository repo = graph.getRepository();
        RepositoryConnection connection = repo.getConnection();
        BooleanQuery booleanQuery = connection.prepareBooleanQuery(query);
        LOG.info("Executing sparql query...");

        boolean queryResult = booleanQuery.evaluate();
        exchange.getMessage().setBody(queryResult, Boolean.class);
    }
}