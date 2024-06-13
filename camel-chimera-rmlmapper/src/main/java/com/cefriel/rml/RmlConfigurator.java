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

import be.ugent.rml.Utils;
import be.ugent.rml.*;
import be.ugent.rml.access.AccessFactory;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.JSONOptRecordFactory;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.records.ReferenceFormulationRecordFactory;
import be.ugent.rml.records.XMLSAXRecordFactory;
import be.ugent.rml.store.*;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;
import com.cefriel.component.RmlBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.*;
import org.apache.camel.Exchange;
import org.apache.commons.cli.ParseException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class RmlConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(RmlConfigurator.class);

    public static Initializer getInitializer(Exchange exchange, RmlBean configuration) {

        try {
            InputStream is;
            FunctionLoader functionLoader;
            // Concatenate all mapping files
            if(exchange.getMessage().getHeader(ChimeraRmlConstants.RML_MAPPINGS) != null)
                is = exchange.getMessage().getHeader(ChimeraRmlConstants.RML_MAPPINGS, InputStream.class);
            else {
                List<InputStream> lis = new ArrayList<>();
                lis.add(ResourceAccessor.open(configuration.getMapping(), exchange));
                is = new SequenceInputStream(Collections.enumeration(lis));
            }

            // Read mapping file.
            RDF4JStore rmlStore = new RDF4JStore();
            rmlStore.read(is, null, RDFFormat.TURTLE);

            if(exchange.getMessage().getHeader(ChimeraRmlConstants.RML_FUNCTION) != null)
                functionLoader = getFunctionLoader(exchange.getMessage().getHeader(ChimeraRmlConstants.RML_FUNCTION, InputStream.class));
            else {
                ChimeraResourceBean functionFile = configuration.getFunctionFile();
                if (configuration.getFunctionFile().getUrl() != null) {
                    functionLoader = getFunctionLoader(List.of(functionFile));
                }
                else functionLoader = getFunctionLoader(List.of());
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

    public static String getInitializerId(RmlBean configuration) {
        List<String> mappingFiles = Stream.of(configuration.getMapping())
                .map(fileUrl -> FileResourceAccessor.fileUrlToPath(fileUrl).toString()).toList();

        String mappings = String.join("-", mappingFiles);

        List<String> functionFiles;
        if (configuration.getFunctionFile().getUrl() != null){
            functionFiles = List.of(configuration.getFunctionFile()).stream()
                    .map(fileUrl -> FileResourceAccessor.fileUrlToPath(fileUrl).toString()).toList();
        }
        else {
            functionFiles = List.of();
        }

        String functions = String.join("-", functionFiles);
        return Long.toString((mappings+functions).hashCode());
    }

    static FunctionLoader getFunctionLoader(List<ChimeraResourceBean> functionFiles) throws Exception {

        String[] fOptionValue = null;
        if (!functionFiles.isEmpty()) {
            fOptionValue = new String[functionFiles.size()];
            fOptionValue = (String[]) functionFiles.stream().map(ChimeraResourceBean::getUrl).toArray();
        }
        // Read function description files.
        if (fOptionValue == null) {
            return new FunctionLoader();
        } else {
            logger.debug("Using custom path to functions.ttl file: " + Arrays.toString(fOptionValue));
            RDF4JStore functionDescriptionTriples = new RDF4JStore();
            functionDescriptionTriples.read(UniLoader.open("functions_idlab.ttl"), null, RDFFormat.TURTLE);
            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);
            List<InputStream> lisF = new ArrayList<>();
            for (String function : fOptionValue) {
                lisF.add(UniLoader.open(function));
            }
            for (InputStream inputStream : lisF) {
                functionDescriptionTriples.read(inputStream, null, RDFFormat.TURTLE);
            }
            return new FunctionLoader(functionDescriptionTriples, libraryMap);
        }
    }

    static FunctionLoader getFunctionLoader(InputStream inputStream) throws Exception {

        RDF4JStore functionDescriptionTriples = new RDF4JStore();
        functionDescriptionTriples.read(UniLoader.open("functions_idlab.ttl"), null, RDFFormat.TURTLE);
        Map<String, Class> libraryMap = new HashMap<>();
        libraryMap.put("IDLabFunctions", IDLabFunctions.class);
        functionDescriptionTriples.read(inputStream, null, RDFFormat.TURTLE);
        return new FunctionLoader(functionDescriptionTriples, libraryMap);

    }

    static RecordsFactory getRecordsFactory(AccessFactory accessFactory, RmlBean options) {
        Map<String, ReferenceFormulationRecordFactory> map = new HashMap<>();
        if (!options.isDefaultRecordFactory()) {
            map.put(NAMESPACES.QL + "XPath", new XMLSAXRecordFactory());
            map.put(NAMESPACES.QL + "JSONPath", new JSONOptRecordFactory());
        }
        RecordsFactory factory = new RecordsFactory(accessFactory, map);
        if (options.isEmptyStrings())
            factory.setEmptyStrings(true);
        return factory;
    }

    static Mapper configure(Exchange exchange, RDFGraph graph, AccessFactory accessFactory, Initializer initializer, RmlBean options) {
        RecordsFactory factory = getRecordsFactory(accessFactory, options);
        return configure(exchange, graph, factory, initializer, options);
    }

    static Mapper configure(Exchange exchange, RDFGraph graph, RecordsFactory factory, Initializer initializer, RmlBean options) {
        try {
            if (initializer == null)
                initializer = getInitializer(exchange, options);

            RDF4JRepository outputStore;
            if (options.isConcurrentWrites()) {
                outputStore = new ConcurrentRDF4JRepository(graph.getRepository(), null,
                        options.getBatchSize(), options.isIncrementalUpdate());
            } else {
                outputStore = new RDF4JRepository(graph.getRepository(), null, options.getBatchSize(), options.isIncrementalUpdate());
            }

            String baseIRI;
            if (options.getBaseIri() != null) {
                baseIRI = options.getBaseIri();
                logger.debug("Base IRI set to value: " + options.getBaseIri());
            }
            else {
                baseIRI = getBaseIRIFromMappings(exchange, options);
                if (baseIRI == null)
                    baseIRI = ChimeraConstants.DEFAULT_BASE_IRI;
            }

            outputStore.copyNameSpaces(initializer.getRMLStore());

            if (baseIRI != null)
                if (options.getBaseIriPrefix() != null)
                    outputStore.addNamespace(options.getBaseIriPrefix(), baseIRI);
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

    private static String getBaseIRIFromMappings(Exchange exchange, RmlBean options) throws Exception {

        InputStream is;
        // Concatenate all mapping files
        if(exchange.getMessage().getHeader(ChimeraRmlConstants.RML_MAPPINGS) != null)
            is = exchange.getMessage().getHeader(ChimeraRmlConstants.RML_MAPPINGS, InputStream.class);
        else {
            List<InputStream> lis = new ArrayList<>();
            for (var mapping : List.of(options.getMapping())) {
                lis.add(ResourceAccessor.open(mapping, exchange));
            }
            is = new SequenceInputStream(Collections.enumeration(lis));
        }

        return be.ugent.rml.Utils.getBaseDirectiveTurtle(is);
    }

    public static void initExecutors (RmlBean configuration) {
        if(configuration.isConcurrentRecords())
            if (ConcurrentExecutor.executorService == null) {
                ConcurrentExecutor.executorService = Executors.newFixedThreadPool(configuration.getNumThreadsRecords());
                logger.info("ExecutorService for ConcurrentExecutor initialized [num_threads = " + configuration.getNumThreadsRecords() + "]");
            }
        if (configuration.isConcurrentWrites())
            if (ConcurrentRDF4JRepository.executorService == null) {
                ConcurrentRDF4JRepository.executorService = Executors.newFixedThreadPool(configuration.getNumThreadsWrites());
                logger.info("ExecutorService for ConcurrentRDF4JRepository initialized [num_threads = " + configuration.getNumThreadsWrites() + "]");
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
