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
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cefriel.chimera.util.ProcessorConstants;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class AttachGraph implements Processor {

    private Logger logger = LoggerFactory.getLogger(AttachGraph.class);

    private String rrAddress;
    private String repositoryId;
    private String pathDataDir;
    private String sparqlEndpoint;

    private List<String> ontologyUrls;
    private String ontologyRDFFormat;
    private boolean allRules = true;

    private boolean context = true;
    private String contextIRI;

    public void process(Exchange exchange) throws Exception {

        if (context) {
            exchange.setProperty(ProcessorConstants.GRAPH_ID, exchange.getExchangeId());
            if (contextIRI == null)
                contextIRI = ProcessorConstants.BASE_IRI_VALUE + exchange.getExchangeId();
        }

        RDFGraph graph = null;
        boolean attached = false;
        if (rrAddress != null && repositoryId != null) {
            logger.info("Connecting to remote repository " + rrAddress + " " + repositoryId);
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

        Repository schema = null;
        if (ontologyUrls != null) {
            String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);
            if (attached) {
                Utils.addSchemaToRepository(graph.getRepository(), contextIRI, ontologyUrls, ontologyRDFFormat, token);
                logger.info("Inference demanded to the remote graph. Schema added: " + ontologyUrls);
            } else
                schema = Utils.getSchemaRepository(ontologyUrls, ontologyRDFFormat, token);
        }

        if (pathDataDir != null) {
            logger.info("Creating Native RDF4J store with path " + pathDataDir);
            if (schema != null) {
                graph = new InferenceRDFGraph(schema, pathDataDir, allRules);
                logger.info("Inference enabled using schema: " + ontologyUrls);
            }  else
                graph = new NativeRDFGraph(pathDataDir);
            if (attached)
                logger.warn("Multiple graphs provided! Native Graph attached, others have been discarded!");
        }
        if (graph == null) {
            logger.info("Creating In-Memory RDF4J store");
            if (schema != null) {
                graph = new InferenceRDFGraph(schema, null, allRules);
                logger.info("Inference enabled using schema: " + ontologyUrls);
            } else
                graph = new MemoryRDFGraph();
        }
    	exchange.setProperty(ProcessorConstants.CONTEXT_GRAPH, graph);
    	if(context)
            graph.setContext(contextIRI);
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

    public String getContextIRI() {
        return contextIRI;
    }

    public void setContextIRI(String contextIRI) {
        this.contextIRI = contextIRI;
    }

    public List<String> getOntologyUrls() {
        return ontologyUrls;
    }

    public void setOntologyUrls(List<String> ontologyUrls) {
        this.ontologyUrls = ontologyUrls;
    }

    public String getOntologyRDFFormat() {
        return ontologyRDFFormat;
    }

    public void setOntologyRDFFormat(String ontologyRDFFormat) {
        this.ontologyRDFFormat = ontologyRDFFormat;
    }

    public boolean isAllRules() {
        return allRules;
    }

    public void setAllRules(boolean allRules) {
        this.allRules = allRules;
    }

}