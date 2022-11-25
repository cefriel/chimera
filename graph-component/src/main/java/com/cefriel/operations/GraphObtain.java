package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.*;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.StreamParser;
import com.cefriel.util.UniLoader;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GraphObtain {
    // todo ontologyFormat is a good candidate for a sum type, possible values may be Turtle | RDF | (possible types in chimera constants) ...
    // todo headers are propagated down the route, also the operationConfig (no)?
    private record HeaderParams(String namedGraph, String baseIRI, String rdfFormat) {}
    private record EndpointParams(String namedGraph, String baseIri, String rdfFormat, Boolean defaultGraph,
                                  String ontologyFormat,
                                  List<String> ontologyPaths,
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
                operationConfig.getRdfFormat(),
                operationConfig.isDefaultGraph(),
                operationConfig.getOntologyFormat(),
                operationConfig.getResources(),
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
                headerParams.rdfFormat() != null ?  headerParams.rdfFormat() : endpointParams.rdfFormat(),
                endpointParams.defaultGraph(),
                endpointParams.ontologyFormat(),
                endpointParams.ontologyPaths(),
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
        return (params.endpointParams().ontologyPaths() != null) &&
                (params.endpointParams().ontologyPaths().size() > 0) &&
                (params.endpointParams().rdfFormat() != null);
    }

    private static Boolean isHTTPRDFGraph (OperationParams params) {
        return params.endpointParams().serverURL() != null &&
                params.endpointParams().repositoryId() != null;
    }

    private static Boolean isSPARQLEndpointGraph (OperationParams params) {
        return params.endpointParams().sparqlEndpoint() != null;
    }

    // todo refactor and reuse the repo population functionality of the GraphAdd operation
    private static void populateRepository(Repository repo, Exchange exchange, String namedGraph, List<String> ontologyPaths, String jwtToken) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        try (RepositoryConnection con = repo.getConnection()) {
            for (String url: ontologyPaths) { // todo check why namespace is not handled here
                if (namedGraph != null)
                    con.add(StreamParser.parse(UniLoader.open(url, jwtToken), exchange), vf.createIRI(namedGraph));
                else
                    con.add(StreamParser.parse(UniLoader.open(url, jwtToken), exchange));
            }
        }
    }

    private static Repository createSchemaRepository(Exchange exchange, String namedGraph, List<String> ontologyPaths, String jwtToken) throws IOException {
        Repository schema = new SailRepository(new MemoryStore());
        schema.init();
        populateRepository(schema, exchange, namedGraph, ontologyPaths, jwtToken);
        return schema;
    }

    private record NamedGraphAndBaseIRI(String namedGraph, String baseIri) {}
    private static NamedGraphAndBaseIRI handleNamedGraphAndBaseIRI(String namedGraph, String baseIri, String graphID) {
        String returnNamedGraph, returnBaseIRI;

        returnBaseIRI = baseIri == null ? ChimeraConstants.DEFAULT_BASE_IRI : baseIri;
        returnNamedGraph = namedGraph == null ? returnBaseIRI + graphID : namedGraph;
        return new NamedGraphAndBaseIRI(returnNamedGraph, returnBaseIRI);
    }
    public record RDFGraphAndExchange (RDFGraph graph, Exchange exchange) {}
    // called by the producer
    public static RDFGraphAndExchange obtainGraph(Exchange exchange, GraphBean operationConfig, InputStream inputStream) throws IOException {
        OperationParams params = getOperationParams(exchange, operationConfig);
        return obtainGraph(params, exchange, inputStream);
    }

    private static RDFGraphAndExchange obtainGraph(OperationParams params, Exchange exchange, InputStream inputStream) throws IOException {
        RDFGraph graph = obtainGraph(params, exchange).graph();
        populateRepository(graph.getRepository(), exchange, params.endpointParams().namedGraph(), params.endpointParams().ontologyPaths(), params.jwtToken());
        return new RDFGraphAndExchange(graph, exchange);
    }

    // called by the consumer
    public static RDFGraphAndExchange obtainGraph(Exchange exchange, GraphBean operationConfig) throws IOException {
        OperationParams params = getOperationParams(exchange, operationConfig);
        return obtainGraph(params, exchange);
    }
    private static RDFGraphAndExchange obtainGraph(OperationParams params, Exchange exchange) throws IOException {

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
            Repository schema = createSchemaRepository(exchange, params.endpointParams().namedGraph(), params.endpointParams().ontologyPaths(), params.jwtToken());
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

        return new RDFGraphAndExchange(graph, exchange);
    }
}
