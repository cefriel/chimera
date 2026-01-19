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
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Provides operations for executing SPARQL CONSTRUCT queries on RDF graphs.
 * <p>
 * This class handles the execution of CONSTRUCT queries against an RDF graph and
 * manages the resulting triples, either by adding them to the existing graph or
 * creating a new graph. It also supports adding the constructed triples to specific
 * named graphs within the repository.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 */
public class GraphConstruct {

    private static final Logger LOG = LoggerFactory.getLogger(GraphConstruct.class);

    /**
     * Executes a SPARQL CONSTRUCT query on an RDF graph and processes the results.
     * <p>
     * This method retrieves the RDF graph from the exchange, resolves the CONSTRUCT query
     * from the configuration, executes the query, and sets the resulting graph as the
     * exchange message body. The behavior is controlled by the {@link GraphBean} configuration:
     * </p>
     * <ul>
     *   <li>If {@code newGraph} is true, results are added to a new graph instance</li>
     *   <li>If {@code newGraph} is false, results are added to the existing graph</li>
     *   <li>If {@code namedGraph} is specified, results are added to the specified named graph(s)</li>
     * </ul>
     *
     * @param exchange the Camel exchange containing the RDF graph and configuration
     * @param config the configuration bean containing query details, named graph settings,
     *               and whether to create a new graph
     * @throws Exception if the graph cannot be retrieved, the query is invalid, or execution fails
     */
    public static void graphConstruct(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);

        String query = Utils.resolveQuery(config.getQuery(), config.getChimeraResource(), exchange);
        RDFGraph result = executeQueryOnGraph(graph, query, config.getNamedGraph(), config.isNewGraph());

        exchange.getMessage().setBody(result, RDFGraph.class);
    }

    /**
     * Executes a SPARQL CONSTRUCT query on the given RDF graph and manages the result triples.
     * <p>
     * This method performs the following operations:
     * </p>
     * <ol>
     *   <li>Executes the CONSTRUCT query against the provided graph</li>
     *   <li>Creates a new graph or reuses the existing one based on the {@code newGraph} flag</li>
     *   <li>Adds the constructed triples to the target graph:
     *     <ul>
     *       <li>If {@code namedGraphs} is specified, adds triples to each named graph (semicolon-separated)</li>
     *       <li>Otherwise, adds triples to the default graph</li>
     *     </ul>
     *   </li>
     * </ol>
     *
     * @param graph the source RDF graph to query
     * @param query the SPARQL CONSTRUCT query string to execute
     * @param namedGraphs optional semicolon-separated list of named graph URIs where results should be added;
     *                    if null, results are added to the default graph
     * @param newGraph if true, creates a new {@link MemoryRDFGraph} for the results;
     *                 if false, adds results to the existing graph
     * @return the RDF graph containing the constructed triples (either new or the modified input graph)
     */
    private static RDFGraph executeQueryOnGraph(RDFGraph graph, String query, String namedGraphs, boolean newGraph) {
        Model constructResult = Repositories.graphQuery(graph.getRepository(), query, QueryResults::asModel);
        RDFGraph resultGraph = newGraph ? new MemoryRDFGraph() : graph;

        try (RepositoryConnection con = resultGraph.getRepository().getConnection()) {
            if (namedGraphs != null) {
                List<IRI> namedGraphsList = Arrays.stream(namedGraphs.split(";")).map(Utils::stringToIRI).toList();
                for (IRI namedGraph : namedGraphsList) {
                    con.add(constructResult, namedGraph);
                    LOG.info("Added {} triples to {}", constructResult.size(), namedGraph);
                }
            } else {
                Utils.populateRepository(resultGraph.getRepository(), constructResult);
                LOG.info("Added {} triples", constructResult.size());
            }
        }

        return resultGraph;
    }
}
