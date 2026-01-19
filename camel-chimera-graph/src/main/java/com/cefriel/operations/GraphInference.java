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
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides operations for performing RDFS inference on RDF graphs.
 * <p>
 * This class handles the materialization of inferred triples using RDFS (RDF Schema)
 * inference rules. It creates a temporary inference-enabled repository, applies the
 * inference rules to the source graph, and adds the inferred statements back to the
 * original graph. Optionally, a custom schema can be provided to guide the inference
 * process.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see SchemaCachingRDFSInferencer
 */
public class GraphInference {

    private static final Logger LOG = LoggerFactory.getLogger(GraphInference.class);

    /**
     * Performs RDFS inference on an RDF graph and adds inferred triples to the graph.
     * <p>
     * This method executes the following steps:
     * </p>
     * <ol>
     *   <li>Creates a temporary inference-enabled repository with optional custom schema</li>
     *   <li>Copies all statements from the source graph to the inference repository</li>
     *   <li>Allows the inferencer to materialize all inferred triples</li>
     *   <li>Copies both original and inferred statements back to the source graph</li>
     *   <li>Sets the enriched graph as the exchange message body</li>
     * </ol>
     * <p>
     * If a {@code chimeraResource} is specified in the configuration, it will be used
     * as the schema for inference. The {@code allRules} flag controls whether to apply
     * all RDFS rules or only a subset.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph to enrich with inferences
     * @param config the configuration bean specifying the inference schema and rules
     * @throws Exception if graph retrieval, inference execution, or statement copying fails
     */
    public static void graphInference(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);

        Repository inferenceRepo = createInferenceRepository(config, exchange);
        Repository sourceRepo = graph.getRepository();

        // Copy all statements from source to inference-enabled repository
        Model sourceModel = new TreeModel();
        sourceRepo.getConnection()
                .getStatements(null, null, null, true)
                .forEach(sourceModel::add);

        Utils.populateRepository(inferenceRepo, sourceModel);

        // Copy inferred statements back to source
        Model targetModel = new TreeModel();
        inferenceRepo.getConnection()
                .getStatements(null, null, null, true)
                .forEach(targetModel::add);

        Utils.populateRepository(sourceRepo, targetModel);

        exchange.getMessage().setBody(graph, RDFGraph.class);
    }

    /**
     * Creates an inference-enabled repository with optional custom schema.
     * <p>
     * This method constructs a {@link SchemaCachingRDFSInferencer} backed by an in-memory
     * store. If a {@code chimeraResource} is provided in the configuration, it loads
     * the schema from that resource and uses it to guide the inference process. The
     * {@code allRules} flag determines whether to apply all RDFS inference rules or
     * only a subset (e.g., excluding axiomatic triples).
     * </p>
     *
     * @param config the configuration bean containing optional schema resource and rules flag
     * @param exchange the Camel exchange providing context for resource resolution
     * @return an initialized inference-enabled repository
     * @throws Exception if schema loading or repository initialization fails
     */
    private static Repository createInferenceRepository(GraphBean config, Exchange exchange) throws Exception {
        Repository inferenceRepo;
        if (config.getChimeraResource() != null) {
            Repository schema = Utils.createSchemaRepository(config.getChimeraResource(), exchange);
            SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer(
                    new MemoryStore(), schema, config.isAllRules());
            inferenceRepo = new SailRepository(inferencer);
        } else {
            inferenceRepo = new SailRepository(new SchemaCachingRDFSInferencer(new MemoryStore()));
        }
        inferenceRepo.init();
        return inferenceRepo;
    }
}
