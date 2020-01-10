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
package com.cefriel.chimera.processor.rdf4j.rml;

import java.io.InputStream;
import java.util.*;

import be.ugent.rml.store.RDF4JRemoteStore;
import com.cefriel.chimera.context.RDFGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import com.cefriel.chimera.util.ProcessorConstants;

public class RMLProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RMLProcessor.class);

    private RMLOptions rmlOptions;
    private Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();
    private String label;
    
    public void process(Exchange exchange) throws Exception {
        RDFGraph repo;
        Message in = exchange.getIn();
        streamsMap = in.getBody(Map.class);
        
        // RML Processor configuration
        if (rmlOptions ==null)
            rmlOptions = exchange.getProperty(ProcessorConstants.RML_CONFIG, RMLOptions.class);

        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);

        String context = ProcessorConstants.BASE_CONVERSION_IRI
                + exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
        Executor executor = RMLConfigurator.configure(repo, context, streamsMap, rmlOptions);

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

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

    public Map<String, InputStream> getStreamsMap() {
        return streamsMap;
    }

    public void setStreamsMap(Map<String, InputStream> streamsMap) {
        this.streamsMap = streamsMap;
    }

    public RMLOptions getRmlOptions() {
        return rmlOptions;
    }

    public void setRmlOptions(RMLOptions rmlOptions) {
        this.rmlOptions = rmlOptions;
    }
    
}
