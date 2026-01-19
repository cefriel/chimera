/*
 * Copyright (c) 2019-2022 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides operations for adding RDF triples to an existing RDF graph.
 * <p>
 * This class handles the addition of RDF data from external resources (files, URLs, etc.)
 * to an RDF graph repository. The data is loaded and parsed according to the RDF format
 * specified in the configuration, then added to the graph's repository.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see Utils#populateRepository(org.eclipse.rdf4j.repository.Repository, com.cefriel.util.ChimeraResourceBean, Exchange)
 */
public class GraphAdd {

    private static final Logger LOG = LoggerFactory.getLogger(GraphAdd.class);

    /**
     * Adds RDF triples from an external resource to the RDF graph in the exchange.
     * <p>
     * This method retrieves the RDF graph from the exchange, adds triples from the
     * resource specified in the configuration, and sets the modified graph back as
     * the exchange message body.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph to be modified
     * @param config the configuration bean containing the resource location to add
     *               (specified via {@code chimeraResource} property)
     * @throws Exception if the graph cannot be retrieved, the resource cannot be loaded,
     *                   or the RDF data is malformed
     */
    public static void graphAdd(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);
        RDFGraph result = graphAdd(graph, exchange, config);
        exchange.getMessage().setBody(result, RDFGraph.class);
    }

    /**
     * Adds RDF triples from an external resource to the specified RDF graph.
     * <p>
     * This method loads RDF data from the resource specified in the configuration and
     * adds it to the graph's underlying repository. The resource location is resolved
     * from the {@code chimeraResource} property in the configuration bean, which can
     * reference:
     * </p>
     * <ul>
     *   <li>File paths (absolute or relative)</li>
     *   <li>HTTP/HTTPS URLs</li>
     *   <li>Classpath resources</li>
     *   <li>Exchange properties or headers containing the resource location</li>
     * </ul>
     * <p>
     * The RDF format is automatically detected based on file extension or content type.
     * </p>
     *
     * @param graph the RDF graph to which triples will be added (must not be null)
     * @param exchange the Camel exchange providing context for resource resolution
     * @param config the configuration bean containing the resource location
     * @return the modified RDF graph with the added triples
     * @throws IllegalArgumentException if the graph parameter is null
     * @throws Exception if the resource cannot be loaded or the RDF data is malformed
     */
    public static RDFGraph graphAdd(RDFGraph graph, Exchange exchange, GraphBean config) throws Exception {
        if (graph == null) {
            throw new IllegalArgumentException("RDFGraph cannot be null");
        }
        Utils.populateRepository(graph.getRepository(), config.getChimeraResource(), exchange);
        return graph;
    }
}
