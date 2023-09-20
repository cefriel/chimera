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
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.*;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

public class GraphDetach {
    private static final Logger LOG = LoggerFactory.getLogger(GraphDetach.class);
    private record EndpointParams(ChimeraResourcesBean ontologyUrls, boolean clearGraph, boolean repoOff, boolean routeOff) {}
    private record OperationParams(RDFGraph graph, EndpointParams endpointParams) {}
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getChimeraResources(),
                operationConfig.isClear(),
                operationConfig.isRepoOff(),
                operationConfig.isRouteOff());
    }
    private static OperationParams getOperationParams(GraphBean operationConfig, Exchange e) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                getEndpointParams(operationConfig));
    }
    public static boolean validParams(OperationParams params) {
        if (params.graph() == null)
            throw new IllegalArgumentException("Graph in exchange body cannot be null");
        return true;
    }
    public static void graphDetach(Exchange exchange, GraphBean operationConfig) throws IOException {
        var params = getOperationParams(operationConfig, exchange);
        graphDetach(params, exchange);
    }
    public static void graphDetach(OperationParams params, Exchange exchange) throws IOException {
        if (validParams(params)) {
            if (params.endpointParams.clearGraph())
            {
                IRI contextIRI = params.graph.getNamedGraph();
                try (RepositoryConnection con = params.graph().getRepository().getConnection()) {
                    if (contextIRI != null) {
                        con.clear(contextIRI);
                        LOG.info("Cleared named graph " + contextIRI.stringValue());
                    }
                    if (params.endpointParams().ontologyUrls() != null)
                        for (ChimeraResourceBean ontologyUrl : params.endpointParams().ontologyUrls().getResources()) {
                            Model l = StreamParser.parseResource(ontologyUrl, exchange);
                            Set<Namespace> namespaces = l.getNamespaces();
                            for (Namespace n : namespaces)
                                con.removeNamespace(n.getPrefix());
                            LOG.info("Removed namespaces listed in file " + ontologyUrl.getUrl());
                        }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (params.endpointParams().repoOff())
            {
                if (params.graph().getRepository() != null)
                    params.graph().getRepository().shutDown();
                LOG.info("Repo shot down");
            }
            if(params.endpointParams().routeOff()){
                CamelContext camelContext = exchange.getContext();
                // Remove myself from the in flight registry so we can stop this route without trouble
                Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                if (caused !=null)
                    LOG.error(stackToString(caused));
                camelContext.getInflightRepository().remove(exchange);
                // Stop the route
                Thread stop = new Thread(() -> {
                    try {
                    /*if (routeId != null)
                        camelContext.getRouteController().stopRoute(routeId);
                    else*/
                        camelContext.stop();
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }
                });
                LOG.info("Route stopped");
                // Start the thread that stops this route
                stop.start();
            }
        }
    }
    private static String stackToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
