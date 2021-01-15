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

import java.util.*;
import java.util.concurrent.*;

import be.ugent.rml.Initializer;
import be.ugent.rml.access.AccessFactory;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.Mapper;
import be.ugent.rml.term.Term;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefriel.chimera.util.RMLProcessorConstants;

public class RMLProcessor implements Processor {

    Logger logger = LoggerFactory.getLogger(RMLProcessor.class);

    RMLOptions defaultRmlOptions;
    private Initializer rmlInitializer;

    boolean singleRecordsFactory;

    static ExecutorService executorService;
    String concurrency;
    int numThreads = RMLProcessorConstants.DEFAULT_NUM_THREADS;

    @Override
    public void process(Exchange exchange) throws Exception {
        String basePath = System.getProperty("user.dir");
        processRML(exchange, new AccessFactory(basePath));
    }

    public void processRML(Exchange exchange, AccessFactory accessFactory) throws Exception {
        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");

        // RML Processor configuration
        final RMLOptions rmlOptions;
        RMLOptions messageRmlOptions = exchange.getMessage().getHeader(RMLProcessorConstants.RML_CONFIG, RMLOptions.class);
        if (messageRmlOptions == null)
            messageRmlOptions = defaultRmlOptions;
        else
            exchange.getMessage().removeHeader(RMLProcessorConstants.RML_CONFIG);
        synchronized (messageRmlOptions) {
            rmlOptions = new RMLOptions(messageRmlOptions);
        }

        String baseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
        String baseIRIPrefix = exchange.getMessage().getHeader(RMLProcessorConstants.PREFIX_BASE_IRI, String.class);
        if (baseIRI != null)
            rmlOptions.setBaseIRI(baseIRI);
        if (baseIRIPrefix != null)
            rmlOptions.setBaseIRIPrefix(baseIRIPrefix);

        IRI context =  graph.getContext();

        final Initializer initializer;
        if (rmlInitializer != null) {
            initializer = rmlInitializer;
        } else {
            initializer = RMLConfigurator.getInitializer(rmlOptions);
            if (initializer == null)
                throw new IllegalArgumentException("RML Initializer cannot be retrieved or generated using RMLOptions");
        }

        //Init executors if needed for writes or records
        RMLConfigurator.initExecutors(rmlOptions);
        //Log current config
        logger.info(getConfig(rmlOptions));

        if (concurrency != null) {
            List<Future<String>> jobs = new ArrayList<Future<String>>();
            if (executorService == null) {
                executorService = Executors.newFixedThreadPool(numThreads);
                logger.info("ExecutorService for RMLProcessor initialized [num_threads = " + numThreads + "]");
            }
            ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(executorService);

            // Check option for single RecordsFactory
            final RecordsFactory recordsFactory;
            if (singleRecordsFactory) {
                recordsFactory = RMLConfigurator.getRecordsFactory(accessFactory, rmlOptions);
                logger.info("Single RecordsFactory instantiated");
            }
            else
                recordsFactory = null;

            if (concurrency.toLowerCase().equals(RMLProcessorConstants.CONCURRENCY_LOGICAL_SOURCE)) {
                logger.info("Logical Source Concurrency enabled. Num threads: " + numThreads);
                Map<String, List<Term>> orderedTriplesMaps = RMLConfigurator.getOrderedTriplesMaps(initializer);
                for (String logicalSource : orderedTriplesMaps.keySet()) {
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper;
                        if (singleRecordsFactory)
                            mapper = RMLConfigurator.configure(graph, context, recordsFactory, initializer, rmlOptions);
                        else
                            mapper = RMLConfigurator.configure(graph, context, accessFactory, initializer, rmlOptions);
                        if(mapper != null)
                            executeMappings(mapper, orderedTriplesMaps.get(logicalSource));
                        return "Job done for Logical Source " + logicalSource;
                    }));
                }
            } else if (concurrency.toLowerCase().equals(RMLProcessorConstants.CONCURRENCY_TRIPLES_MAPS)) {
                logger.info("Triples Maps Concurrency enabled. Num threads: " + numThreads);
                List<Term> triplesMaps = initializer.getTriplesMaps();
                for (Term triplesMap : triplesMaps) {
                    List<Term> mappings = new ArrayList<>();
                    mappings.add(triplesMap);
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper;
                        if (singleRecordsFactory)
                            mapper = RMLConfigurator.configure(graph, context, recordsFactory, initializer, rmlOptions);
                        else
                            mapper = RMLConfigurator.configure(graph, context, accessFactory, initializer, rmlOptions);
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
                    logger.debug(result);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    e.printStackTrace();
                }

        } else {
            Mapper mapper = RMLConfigurator.configure(graph, context, accessFactory, initializer, rmlOptions);
            if(mapper != null)
                executeMappings(mapper, null);
        }

        logger.info("RML processing completed");
    }

    private void executeMappings(Mapper mapper, List<Term> triplesMaps) throws Exception {
        QuadStore outputStore = mapper.execute(triplesMaps);

        // Write quads to the context graph
        outputStore.shutDown();
    }

    private String getConfig(RMLOptions o) {
        return "Configuration RMLProcessor [" +
                "batchSize=" + o.getBatchSize() + ", incrementalUpdate=" + o.isIncrementalUpdate() + ",\n" +
                "noCache=" + o.isNoCache() + ", ordered=" + o.isOrdered() + ",\n" +
                "concurrentWrites=" + o.isConcurrentWrites() + ", concurrentRecords=" + o.isConcurrentRecords() + ",\n" +
                "concurrency=" + concurrency + ", singleRecordsFactory=" + singleRecordsFactory +
                ']';
    }

    public RMLOptions getDefaultRmlOptions() {
        return defaultRmlOptions;
    }

    public void setDefaultRmlOptions(RMLOptions defaultRmlOptions) {
        this.defaultRmlOptions = defaultRmlOptions;
    }

    public Initializer getRmlInitializer() {
        return rmlInitializer;
    }

    public void setRmlInitializer(Initializer rmlInitializer) {
        this.rmlInitializer = rmlInitializer;
    }

    public boolean isSingleRecordsFactory() {
        return singleRecordsFactory;
    }

    public void setSingleRecordsFactory(boolean singleRecordsFactory) {
        this.singleRecordsFactory = singleRecordsFactory;
    }

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

}
