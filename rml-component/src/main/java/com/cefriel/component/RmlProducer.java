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

import be.ugent.rml.access.AccessFactory;
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.RDFGraph;
import com.cefriel.rml.CamelAccessFactory;
import com.cefriel.rml.RmlProcessor;
import com.cefriel.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RmlProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(RmlProducer.class);
    private final RmlEndpoint endpoint;

    public RmlProducer(RmlEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {

        // RML Processor configuration
        final RmlBean configuration;
        RDFGraph graph;
        if (endpoint.getRmlBaseConfig() != null) {
            configuration = new RmlBean(endpoint.getRmlBaseConfig());
        } else if (exchange.getMessage().getHeader(ChimeraRmlConstants.RML_BASECONFIG) != null) {
            configuration = new RmlBean(exchange.getMessage().getHeader(ChimeraRmlConstants.RML_BASECONFIG, RmlBean.class));
            LOG.info("Configuration from exchange");
        } else {
            configuration = new RmlBean();
        }
        configuration.setConfig(endpoint);
        String baseIRI = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
        String baseIRIPrefix = exchange.getMessage().getHeader(ChimeraRmlConstants.PREFIX_BASE_IRI, String.class);
        if (baseIRI != null)
            configuration.setBaseIri(baseIRI);
        if (baseIRIPrefix != null)
            configuration.setBaseIriPrefix(baseIRIPrefix);
        exchange.getMessage().setHeader(ChimeraRmlConstants.RML_CONFIG, configuration);

        if (exchange.getMessage().getBody(RDFGraph.class) == null) {
            if (exchange.getProperty(ChimeraConstants.GRAPH) == null) {
                graph = new MemoryRDFGraph(baseIRI);
                LOG.info("new Memory Graph");
            } else {
                graph = exchange.getProperty(ChimeraConstants.GRAPH, RDFGraph.class);
            }
        } else {
            graph = exchange.getMessage().getBody(RDFGraph.class);
            exchange.getMessage().setBody(null);
        }

        if (exchange.getMessage().getBody() == null) {
            LOG.info("Body is null");
            if (configuration.getInputFiles() == null) {
                String basePath = configuration.getBasePath();
                if (basePath == null)
                    basePath = System.getProperty("user.id");
                RmlProcessor.execute(exchange, new AccessFactory(basePath), graph);
            } else if (configuration.getInputFiles().getResources().size() == 1) {
                exchange.getMessage().setBody(ResourceAccessor.open(configuration.getInputFiles().getResources().get(0), exchange.getContext()));
                // exchange.getMessage().setBody(UniLoader.open(configuration.getInputFiles().getResources().get(0)));
            } else {
                MapConverter.fileConvert(exchange, configuration.getInputFiles());
            }
        }
        if (exchange.getMessage().getBody(Map.class) != null) {
            LOG.info("Body is map");
            RmlProcessor.execute(exchange, new CamelAccessFactory(exchange), graph);
        } else {
            if (configuration.isUseMessage()) {
                LOG.info("inputStream message");
                RmlProcessor.execute(exchange, new CamelAccessFactory(exchange, true), graph);
            } else {
                MapConverter.inputStreamConvert(exchange);
                LOG.info("Normal inputStream");
                RmlProcessor.execute(exchange, new CamelAccessFactory(exchange, false), graph);
            }
        }
        exchange.getMessage().setBody(graph);
    }
}
