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

import be.ugent.rml.store.QuadStore;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import com.cefriel.chimera.util.RMLProcessorConstants;

public class RMLProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RMLProcessor.class);

    private RMLOptions defaultRmlOptions;
    
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Map<String, InputStream> streamsMap = in.getBody(Map.class);

        processRML(streamsMap, exchange);
    }

    public void processRML(Map<String, InputStream> streamsMap, Exchange exchange) throws Exception {
        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");

        // RML Processor configuration
        RMLOptions rmlOptions = exchange.getIn().getHeader(RMLProcessorConstants.RML_CONFIG, RMLOptions.class);
        if (rmlOptions != null)
            exchange.getIn().removeHeader(RMLProcessorConstants.RML_CONFIG);
        else {
            rmlOptions = defaultRmlOptions;
            if (rmlOptions == null)
                throw new IllegalArgumentException("RMLOptions config should be provided in the header");
        }

        String baseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
        String baseIRIPrefix = exchange.getMessage().getHeader(RMLProcessorConstants.PREFIX_BASE_IRI, String.class);
        if (baseIRI != null)
            rmlOptions.setBaseIRI(baseIRI);
        if (baseIRIPrefix != null)
            rmlOptions.setBaseIRIPrefix(baseIRIPrefix);

        IRI context =  graph.getContext();
        logger.info("MAP " + streamsMap.keySet());
        Executor executor = RMLConfigurator.configure(graph, context, streamsMap, rmlOptions);

        if(executor != null) {
            QuadStore outputStore = executor.execute(null);

            if (outputStore.isEmpty()) {
                logger.info("No results!");
                // Write even if no results
            }

            //Write quads to the context graph
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
