/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.processor.rml;

import be.ugent.rml.*;
import be.ugent.rml.access.AccessFactory;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.*;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RMLConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(RMLConfigurator.class);

    static Initializer getInitializer(RMLOptions options) {

        try {
            // Concatenate all mapping files
            List<InputStream> lis = options.getMappings().stream()
                    .map(Utils::getInputStreamFromFileOrContentString)
                    .collect(Collectors.toList());
            InputStream is = new SequenceInputStream(Collections.enumeration(lis));

            // Read mapping file.
            RDF4JStore rmlStore = new RDF4JStore();
            rmlStore.read(is, null, RDFFormat.TURTLE);

            String[] fOptionValue = null;
            List<String> functionFiles = options.getFunctionFiles();
            if (functionFiles != null) {
                fOptionValue = new String[functionFiles.size()];
                fOptionValue = functionFiles.toArray(fOptionValue);
            }
            FunctionLoader functionLoader;

            // Read function description files.
            if (fOptionValue == null) {
                functionLoader = new FunctionLoader();
            } else {
                logger.debug("Using custom path to functions.ttl file: " + Arrays.toString(fOptionValue));
                RDF4JStore functionDescriptionTriples = new RDF4JStore();
                functionDescriptionTriples.read(Utils.getInputStreamFromFile(Utils.getFile("functions_idlab.ttl")), null, RDFFormat.TURTLE);
                Map<String, Class> libraryMap = new HashMap<>();
                libraryMap.put("IDLabFunctions", IDLabFunctions.class);
                List<InputStream> lisF = Arrays.stream(fOptionValue)
                        .map(Utils::getInputStreamFromFileOrContentString)
                        .collect(Collectors.toList());
                for (int i = 0; i < lisF.size(); i++) {
                    functionDescriptionTriples.read(lisF.get(i), null, RDFFormat.TURTLE);
                }
                functionLoader = new FunctionLoader(functionDescriptionTriples, libraryMap);
            }

            return new Initializer(rmlStore, functionLoader);

        } catch (ParseException exp) {
            // oops, something went wrong
            logger.error("Parsing failed. Reason: " + exp.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;

    }

    static RecordsFactory getRecordsFactory(AccessFactory accessFactory, RMLOptions options) {
        RecordsFactory factory = new RecordsFactory(accessFactory);
        if (options.isEmptyStrings())
            factory.setEmptyStrings(true);
        return factory;
    }

    static Mapper configure(RDFGraph graph, IRI contextIRI, RecordsFactory factory, RMLOptions options) {
        return configure(graph, contextIRI, factory, null, options);
    }

    static Mapper configure(RDFGraph graph, IRI contextIRI, RecordsFactory factory, Initializer initializer, RMLOptions options) {
        try {
            if (initializer == null)
                initializer = getInitializer(options);

            RDF4JRepository outputStore;
            if (options.isConcurrentWrites()) {
                outputStore = new ConcurrentRDF4JRepository(graph.getRepository(), contextIRI,
                        options.getBatchSize(), options.isIncrementalUpdate());
            } else {
                outputStore = new RDF4JRepository(graph.getRepository(), contextIRI,
                        options.getBatchSize(), options.isIncrementalUpdate());
            }

            String baseIRI;
            if (options.getBaseIRI() != null) {
                baseIRI = options.getBaseIRI();
                logger.debug("Base IRI set to value: " + options.getBaseIRI());
            }
            else {
                baseIRI = ProcessorConstants.BASE_IRI_VALUE;
            }

            outputStore.copyNameSpaces(initializer.getRMLStore());

            if (baseIRI != null)
                if (options.getBaseIRIPrefix() != null)
                    outputStore.addNamespace(options.getBaseIRIPrefix(), baseIRI);
                else
                    outputStore.addNamespace("base", baseIRI);

            Mapper mapper;
            if(options.isConcurrentRecords()) {
                mapper = new ConcurrentExecutor(initializer, factory, outputStore, baseIRI);
            } else {
                mapper = new Executor(initializer, factory, outputStore, baseIRI);
            }

            if (options.isNoCache())
                mapper.setNoCache(true);
            if (options.isOrdered())
                mapper.setOrdered(true);

            return mapper;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static void initExecutors (RMLOptions options) {
        if(options.isConcurrentRecords())
            if (ConcurrentExecutor.executorService == null) {
                ConcurrentExecutor.executorService = Executors.newFixedThreadPool(options.getNumThreadsRecords());
                logger.info("ExecutorService for ConcurrentExecutor initialized [num_threads = " + options.getNumThreadsRecords() + "]");
            }
        if (options.isConcurrentWrites())
            if (ConcurrentRDF4JRepository.executorService == null) {
                ConcurrentRDF4JRepository.executorService = Executors.newFixedThreadPool(options.getNumThreadsWrites());
                logger.info("ExecutorService for ConcurrentRDF4JRepository initialized [num_threads = " + options.getNumThreadsWrites() + "]");
            }
    }

    public static Map<String, List<Term>> getOrderedTriplesMaps(Initializer initializer) {
        QuadStore rmlStore = initializer.getRMLStore();
        List<Term> triplesMaps = initializer.getTriplesMaps();
        Map<String, List<Term>> orderedTriplesMaps = new HashMap<>();
        for (Term triplesMap : triplesMaps) {
            List<Term> logicalSources = Utils.getObjectsFromQuads(rmlStore.getQuads(triplesMap, new NamedNode(NAMESPACES.RML + "logicalSource"), null));
            if (logicalSources.isEmpty()) {
                orderedTriplesMaps.get("others").add(triplesMap);
                continue;
            }
            Term logicalSource = logicalSources.get(0);
            List<Quad> quads = rmlStore.getQuads(logicalSource, new NamedNode(NAMESPACES.RML + "source"), null);
            if (quads.isEmpty()) {
                orderedTriplesMaps.get("others").add(triplesMap);
                continue;
            }
            String source = quads.get(0).getObject().getValue();
            if (source == null) {
                orderedTriplesMaps.get("others").add(triplesMap);
                continue;
            }
            if(orderedTriplesMaps.containsKey(source))
                orderedTriplesMaps.get(source).add(triplesMap);
            else{
                List<Term> terms = new ArrayList<>();
                terms.add(triplesMap);
                orderedTriplesMaps.put(source, terms);
            }
        }
        return orderedTriplesMaps;
    }

}
