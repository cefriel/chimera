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
import java.util.List;

public class GraphObtain {

    record GraphObtainHeaderParams (String namedGraph, String baseIRI, String rdfFormat, String jwtToken, String graphId) {}
    record GraphObtainParams (String namedGraph, String baseIri, String rdfFormat, Boolean defaultGraph,
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
                              Boolean allRules,
                              // hack, this should be a header only param (and it is)
                              // instead of defining a new record type which has all header and non-header params
                              // for simplicity the jwt token is added here
                              String jwtToken,
                              // same situation as jwtToken
                              String graphID) {}

    //these params come from the header
    private static GraphObtainHeaderParams exchangeToGraphObtainHeaderParams(Exchange e) {
        return new GraphObtainHeaderParams(
                e.getMessage().getHeader(ChimeraConstants.CONTEXT_GRAPH, String.class),
                e.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class),
                e.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class),
                e.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class),
                e.getMessage().getHeader(ChimeraConstants.GRAPH_ID, String.class));
    }

    // these params come from the endpoint
    private static GraphObtainParams configToGraphObtainParams(GraphBean baseConfig) {
        return new GraphObtainParams(
                baseConfig.getNamedGraph(),
                baseConfig.getBaseIri(),
                baseConfig.getRdfFormat(),
                baseConfig.isDefaultGraph(),
                baseConfig.getOntologyFormat(),
                baseConfig.getResources(),
                baseConfig.getServerUrl(),
                baseConfig.getRepositoryID(),
                baseConfig.getSparqlEndpoint(),
                baseConfig.getPathDataDir(),
                baseConfig.isAllRules(),
                // the jwt token is (possibly) in the header of the exchange
                null,
                null);
    }

    private static GraphObtainParams mergeHeaderParams(GraphObtainHeaderParams headerParams, GraphObtainParams graphObtainParams) {
        return new GraphObtainParams(
                headerParams.namedGraph() != null ? headerParams.namedGraph() : graphObtainParams.namedGraph(),
                headerParams.baseIRI() != null ? headerParams.baseIRI() : graphObtainParams.baseIri(),
                headerParams.rdfFormat() != null ?  headerParams.rdfFormat() : graphObtainParams.rdfFormat(),
                graphObtainParams.defaultGraph(),
                graphObtainParams.ontologyFormat(),
                graphObtainParams.ontologyPaths(),
                graphObtainParams.serverURL(),
                graphObtainParams.repositoryId(),
                graphObtainParams.sparqlEndpoint(),
                graphObtainParams.pathDataDir(),
                graphObtainParams.allRules(),
                headerParams.jwtToken(),
                headerParams.graphId());

    }

    private static Boolean isNativeRDFGraph (GraphObtainParams params) {
        return params.pathDataDir != null;
    }
    private static Boolean isInferenceRDFGraph (GraphObtainParams params) {
        return (params.ontologyPaths != null) &&
                (params.ontologyPaths.size() > 0) &&
                (params.rdfFormat != null);
    }

    private static Boolean isHTTPRDFGraph (GraphObtainParams params) {
        return params.serverURL != null &&
                params.repositoryId != null;
    }

    private static Boolean isSPARQLEndpointGraph (GraphObtainParams params) {
        return params.sparqlEndpoint != null;
    }

    // todo refactor and reuse the repo population functionality of the GraphAdd operation
    private static void populateRepository(Repository repo, Exchange exchange, String namedGraph, List<String> ontologyPaths, String jwtToken) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        try (RepositoryConnection con = repo.getConnection()) {
            for (String url: ontologyPaths) {
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
        if (namedGraph != null && baseIri != null) {
            returnNamedGraph = namedGraph;
            returnBaseIRI = baseIri;
        }
        else if (namedGraph == null && baseIri != null) {
            returnBaseIRI = baseIri;
            returnNamedGraph = returnBaseIRI + graphID;
        }
        else {
            returnBaseIRI = ChimeraConstants.DEFAULT_BASE_IRI;
            returnNamedGraph = returnBaseIRI + graphID;
        }

        return new NamedGraphAndBaseIRI(returnNamedGraph, returnBaseIRI);
    }
    public record RDFGraphAndExchange (RDFGraph graph, Exchange exchange) {}
    public static RDFGraphAndExchange obtainGraph(Exchange exchange, GraphBean operationConfiguration) throws IOException {
        GraphObtainParams params = mergeHeaderParams(exchangeToGraphObtainHeaderParams(exchange), configToGraphObtainParams(operationConfiguration));
        return obtainGraph(params, exchange);
    }
    public static RDFGraphAndExchange obtainGraph(GraphObtainParams params, Exchange exchange) throws IOException {

        String namedGraph, baseIRI;
        if (!params.defaultGraph()) {
            NamedGraphAndBaseIRI x = handleNamedGraphAndBaseIRI(params.namedGraph(), params.baseIri(), params.graphID());
            namedGraph = x.namedGraph();
            baseIRI = x.baseIri();
        }
        else {
            namedGraph = null;
            baseIRI = null;
        }

        RDFGraph graph;
        Exchange returnExchange = exchange.copy();

        // set header params for the exchange which will be forwarded down the route
        if (params.rdfFormat() != null)
            returnExchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, params.rdfFormat);
        if (params.graphID() != null)
            returnExchange.getMessage().setHeader(ChimeraConstants.GRAPH_ID, params.graphID());

        if (isInferenceRDFGraph(params)) {
            Repository schema = createSchemaRepository(exchange, params.namedGraph(), params.ontologyPaths, params.jwtToken());
            if (namedGraph != null && baseIRI != null)
                graph = new InferenceRDFGraph(schema, params.pathDataDir(), params.allRules(), namedGraph, baseIRI);
            else
                graph = new InferenceRDFGraph(schema, params.pathDataDir(), params.allRules());
        }

        if (isHTTPRDFGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new HTTPRDFGraph(params.serverURL(), params.repositoryId(), namedGraph, baseIRI);
            else
                graph = new HTTPRDFGraph(params.serverURL(), params.repositoryId());
        }

        if (isSPARQLEndpointGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new SPARQLEndpointGraph(params.sparqlEndpoint(), namedGraph, baseIRI);
            else
                graph = new SPARQLEndpointGraph(params.sparqlEndpoint());
        }

        if (isNativeRDFGraph(params)) {
            if (namedGraph != null && baseIRI != null)
                graph = new NativeRDFGraph(params.pathDataDir(), namedGraph, baseIRI);
            else
                graph = new NativeRDFGraph(params.pathDataDir());
        }

        if (namedGraph != null && baseIRI != null)
            graph = new MemoryRDFGraph(namedGraph, baseIRI);
        else
            graph = new MemoryRDFGraph();

        return new RDFGraphAndExchange(graph, returnExchange);
    }
}
