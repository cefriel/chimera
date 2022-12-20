package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.*;
import com.cefriel.util.*;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.Repository;

import java.io.IOException;
import java.io.InputStream;

public class GraphGet {
    private record HeaderParams(String namedGraph, String baseIRI, String rdfFormat) {}
    private record EndpointParams(String namedGraph, String baseIri, Boolean defaultGraph,
                                  String rdfFormat,
                                  ChimeraResourcesBean ontologies,
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
                operationConfig.getChimeraResources(),
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
                headerParams.namedGraph() != null ? headerParams.namedGraph() : endpointParams.namedGraph(),
                headerParams.baseIRI() != null ? headerParams.baseIRI() : endpointParams.baseIri(),
                endpointParams.defaultGraph(),
                headerParams.rdfFormat() != null ? headerParams.rdfFormat() : endpointParams.rdfFormat(),
                endpointParams.ontologies(),
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
        return (params.endpointParams().ontologies() != null) &&
                (params.endpointParams().ontologies().getResources().size() > 0);
    }
    private static Boolean isHTTPRDFGraph (OperationParams params) {
        return params.endpointParams().serverURL() != null &&
                params.endpointParams().repositoryId() != null;
    }
    private static Boolean isSPARQLEndpointGraph (OperationParams params) {
        return params.endpointParams().sparqlEndpoint() != null;
    }
    private record NamedGraphAndBaseIRI(String namedGraph, String baseIri) {}
    private static NamedGraphAndBaseIRI handleNamedGraphAndBaseIRI(String namedGraph, String baseIri, String graphID) {
        String returnNamedGraph, returnBaseIRI;

        returnBaseIRI = baseIri == null ? ChimeraConstants.DEFAULT_BASE_IRI : baseIri;
        returnNamedGraph = namedGraph == null ? returnBaseIRI + graphID : namedGraph;
        return new NamedGraphAndBaseIRI(returnNamedGraph, returnBaseIRI);
    }
    // called by the producer
    public static RDFGraph obtainGraph(Exchange exchange, GraphBean operationConfig, InputStream inputStream) throws IOException {
        OperationParams params = getOperationParams(exchange, operationConfig);
        return obtainGraph(params, exchange, inputStream);
    }
    private static RDFGraph obtainGraph(OperationParams params, Exchange exchange, InputStream inputStream) throws IOException {
        RDFGraph graph = obtainGraph(params, exchange);
        Utils.populateRepository(graph.getRepository(), inputStream, params.endpointParams().rdfFormat());
        return graph;
    }

    // called by the consumer
    public static void obtainGraph(Exchange exchange, GraphBean operationConfig) throws IOException {
        OperationParams params = getOperationParams(exchange, operationConfig);
        RDFGraph graph = obtainGraph(params, exchange);
        exchange.getMessage().setBody(graph, RDFGraph.class);
    }
    private static RDFGraph obtainGraph(OperationParams params, Exchange exchange) throws IOException {

        String namedGraph, baseIRI;
        if (!params.endpointParams().defaultGraph()) {
            NamedGraphAndBaseIRI x = handleNamedGraphAndBaseIRI(params.endpointParams().namedGraph(), params.endpointParams().baseIri(), params.graphID());
            namedGraph = x.namedGraph();
            baseIRI = x.baseIri();
        }
        else {
            namedGraph = null;
            baseIRI = handleNamedGraphAndBaseIRI(null, params.endpointParams().baseIri(), null).baseIri();
        }

        RDFGraph graph;
        if (isInferenceRDFGraph(params)) {
            Repository schema = Utils.createSchemaRepository(params.endpointParams().ontologies(), exchange.getContext());
            if (namedGraph != null && baseIRI != null)
                graph = new InferenceRDFGraph(schema, params.endpointParams().pathDataDir(), params.endpointParams().allRules(), namedGraph, baseIRI);
            else
                graph = new InferenceRDFGraph(schema, params.endpointParams().pathDataDir(), params.endpointParams().allRules());
        }

        else if (isHTTPRDFGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new HTTPRDFGraph(params.endpointParams().serverURL(), params.endpointParams().repositoryId(), namedGraph, baseIRI);
            else
                graph = new HTTPRDFGraph(params.endpointParams().serverURL(), params.endpointParams().repositoryId());
        }

        else if (isSPARQLEndpointGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new SPARQLEndpointGraph(params.endpointParams().sparqlEndpoint(), namedGraph, baseIRI);
            else
                graph = new SPARQLEndpointGraph(params.endpointParams().sparqlEndpoint());
        }

        else if (isNativeRDFGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new NativeRDFGraph(params.endpointParams().pathDataDir(), namedGraph, baseIRI);
            else
                graph = new NativeRDFGraph(params.endpointParams().pathDataDir());
        }

        else {
            if (namedGraph != null && baseIRI != null)
                graph = new MemoryRDFGraph(namedGraph, baseIRI);
            else
                graph = new MemoryRDFGraph();
        }

        return graph;
    }
}
