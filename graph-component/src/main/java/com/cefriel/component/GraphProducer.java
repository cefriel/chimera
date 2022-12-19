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
import com.cefriel.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.support.DefaultProducer;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class GraphProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(GraphProducer.class);
    private final GraphEndpoint endpoint;

    // todo wherever a resource like a file or a url use ChimeraResource
    // todo every operation will get the Camel Context from the Exchange that it receives

    public GraphProducer(GraphEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }
    public void process(Exchange exchange) throws Exception {
        InputStream inputStream;
        Model model;
        GraphBean operationConfig;


        if( exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION)!=null ){
            operationConfig = new GraphBean(exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION, GraphBean.class));
            LOG.info("Configuration from exchange");
        } else {
            operationConfig = new GraphBean();
            LOG.info("No GraphBean detected");
        }
        operationConfig.setEndpointParameters(endpoint);

        // todo see if this can be avoided and only have operationLocalConfig
        // this is done to propagate the configuration from one producer operation to the other
        exchange.getMessage().setHeader(ChimeraConstants.CONFIGURATION, operationConfig);

        switch (endpoint.getName()){
            case "config" -> exchange.getMessage().setHeader(ChimeraConstants.BASE_CONFIGURATION, endpoint.getBaseConfig());
            case "get" -> {
                // todo here i need to keep header format information
                inputStream = exchange.getMessage().getBody(InputStream.class);
                // Utils.setConfigurationRDFHeader(exchange, operationConfig.getRdfFormat());
                exchange.getMessage().removeHeader(ChimeraConstants.RDF_FORMAT);
                RDFGraph graph = GraphObtain.obtainGraph(exchange, operationConfig, inputStream);
                exchange.getMessage().setBody(graph);
                }
            case "add" -> GraphAdd.graphAdd(exchange, operationConfig);
            case "construct" -> GraphConstruct.graphConstruct(exchange, operationConfig);
            case "detach" -> GraphDetach.graphDetach(exchange, operationConfig);
            case "dump" -> GraphDump.graphDump(exchange, operationConfig);
            case "inference" -> GraphInference.graphInference(exchange, operationConfig);
            case "shacl" -> { //TODO Add tests
                GraphShacl.graphShacl(exchange, operationConfig);
            }
            default -> throw new NoSuchEndpointException("This endpoint does not exist");
        }
    }
}