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
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.StreamParser;
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

/**
 * Provides operations for detaching and cleaning up RDF graph resources.
 * <p>
 * This class handles the cleanup and shutdown of RDF graph resources at the end of
 * processing pipelines. It supports clearing named graphs, removing namespaces,
 * shutting down repositories, and stopping Camel routes. These operations are typically
 * used in route finalization or error handling scenarios.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 */
public class GraphDetach {
    private static final Logger LOG = LoggerFactory.getLogger(GraphDetach.class);

    /**
     * Detaches and cleans up RDF graph resources based on configuration flags.
     * <p>
     * This method performs cleanup operations in sequence based on the configuration:
     * </p>
     * <ul>
     *   <li>If {@code clear} is true, clears all named graphs and optionally removes specified namespaces</li>
     *   <li>If {@code repoOff} is true, shuts down the graph's repository</li>
     *   <li>If {@code routeOff} is true, stops the current Camel route</li>
     * </ul>
     *
     * @param exchange the Camel exchange containing the RDF graph to detach
     * @param config the configuration bean specifying which cleanup operations to perform
     * @throws IOException if an error occurs during namespace removal or route shutdown
     */
    public static void graphDetach(Exchange exchange, GraphBean config) throws IOException {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);

        if (config.isClear()) {
            clearGraph(graph, config.getChimeraResource(), exchange);
        }
        if (config.isRepoOff()) {
            shutdownRepository(graph);
        }
        if (config.isRouteOff()) {
            stopRoute(exchange);
        }
    }

    /**
     * Clears all named graphs in the RDF graph and optionally removes specified namespaces.
     * <p>
     * This method iterates through all named graphs associated with the RDF graph and
     * removes all triples from each one. If a {@code ChimeraResourceBean} is provided,
     * it parses the resource to extract namespace declarations and removes those
     * namespaces from the repository.
     * </p>
     *
     * @param graph the RDF graph containing the named graphs to clear
     * @param triples optional resource containing namespace declarations to remove; can be null
     * @param exchange the Camel exchange providing context for resource resolution
     * @throws RuntimeException if an error occurs during graph clearing or namespace removal
     */
    private static void clearGraph(RDFGraph graph, ChimeraResourceBean triples, Exchange exchange) {
        List<IRI> namedGraphs = graph.getNamedGraphs();
        try (RepositoryConnection con = graph.getRepository().getConnection()) {
            for (IRI namedGraph : namedGraphs) {
                if (namedGraph != null) {
                    con.clear(namedGraph);
                    LOG.info("Cleared named graph " + namedGraph.stringValue());
                }
            }
            if (triples != null) {
                Model model = StreamParser.parseResource(triples, exchange);
                Set<Namespace> namespaces = model.getNamespaces();
                for (Namespace n : namespaces) {
                    con.removeNamespace(n.getPrefix());
                }
                LOG.info("Removed namespaces listed in file " + triples.getUrl());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down the RDF graph's repository, releasing all resources.
     * <p>
     * This method safely closes the repository connection and releases any system
     * resources held by the repository. After shutdown, the repository cannot be
     * used for further operations.
     * </p>
     *
     * @param graph the RDF graph whose repository should be shut down
     */
    private static void shutdownRepository(RDFGraph graph) {
        if (graph.getRepository() != null) {
            graph.getRepository().shutDown();
        }
        LOG.info("Repo shut down");
    }

    /**
     * Stops the current Camel route and removes the exchange from the inflight repository.
     * <p>
     * This method logs any exceptions caught during exchange processing, removes the
     * exchange from the inflight repository to prevent memory leaks, and initiates
     * an asynchronous shutdown of the Camel context. The shutdown is performed in a
     * separate thread to avoid blocking the current execution.
     * </p>
     *
     * @param exchange the Camel exchange containing the context to stop
     */
    private static void stopRoute(Exchange exchange) {
        CamelContext camelContext = exchange.getContext();
        Throwable caused = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        if (caused != null) {
            LOG.error(stackToString(caused));
        }
        camelContext.getInflightRepository().remove(exchange);

        Thread stop = new Thread(() -> {
            try {
                camelContext.stop();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        });
        LOG.info("Route stopped");
        stop.start();
    }

    /**
     * Converts a throwable's stack trace to a string representation.
     *
     * @param e the throwable to convert
     * @return the complete stack trace as a string
     */
    private static String stackToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
