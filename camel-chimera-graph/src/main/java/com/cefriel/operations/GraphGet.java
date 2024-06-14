package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.*;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.Repository;

import java.io.InputStream;

public class GraphGet {
    private record HeaderParams(String namedGraphs, String baseIRI, String rdfFormat) {}
    private record EndpointParams(String namedGraphs, String baseIri, Boolean defaultGraph,
                                  String rdfFormat,
                                  ChimeraResourceBean triples,
                                  // HTTPRDF specific parameters
                                  String serverURL,
                                  String repositoryId,
                                  // SPARQL ENDPOINT specific parameter
                                  String sparqlEndpoint,
                                  // Native and Inference data dir
                                  String pathDataDir,
                                  // InferenceRDFGraph parameter
                                  Boolean allRules) {}

    private record OperationParams(String jwtToken, String graphID, EndpointParams endpointParams) {}

    //these params come from the header
    private static HeaderParams getHeaderParams(Exchange e) {
        return new HeaderParams(
                e.getMessage().getHeader(ChimeraConstants.CONTEXT_GRAPH, String.class),
                e.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class),
                e.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class));
    }
    // these params come from the endpoint
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getNamedGraph(),
                operationConfig.getBaseIri(),
                operationConfig.isDefaultGraph(),
                operationConfig.getRdfFormat(),
                operationConfig.getChimeraResource(),
                operationConfig.getServerUrl(),
                operationConfig.getRepositoryID(),
                operationConfig.getSparqlEndpoint(),
                operationConfig.getPathDataDir(),
                operationConfig.isAllRules());
    }
    private static OperationParams getOperationParams(Exchange e, GraphBean operationConfig) {
        String graphID = e.getMessage().getHeader(ChimeraConstants.GRAPH_ID, String.class);

        return new OperationParams(
                e.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class),
                graphID == null ? e.getExchangeId() : graphID,
                mergeHeaderParams(getHeaderParams(e), getEndpointParams(operationConfig)));
    }
    private static EndpointParams mergeHeaderParams(HeaderParams headerParams, EndpointParams endpointParams) {
        return new EndpointParams(
                headerParams.namedGraphs() != null ? headerParams.namedGraphs() : endpointParams.namedGraphs(),
                headerParams.baseIRI() != null ? headerParams.baseIRI() : endpointParams.baseIri(),
                endpointParams.defaultGraph(),
                headerParams.rdfFormat() != null ? headerParams.rdfFormat() : endpointParams.rdfFormat(),
                endpointParams.triples(),
                endpointParams.serverURL(),
                endpointParams.repositoryId(),
                endpointParams.sparqlEndpoint(),
                endpointParams.pathDataDir(),
                endpointParams.allRules());
    }
    private static Boolean isNativeRDFGraph (OperationParams params) {
        return params.endpointParams().pathDataDir() != null;
    }
    private static Boolean isInferenceRDFGraph (OperationParams params) {
        return (params.endpointParams().triples() != null);
    }
    private static Boolean isHTTPRDFGraph (OperationParams params) {
        return params.endpointParams().serverURL() != null &&
                params.endpointParams().repositoryId() != null;
    }
    private static Boolean isSPARQLEndpointGraph (OperationParams params) {
        return params.endpointParams().sparqlEndpoint() != null;
    }

    private static boolean validateParams(OperationParams params) {
        return true;
    }
    // called by the producer
    public static RDFGraph obtainGraph(Exchange exchange, GraphBean operationConfig, InputStream inputStream) throws Exception {
        OperationParams params = getOperationParams(exchange, operationConfig);
        if (validateParams(params)) {
            return obtainGraph(params, exchange, inputStream);
        }
        else {
            throw new IllegalArgumentException("Invalid parameters supplied to GraphGET operation");
        }

    }
    private static RDFGraph obtainGraph(OperationParams params, Exchange exchange, InputStream inputStream) throws Exception {
        RDFGraph graph = obtainGraph(params, exchange);
        Utils.populateRepository(graph.getRepository(), inputStream, params.endpointParams().rdfFormat());
        return graph;
    }

    // called by the consumer
    public static void obtainGraph(Exchange exchange, GraphBean operationConfig) throws Exception {
        OperationParams params = getOperationParams(exchange, operationConfig);
        if (validateParams(params)) {
            RDFGraph graph = obtainGraph(params, exchange);
            exchange.getMessage().setBody(graph, RDFGraph.class);
        }
        else {
            throw new IllegalArgumentException("Invalid parameters supplied to GraphGet operation");
        }
    }
    private static RDFGraph obtainGraph(OperationParams params, Exchange exchange) throws Exception {

        String namedGraphs;
        String baseIRI = params.endpointParams().baseIri != null ? params.endpointParams().baseIri() : ChimeraConstants.DEFAULT_BASE_IRI;
        if(params.endpointParams().defaultGraph()) {
            namedGraphs = null;
        }

        else {
            if (params.endpointParams().namedGraphs() != null) {
                namedGraphs = params.endpointParams().namedGraphs();
            }

            else {
                namedGraphs = ChimeraConstants.DEFAULT_BASE_IRI + params.graphID();
            }
        }
        RDFGraph graph;
        if (isInferenceRDFGraph(params)) {
            Repository schema = Utils.createSchemaRepository(params.endpointParams().triples(), exchange);
            graph = new InferenceRDFGraph(schema, params.endpointParams().pathDataDir(), params.endpointParams().allRules(), namedGraphs, baseIRI);
        }

        else if (isHTTPRDFGraph(params)) {
            graph = new HTTPRDFGraph(params.endpointParams().serverURL(), params.endpointParams().repositoryId(), namedGraphs, baseIRI);
        }

        else if (isSPARQLEndpointGraph(params)) {
            graph = new SPARQLEndpointGraph(params.endpointParams().sparqlEndpoint(), namedGraphs, baseIRI);
        }

        else if (isNativeRDFGraph(params)) {
            graph = new NativeRDFGraph(params.endpointParams().pathDataDir(), namedGraphs, baseIRI);
        }

        else {
            graph = new MemoryRDFGraph(namedGraphs, baseIRI);
        }

        return graph;
    }
}
