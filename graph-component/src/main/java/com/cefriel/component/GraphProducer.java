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

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.JsonDocument;
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
import java.util.ArrayList;
import java.util.List;

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
        GraphBean configuration;
        if (endpoint.getBaseConfig() != null){
            configuration = new GraphBean(endpoint.getBaseConfig());
            LOG.info("There is a base configuration");
        } else if( exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION)!=null ){
            configuration = new GraphBean(exchange.getMessage().getHeader(ChimeraConstants.BASE_CONFIGURATION, GraphBean.class));
            LOG.info("Configuration from exchange");
        } else {
            configuration = new GraphBean();
            LOG.info("No GraphBean detected");
        }
        configuration.setConfig(endpoint);
        exchange.getMessage().setHeader(ChimeraConstants.CONFIGURATION, configuration);
        switch (endpoint.getName()){
            case "config":
                exchange.getMessage().setHeader(ChimeraConstants.BASE_CONFIGURATION, endpoint.getBaseConfig());
                break;
            case "get":
                inputStream = exchange.getMessage().getBody(InputStream.class);
                Utils.setConfigurationRDFHeader(exchange, configuration.getRdfFormat());
                model = StreamParser.parse(inputStream, exchange);
                exchange.getMessage().removeHeader(ChimeraConstants.RDF_FORMAT);
                RDFGraph graph = GraphGet.graphCreate(exchange);
                GraphAdd.graphAdd(graph, model);
                exchange.getMessage().setBody(graph);
                break;
            case "add":
                Utils.setConfigurationRDFHeader(exchange, configuration.getRdfFormat());
                for (String path: configuration.getResources()) {
                    inputStream = UniLoader.open(path, exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class));
                    LOG.info("InputStream loaded from path " + path);
                    Rio.getParserFormatForFileName(path)
                            .ifPresent(format -> exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, format.getMIMETypes()));
                    model = StreamParser.parse(inputStream, exchange);
                    GraphAdd.graphAdd(exchange.getMessage().getBody(RDFGraph.class), model);
                }
                break;
            case "construct":
                GraphConstruct.graphConstruct(exchange);
                break;
            case "detach":
                GraphDetach.graphDetach(exchange);
                break;
            case "dump":
                GraphDump.graphDump(exchange);
                break;
            case "inference":
                GraphInference.graphInference(exchange);
                break;
            case "shacl":
                //TODO Add tests
                GraphShacl.graphShacl(exchange);
                break;
            case "frame":
                //TODO Add tests
                //TODO Get the graph as input, serialise as JSON-LD, apply the frame
                List<JsonDocument> doc = exchange.getMessage().getHeader("doc", List.class);
                exchange.getMessage().setBody(JsonLd.frame(doc.get(0), doc.get(1)).get());
                break;
            default:
                throw new NoSuchEndpointException("This endpoint doesn't exist");
        }
    }
}