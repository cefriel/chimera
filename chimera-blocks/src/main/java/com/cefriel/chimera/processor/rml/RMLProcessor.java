/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.processor.rml;

import java.io.InputStream;
import java.util.*;

import be.ugent.rml.store.RDF4JRemoteStore;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import com.cefriel.chimera.util.ProcessorConstants;

public class RMLProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RMLProcessor.class);

    private RMLOptions defaultRmlOptions;
    
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Map<String, InputStream> streamsMap = in.getBody(Map.class);

        processRML(streamsMap,exchange);
    }

    public void processRML(Map<String, InputStream> streamsMap, Exchange exchange) throws Exception {
        RDFGraph graph;
        graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);

        // RML Processor configuration
        RMLOptions rmlOptions = exchange.getIn().getHeader(ProcessorConstants.RML_CONFIG, RMLOptions.class);
        if (rmlOptions != null)
            exchange.getIn().removeHeader(ProcessorConstants.RML_CONFIG);
        else {
            rmlOptions = defaultRmlOptions;
            if (rmlOptions == null)
                throw new IllegalArgumentException("RMLOptions config should be provided in the header");
        }


        IRI context =  Utils.getContextIRI(exchange);
        Executor executor = RMLConfigurator.configure(graph, context, streamsMap, rmlOptions);

        if(executor != null) {
            RDF4JRemoteStore outputStore = (RDF4JRemoteStore) executor.execute(null);

            if (outputStore.isEmpty()) {
                logger.info("No results!");
                // Write even if no results
            }

            //Write quads to the context graph
            outputStore.writeToDB();
            outputStore.shutDown();
        }
    }

    public RMLOptions getDefaultRmlOptions() {
        return defaultRmlOptions;
    }

    public void setDefaultRmlOptions(RMLOptions defaultRmlOptions) {
        this.defaultRmlOptions = defaultRmlOptions;
    }

}
