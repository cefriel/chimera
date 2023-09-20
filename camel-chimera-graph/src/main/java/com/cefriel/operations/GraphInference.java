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
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ChimeraResourcesBean;
import com.cefriel.util.Utils;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class GraphInference {

    private static final Logger LOG = LoggerFactory.getLogger(GraphInference.class);
    private record EndpointParams(ChimeraResourcesBean ontologies, boolean allRules){}
    private record OperationParams(RDFGraph graph, Exchange exchange, EndpointParams endpointParams){}

    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getChimeraResources(),
                operationConfig.isAllRules());
    }
    private static OperationParams getOperationParams(Exchange e, GraphBean operationConfig) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                e,
                getEndpointParams(operationConfig));
    }

    public static void graphInference(Exchange exchange, GraphBean operationConfig) throws Exception {
        OperationParams operationParams = getOperationParams(exchange, operationConfig);
        graphInference(operationParams, exchange);
    }
    public static void graphInference(OperationParams params, Exchange exchange) throws Exception {
        Repository schema = Utils.createSchemaRepository(params.endpointParams().ontologies(), params.exchange());
        SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer(new MemoryStore(), schema, params.endpointParams().allRules());
        Repository inferenceRepo = new SailRepository(inferencer);
        inferenceRepo.init();

        Repository sourceRepo = params.graph().getRepository();
        Repository targetRepo = inferenceRepo;
        //Enable inference
        // all statements from source graph to graph with inference enabled

        Model sourceModel = new TreeModel();
        for (var st: sourceRepo.getConnection().getStatements(null, null, null, true))
        {
            sourceModel.add(st);
        }

        Utils.populateRepository(targetRepo, sourceModel);

        //Copy back
        Model targetModel = new TreeModel();
        for (var st: targetRepo.getConnection().getStatements(null, null, null, true))
        {
            targetModel.add(st);
        }

        Utils.populateRepository(sourceRepo, targetModel);

        exchange.getMessage().setBody(params.graph(), RDFGraph.class);
    }
}
