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
import java.util.concurrent.*;

import be.ugent.rml.ConcurrentExecutor;
import be.ugent.rml.Initializer;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.Mapper;
import be.ugent.rml.term.Term;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefriel.chimera.util.RMLProcessorConstants;

public class RMLProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RMLProcessor.class);

    private RMLOptions defaultRmlOptions;
    private String concurrency;
    private int nThreads = 4;
    //TODO Set also nThreads for ConcurrentExecutor executorService?
    //TODO Use the same executorService?
    
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
        final RMLOptions rmlOptions;
        RMLOptions messageRmlOptions = exchange.getIn().getHeader(RMLProcessorConstants.RML_CONFIG, RMLOptions.class);
        if (messageRmlOptions != null) {
            exchange.getIn().removeHeader(RMLProcessorConstants.RML_CONFIG);
            rmlOptions = messageRmlOptions;
        } else {
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

        final Initializer initializer;
        Initializer messageInitializer = exchange.getIn().getHeader(RMLProcessorConstants.RML_INITIALIZER, Initializer.class);
        if (messageInitializer != null) {
            exchange.getIn().removeHeader(RMLProcessorConstants.RML_INITIALIZER);
            initializer = messageInitializer;
        } else {
            initializer = RMLConfigurator.getInitializer(rmlOptions);
            if (initializer == null)
                throw new IllegalArgumentException("Initializer cannot be null");
        }

        if (concurrency != null) {
            List<Future<String>> jobs = new ArrayList<Future<String>>();
            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
            ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(executorService);

            if (concurrency.toLowerCase().equals(RMLProcessorConstants.CONCURRENCY_LOGICAL_SOURCE)) {
                logger.info("Logical Source Concurrency enabled. Num threads: " + nThreads);
                Map<String, List<Term>> orderedTriplesMaps = RMLConfigurator.getOrderedTriplesMaps(initializer);
                for (String logicalSource : orderedTriplesMaps.keySet()) {
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper = RMLConfigurator.configure(graph, context, streamsMap, initializer, rmlOptions);
                        if(mapper != null)
                            executeMappings(mapper, orderedTriplesMaps.get(logicalSource));
                        return "Job done for Logical Source " + logicalSource;
                    }));
                }
            } else if (concurrency.toLowerCase().equals(RMLProcessorConstants.CONCURRENCY_TRIPLES_MAPS)) {
                logger.info("Triples Maps Concurrency enabled. Num threads: " + nThreads);
                List<Term> triplesMaps = initializer.getTriplesMaps();
                for (Term triplesMap : triplesMaps) {
                    List<Term> mappings = new ArrayList<>();
                    mappings.add(triplesMap);
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper = RMLConfigurator.configure(graph, context, streamsMap, initializer, rmlOptions);
                        if(mapper != null)
                            executeMappings(mapper, mappings);
                        return "Job done for Triples Map " + triplesMap.getValue();
                    }));
                }
            }

            /* wait for all tasks to complete */
            for (Future<String> task : jobs)
                try {
                    String result = task.get();
                    logger.info(result);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    ;
                    executorService.shutdownNow();
                }

            executorService.shutdownNow();

        } else {
            Mapper mapper = RMLConfigurator.configure(graph, context, streamsMap, initializer, rmlOptions);
            if(mapper != null)
                executeMappings(mapper, null);
        }

        if (ConcurrentExecutor.executorService != null)
            ConcurrentExecutor.executorService.shutdownNow();
    }

    private void executeMappings(Mapper mapper, List<Term> triplesMaps) throws Exception {
        QuadStore outputStore = mapper.execute(triplesMaps);

        if (outputStore.isEmpty())
            logger.info("No results!");

        //Write quads to the context graph
        outputStore.shutDown();
    }

    public RMLOptions getDefaultRmlOptions() {
        return defaultRmlOptions;
    }

    public void setDefaultRmlOptions(RMLOptions defaultRmlOptions) {
        this.defaultRmlOptions = defaultRmlOptions;
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public int getnThreads() {
        return nThreads;
    }

    public void setnThreads(int nThreads) {
        this.nThreads = nThreads;
    }

}
