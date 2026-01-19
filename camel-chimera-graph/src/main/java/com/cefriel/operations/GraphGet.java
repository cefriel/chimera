package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.*;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.Repository;

import java.io.InputStream;

/**
 * Provides operations for creating and initializing RDF graphs.
 * <p>
 * This class handles the creation of various RDF graph implementations based on
 * configuration parameters. It supports multiple backend types including in-memory graphs,
 * native persistent stores, HTTP triple stores, SPARQL endpoints, and inference-enabled
 * graphs. The class is used by both producers (to obtain graphs with data) and consumers
 * (to create empty graphs for subsequent operations).
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see MemoryRDFGraph
 * @see NativeRDFGraph
 * @see HTTPRDFGraph
 * @see SPARQLEndpointGraph
 * @see InferenceRDFGraph
 */
public class GraphGet {

    /**
     * Creates an RDF graph and populates it with data from an input stream.
     * <p>
     * This method is called by producers to obtain a graph initialized with data.
     * The RDF format for parsing the input stream is determined by checking the
     * {@code ChimeraConstants.RDF_FORMAT} header first, then falling back to the
     * configuration's {@code rdfFormat} parameter.
     * </p>
     *
     * @param exchange the Camel exchange providing context for graph creation
     * @param config the configuration bean specifying the graph type and parameters
     * @param inputStream the input stream containing RDF data to load into the graph
     * @return the created RDF graph populated with the input data
     * @throws Exception if graph creation or data loading fails
     */
    public static RDFGraph obtainGraph(Exchange exchange, GraphBean config, InputStream inputStream) throws Exception {
        RDFGraph graph = createGraph(exchange, config);
        String headerRdfFormatValue = exchange.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class);
        String rdfFormat = ParameterUtils.resolveParam(headerRdfFormatValue, config.getRdfFormat());
        Utils.populateRepository(graph.getRepository(), inputStream, rdfFormat);
        return graph;
    }

    /**
     * Creates an empty RDF graph and sets it as the exchange message body.
     * <p>
     * This method is called by consumers to create an empty graph instance that
     * will be populated by subsequent operations in the route. The graph type
     * is determined by the configuration parameters.
     * </p>
     *
     * @param exchange the Camel exchange where the created graph will be set as the message body
     * @param config the configuration bean specifying the graph type and parameters
     * @throws Exception if graph creation fails
     */
    public static void obtainGraph(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = createGraph(exchange, config);
        exchange.getMessage().setBody(graph, RDFGraph.class);
    }

    /**
     * Creates the appropriate RDF graph implementation based on configuration parameters.
     * <p>
     * This method determines which graph implementation to instantiate based on the
     * configuration bean properties, checked in the following priority order:
     * </p>
     * <ol>
     *   <li>If {@code chimeraResource} is set: creates an {@link InferenceRDFGraph} with RDFS inference</li>
     *   <li>If {@code serverUrl} and {@code repositoryID} are set: creates an {@link HTTPRDFGraph} for remote triple stores</li>
     *   <li>If {@code sparqlEndpoint} is set: creates a {@link SPARQLEndpointGraph} for SPARQL-only access</li>
     *   <li>If {@code pathDataDir} is set: creates a {@link NativeRDFGraph} with persistent disk storage</li>
     *   <li>Otherwise: creates a {@link MemoryRDFGraph} with in-memory storage</li>
     * </ol>
     *
     * @param exchange the Camel exchange providing context for graph creation and resource resolution
     * @param config the configuration bean containing graph type and connection parameters
     * @return the created RDF graph instance
     * @throws Exception if graph creation or resource loading fails
     */
    private static RDFGraph createGraph(Exchange exchange, GraphBean config) throws Exception {
        String namedGraph = ParameterUtils.resolveNamedGraph(exchange, config);
        String baseIRI = ParameterUtils.resolveBaseIri(exchange, config);

        if (config.getChimeraResource() != null) {
            Repository schema = Utils.createSchemaRepository(config.getChimeraResource(), exchange);
            return new InferenceRDFGraph(schema, config.getPathDataDir(), config.isAllRules(), namedGraph, baseIRI);
        }
        if (config.getServerUrl() != null && config.getRepositoryID() != null) {
            return new HTTPRDFGraph(config.getServerUrl(), config.getRepositoryID(), namedGraph, baseIRI);
        }
        if (config.getSparqlEndpoint() != null) {
            return new SPARQLEndpointGraph(config.getSparqlEndpoint(), namedGraph, baseIRI);
        }
        if (config.getPathDataDir() != null) {
            return new NativeRDFGraph(config.getPathDataDir(), namedGraph, baseIRI);
        }
        return new MemoryRDFGraph(namedGraph, baseIRI);
    }
}
