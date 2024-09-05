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

package com.cefriel.component;

import com.cefriel.graph.RDFGraph;
import com.cefriel.operations.*;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.support.DefaultProducer;
import org.eclipse.rdf4j.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class GraphProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(GraphProducer.class);
    private final GraphEndpoint endpoint;

    public GraphProducer(GraphEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }
    public void process(Exchange exchange) throws Exception {
        InputStream inputStream;
        Model model;
        GraphBean operationConfig;


        if( exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION)!=null ){
            // todo this is never called (check if true) (it is true, verified by all available tests)
            operationConfig = new GraphBean(exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION, GraphBean.class));
            LOG.info("Configuration from exchange");
        } else {
            operationConfig = new GraphBean();
            LOG.info("No GraphBean detected");
        }
        operationConfig.setEndpointParameters(endpoint);
        String operation;
        if (endpoint.getName() == null && endpoint.getOperation() != null)
            operation = endpoint.getOperation();
        else if (endpoint.getName() != null && endpoint.getOperation() == null)
            operation = endpoint.getName();
        else
            throw new IllegalArgumentException("The operation name must be specified either by the 'name' or 'operation' parameter.");

        operation = operation.toLowerCase();

        // todo see if this can be avoided and only have operationLocalConfig
        // this is done to propagate the configuration from one producer operation to the other
        exchange.getMessage().setHeader(ChimeraConstants.CONFIGURATION, operationConfig);


        switch (operation){
            case "config" -> exchange.getMessage().setHeader(ChimeraConstants.BASE_CONFIGURATION, endpoint.getBaseConfig());
            case "get" -> {
                inputStream = exchange.getMessage().getBody(InputStream.class);
                RDFGraph graph = GraphGet.obtainGraph(exchange, operationConfig, inputStream);
                exchange.getMessage().setBody(graph);
                }
            case "add" -> GraphAdd.graphAdd(exchange, operationConfig);
            case "construct" -> GraphConstruct.graphConstruct(exchange, operationConfig);
            case "ask" -> GraphSparqlAsk.graphAsk(exchange, operationConfig);
            case "select" -> GraphSparqlSelect.graphSparql(exchange, operationConfig);
            case "detach" -> GraphDetach.graphDetach(exchange, operationConfig);
            case "dump" -> GraphDump.graphDump(exchange, operationConfig);
            case "inference" -> GraphInference.graphInference(exchange, operationConfig);
            case "shacl" -> GraphShacl.graphShacl(exchange, operationConfig);
            default -> throw new NoSuchEndpointException("This endpoint does not exist");
        }
    }
}
