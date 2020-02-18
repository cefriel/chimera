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
package com.cefriel.chimera.processor;

import com.cefriel.chimera.graph.HTTPRDFGraph;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.graph.SPARQLEndpointGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cefriel.chimera.graph.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AttachGraph implements Processor {

    private Logger logger = LoggerFactory.getLogger(AttachGraph.class);

    private String rrAddress;
    private String repositoryId;
    private String sparqlEndpoint;

    private boolean context = true;

    public void process(Exchange exchange) throws Exception {
        RDFGraph graph;
        if (rrAddress != null && repositoryId != null) {
            logger.info("Connecting to remote repository " + repositoryId);
            graph = new HTTPRDFGraph(rrAddress, repositoryId);
        } else if (sparqlEndpoint != null) {
            graph = new SPARQLEndpointGraph(sparqlEndpoint);
        } else
            graph = new MemoryRDFGraph();
    	exchange.setProperty(ProcessorConstants.CONTEXT_GRAPH, graph);

    	if(context)
    	    exchange.setProperty(ProcessorConstants.CONTEXT_ID, exchange.getExchangeId());
    }

    public String getRrAddress() {
        return rrAddress;
    }

    public void setRrAddress(String rrAddress) {
        this.rrAddress = rrAddress;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public boolean isContext() {
        return context;
    }

    public void setContext(boolean context) {
        this.context = context;
    }

}