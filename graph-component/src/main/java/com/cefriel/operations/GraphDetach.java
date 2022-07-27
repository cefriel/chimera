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
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.StreamParser;
import com.cefriel.util.UniLoader;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

public class GraphDetach {

    private static final Logger LOG = LoggerFactory.getLogger(GraphDetach.class);

    public static void graphDetach(Exchange exchange) throws IOException {

        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);
        if (graph != null) {
            Repository repo = graph.getRepository();
            IRI contextIRI = graph.getNamedGraph();
            exchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, configuration.getRdfFormat());
            String token = exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class);

            if (configuration.isClear()) {

                try (RepositoryConnection con = repo.getConnection()) {
                    if (contextIRI != null) {
                        con.clear(contextIRI);
                        LOG.info("Cleared named graph " + contextIRI.stringValue());
                    }
                    if (configuration.getResources() != null)
                        for (String path : configuration.getResources()) {
                            Model l = StreamParser.parse(UniLoader.open(path, token), exchange);
                            Set<Namespace> namespaces = l.getNamespaces();
                            for (Namespace n : namespaces)
                                con.removeNamespace(n.getPrefix());
                            LOG.info("Removed namespaces listed in file " + path);
                        }
                }
            }

            if (configuration.isRepoOff()) {
                if (repo != null)
                    repo.shutDown();
                LOG.info("Repo shot down");
            }
        }

        if(configuration.isRouteOff()){
            CamelContext camelContext = exchange.getContext();
            // Remove myself from the in flight registry so we can stop this route without trouble
            Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
            if (caused !=null)
                LOG.error(stack_to_string(caused));
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

    private static String stack_to_string(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string
        return sStackTrace;
    }
}
