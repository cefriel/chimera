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

import com.cefriel.chimera.graph.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cefriel.chimera.util.ProcessorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AttachGraph implements Processor {

    private Logger logger = LoggerFactory.getLogger(AttachGraph.class);

    private String rrAddress;
    private String repositoryId;
    private String pathDataDir;
    private String sparqlEndpoint;

    private boolean context = true;

    public void process(Exchange exchange) throws Exception {
        RDFGraph graph = null;
        boolean attached = false;
        if (rrAddress != null && repositoryId != null) {
            logger.info("Connecting to remote repository " + repositoryId);
            graph = new HTTPRDFGraph(rrAddress, repositoryId);
            attached = true;
        }
        if (sparqlEndpoint != null) {
            logger.info("Connecting to SPARQL endpoint " + sparqlEndpoint);
            graph = new SPARQLEndpointGraph(sparqlEndpoint);
            if (attached)
                logger.warn("Multiple graphs provided! SPARQL endpoint attached, others have been discarded!");
            attached = true;
        }
        if (pathDataDir != null) {
            logger.info("Creating Native RDF4J store with path " + pathDataDir);
            graph = new NativeRDFGraph(pathDataDir);
            if (attached)
                logger.warn("Multiple graphs provided! Native Graph attached, others have been discarded!");
        }
        if (graph == null) {
            logger.info("Creating In-Memory RDF4J store");
            graph = new MemoryRDFGraph();
        }
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

    public String getPathDataDir() {
        return pathDataDir;
    }

    public void setPathDataDir(String pathDataDir) {
        this.pathDataDir = pathDataDir;
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