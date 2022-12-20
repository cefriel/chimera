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
import com.cefriel.rdf.RdfTemplateProcessor;
import com.cefriel.template.io.Reader;
import com.cefriel.template.io.json.JSONReader;
import com.cefriel.template.io.rdf.RDFReader;
import com.cefriel.template.io.xml.XMLReader;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.RdfTemplateConstants;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdfTemplateProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(RdfTemplateProducer.class);
    private final RdfTemplateEndpoint endpoint;

    public RdfTemplateProducer(RdfTemplateEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {

        final RdfTemplateBean configuration;
        Reader reader;

        if (endpoint.getRdfBaseConfig() != null) {
            configuration = new RdfTemplateBean(endpoint.getRdfBaseConfig());
        } else if (exchange.getMessage().getHeader(RdfTemplateConstants.RDF_BASECONFIG) != null) {
            configuration = new RdfTemplateBean(exchange.getMessage().getHeader(RdfTemplateConstants.RDF_BASECONFIG, RdfTemplateBean.class));
            LOG.info("Configuration from exchange");
        } else {
            configuration = new RdfTemplateBean();
        }
        configuration.setConfig(endpoint);
        // either inputstream or chimera rdf graph
        switch (endpoint.getName()){
            case "rdf" :
                if (exchange.getMessage().getBody(RDFGraph.class) != null)
                    exchange.setProperty(ChimeraConstants.GRAPH, exchange.getMessage().getBody(RDFGraph.class));
                reader = configureRDFReader(exchange);
                RdfTemplateProcessor.execute(exchange, configuration, reader);
                break;
            case "xml" :
                reader = configureXMLReader(exchange);
                RdfTemplateProcessor.execute(exchange, configuration, reader);
                break;
            case "json" :
                reader = configureJSONReader(exchange);
                RdfTemplateProcessor.execute(exchange, configuration, reader);
                break;
            default:
                RdfTemplateProcessor.execute(exchange, configuration, null);
                break;
        }
    }

    private Reader configureJSONReader(Exchange exchange) throws Exception {

        Reader reader;

        reader = new JSONReader(exchange.getMessage().getBody(String.class));
        return reader;

    }

    private Reader configureXMLReader(Exchange exchange) throws Exception {

        Reader reader;

        reader = new XMLReader(exchange.getMessage().getBody(String.class));
        return reader;

    }

    private Reader configureRDFReader(Exchange exchange) throws Exception {

        RDFReader reader;
        RDFGraph graph = exchange.getProperty(ChimeraConstants.GRAPH, RDFGraph.class);

        if(graph != null)
            reader = new RDFReader(graph.getRepository(), graph.getNamedGraph());
        else {
            reader = new RDFReader();
            RDFFormat format = Utils.getExchangeRdfFormat(exchange, Exchange.CONTENT_TYPE);
            reader.addString(exchange.getMessage().getBody(String.class), format);
        }
        return reader;

    }
}
