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
import com.cefriel.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GraphAdd {

    private static final Logger LOG = LoggerFactory.getLogger(GraphAdd.class);

    private record EndpointParams(ChimeraResourcesBean ontologies) {}
    private record OperationParams(RDFGraph graph, Exchange exchange, EndpointParams operationParams) {}
    private static boolean validParams(OperationParams params) throws IllegalArgumentException {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");
        return true;
    }
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(operationConfig.getChimeraResources());
    }
    private static OperationParams getOperationParams(RDFGraph graph, Exchange exchange, GraphBean operationConfig) {
        return new OperationParams(
                graph,
                exchange,
                getEndpointParams(operationConfig));
    }
    public static void graphAdd(Exchange exchange, GraphBean operationConfig) throws Exception {
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        exchange.getMessage().setBody(graphAdd(graph, exchange, operationConfig), RDFGraph.class);
    }
    public static RDFGraph graphAdd(RDFGraph graph, Exchange exchange, GraphBean operationConfig) throws Exception {
        OperationParams params = getOperationParams(graph, exchange, operationConfig);
        if (validParams(params))
            return graphAdd(params);
        return null;
    }
    private static RDFGraph graphAdd(OperationParams params) throws Exception {
        if (validParams(params)){
            Utils.populateRepository(params.graph().getRepository(), params.operationParams().ontologies(), params.exchange());
            return params.graph();
        }
        throw new IllegalArgumentException("One or more parameters for the GraphAdd operation are invalid");
    }
}
