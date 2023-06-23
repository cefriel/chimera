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
package com.cefriel.rml;

import be.ugent.rml.Initializer;
import be.ugent.rml.Mapper;
import be.ugent.rml.access.AccessFactory;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.term.Term;
import com.cefriel.component.RmlBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraRmlConstants;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class RmlProcessor {

    static Logger logger = LoggerFactory.getLogger(RmlProcessor.class);
    static ExecutorService executorService;

    public static void execute(Exchange exchange, AccessFactory accessFactory, RDFGraph graph) throws Exception {

        int numThreads = ChimeraRmlConstants.DEFAULT_NUM_THREADS;
        final Initializer initializer;

        RmlBean configuration = exchange.getMessage().getHeader(ChimeraRmlConstants.RML_CONFIG, RmlBean.class);

        IRI context =  graph.getNamedGraph();

        Initializer rmlInitializer = exchange.getContext().getRegistry().lookupByNameAndType(RmlConfigurator.getInitializerId(configuration), Initializer.class);
        if (rmlInitializer != null)
                initializer = rmlInitializer;
        else {
            initializer = RmlConfigurator.getInitializer(exchange, configuration);
            if (initializer == null)
                throw new IllegalArgumentException("RML Initializer cannot be retrieved or generated using RMLOptions");
            else
                exchange.getContext().getRegistry().bind(RmlConfigurator.getInitializerId(configuration), initializer);
        }

        //Init executors if needed for writes or records
        RmlConfigurator.initExecutors(configuration);
        //Log current config
        logger.info(getConfig(configuration));

        if (configuration.getConcurrency() != null) {
            List<Future<String>> jobs = new ArrayList<Future<String>>();
            if (executorService == null) {
                executorService = Executors.newFixedThreadPool(numThreads);
                logger.info("ExecutorService for RmlProcessor initialized [num_threads = " + numThreads + "]");
            }
            ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(executorService);

            // Check option for single RecordsFactory
            final RecordsFactory recordsFactory;
            if (configuration.isSingleRecordsFactory()) {
                recordsFactory = RmlConfigurator.getRecordsFactory(accessFactory, configuration);
                logger.info("Single RecordsFactory instantiated");
            }
            else
                recordsFactory = null;

            if (configuration.getConcurrency().toLowerCase().equals(ChimeraRmlConstants.CONCURRENCY_LOGICAL_SOURCE)) {
                logger.info("Logical Source Concurrency enabled. Num threads: " + numThreads);
                Map<String, List<Term>> orderedTriplesMaps = RmlConfigurator.getOrderedTriplesMaps(initializer);
                for (String logicalSource : orderedTriplesMaps.keySet()) {
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper;
                        if (configuration.isSingleRecordsFactory())
                            mapper = RmlConfigurator.configure(exchange, graph, recordsFactory, initializer, configuration);
                        else
                            mapper = RmlConfigurator.configure(exchange, graph, accessFactory, initializer, configuration);
                        if(mapper != null)
                            executeMappings(mapper, orderedTriplesMaps.get(logicalSource));
                        return "Job done for Logical Source " + logicalSource;
                    }));
                }
            } else if (configuration.getConcurrency().toLowerCase().equals(ChimeraRmlConstants.CONCURRENCY_TRIPLES_MAPS)) {
                logger.info("Triples Maps Concurrency enabled. Num threads: " + numThreads);
                List<Term> triplesMaps = initializer.getTriplesMaps();
                for (Term triplesMap : triplesMaps) {
                    List<Term> mappings = new ArrayList<>();
                    mappings.add(triplesMap);
                    jobs.add(completionService.submit(() -> {
                        Mapper mapper;
                        if (configuration.isSingleRecordsFactory())
                            mapper = RmlConfigurator.configure(exchange, graph, recordsFactory, initializer, configuration);
                        else
                            mapper = RmlConfigurator.configure(exchange, graph, accessFactory, initializer, configuration);
                        if(mapper != null)
                            executeMappings(mapper, mappings);
                        return "Job done for Triples Map " + triplesMap.getValue();
                    }));
                }
            }

            // wait for all tasks to complete
            for (Future<String> task : jobs)
                try {
                    String result = task.get();
                    logger.debug(result);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage(), e);
                    e.printStackTrace();
                }

        } else {
            Mapper mapper = RmlConfigurator.configure(exchange, graph, accessFactory, initializer, configuration);
            if(mapper != null)
                executeMappings(mapper, null);
        }

        logger.info("RML processing completed");
    }

    private static void executeMappings(Mapper mapper, List<Term> triplesMaps) throws Exception {
        QuadStore outputStore = mapper.execute(triplesMaps);

        // Write quads to the context graph
        outputStore.shutDown();
    }

    public static String getConfig(RmlBean o) {
        return "Configuration RmlProcessor [" +
                "batchSize=" + o.getBatchSize() + ", incrementalUpdate=" + o.isIncrementalUpdate() + ",\n" +
                "noCache=" + o.isNoCache() + ", ordered=" + o.isOrdered() + ",\n" +
                "concurrentWrites=" + o.isConcurrentWrites() + ", concurrentRecords=" + o.isConcurrentRecords() + ",\n" +
                "concurrency=" + o.getConcurrency() + ", singleRecordsFactory=" + o.isSingleRecordsFactory() +
                ']';
    }

}
