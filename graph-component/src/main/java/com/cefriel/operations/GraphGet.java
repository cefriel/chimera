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

package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.*;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GraphGet {

    private static final Logger LOG = LoggerFactory.getLogger(GraphGet.class);

    public static RDFGraph graphCreate(Exchange exchange) throws IOException {

        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);

        //configuration.getOntologyUrls().addAll(setConverterConfiguration(configuration, exchange));
        RDFGraph graph = null;
        boolean attached = false;
        ValueFactory vf = SimpleValueFactory.getInstance();

        if(!configuration.isDefaultGraph()){
            if(exchange.getMessage().getHeader(ChimeraConstants.CONTEXT_GRAPH, String.class) != null)
                configuration.setNamedGraph(exchange.getMessage().getHeader(ChimeraConstants.CONTEXT_GRAPH, String.class));
            if(configuration.getNamedGraph() == null) {
                if(exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class) != null) {
                    configuration.setNamedGraph(exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class) + exchange.getExchangeId());
                    configuration.setBaseIri(exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class));
                }
                else if (configuration.getBaseIri() != null) {
                    configuration.setNamedGraph(configuration.getBaseIri() + exchange.getExchangeId());
                }
                else
                    configuration.setNamedGraph(ChimeraConstants.DEFAULT_BASE_IRI + exchange.getExchangeId());
            }
            exchange.getMessage().setHeader(ChimeraConstants.GRAPH_ID, exchange.getExchangeId());
        }

        if (configuration.getServerUrl() != null && configuration.getRepositoryID() != null) {
            LOG.info("Connecting to remote repository " + configuration.getServerUrl()+ " " + configuration.getRepositoryID());
            graph = new HTTPRDFGraph(configuration.getServerUrl(), configuration.getRepositoryID());
            attached = true;
        }

        if (configuration.getSparqlEndpoint() != null) {
            LOG.info("Connecting to SPARQL configuration " + configuration.getSparqlEndpoint());
            graph = new SPARQLEndpointGraph(configuration.getSparqlEndpoint());
            if (attached)
                LOG.warn("Multiple graphs provided! SPARQL configuration attached, others have been discarded!");
            attached = true;
        }

        Repository schema = null;
        if (configuration.getResources().size() != 0) {
            if(configuration.getOntologyFormat() != null)
                exchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, configuration.getOntologyFormat());
            if (attached) {
                Utils.addSchemaToRepository(graph.getRepository(),configuration, exchange);
                LOG.info("Inference demanded to the remote graph. Schema added: " + configuration.getResources());
            } else
                schema = Utils.getSchemaRepository(configuration, exchange);
            exchange.getMessage().removeHeader(ChimeraConstants.RDF_FORMAT);
        }

        if (configuration.getPathDataDir() != null) {
            LOG.info("Creating Native RDF4J store with path " + configuration.getPathDataDir());
            if (schema != null) {
                graph = new InferenceRDFGraph(schema, configuration.getPathDataDir(), configuration.isAllRules());
                LOG.info("Inference enabled using schema: " + configuration.getResources());
            }  else
                graph = new NativeRDFGraph(configuration.getPathDataDir());
            if (attached)
                LOG.warn("Multiple graphs provided! Native Graph attached, others have been discarded!");
        }
        if (graph == null) {
            LOG.info("Creating In-Memory RDF4J store");
            if (schema != null) {
                graph = new InferenceRDFGraph(schema, null, configuration.isAllRules());
                LOG.info("Inference enabled using schema: " + configuration.getResources());
            } else
                graph = new MemoryRDFGraph();
        }

        if (configuration.getNamedGraph() != null) {
            graph.setNamedGraph(configuration.getNamedGraph());
            LOG.info("The graph has name: " + graph.getNamedGraph());
        }
        if (configuration.getBaseIri() != null)
            graph.setBaseIRI(vf.createIRI(configuration.getBaseIri()));

        return graph;
    }
}
