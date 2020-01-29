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

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.GrelProcessor;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.RDF4JRemoteStore;
import be.ugent.rml.store.RDF4JStore;
import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RMLConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(RMLConfigurator.class);

    static Executor configure(RDFGraph graph, IRI contextIRI, Map<String, InputStream> streams, RMLOptions options) {

        try {
            // parse the command line arguments
            String basePath = System.getProperty("user.dir");

            // Concatenate all mapping files
            List<InputStream> lis = options.getMappings().stream()
                    .map(Utils::getInputStreamFromMOptionValue)
                    .collect(Collectors.toList());
            InputStream is = new SequenceInputStream(Collections.enumeration(lis));

            // Read mapping file.
            RDF4JStore rmlStore = new RDF4JStore();
            rmlStore.read(is, null, RDFFormat.TURTLE);

            RecordsFactory factory = new RecordsFactory(basePath, streams);

            RDF4JRemoteStore outputStore;
            if (graph.isRemote() && options.getBatchSize() > 0) {
                    outputStore = new RDF4JRemoteStore(graph.getRepository(), contextIRI,
                            options.getBatchSize(), options.isIncrementalUpdate());
            } else {
                // Covers both the cases: HTTPRepository not incremental / In-memory repository
                outputStore = new RDF4JRemoteStore(graph.getRepository(), contextIRI, 0, false);
            }

            String baseIRI;
            if (options.getBaseIRI() != null) {
                baseIRI = options.getBaseIRI();
                logger.debug("Base IRI set to value: " + options.getBaseIRI());
            }
            else {
                baseIRI = Utils.getBaseDirectiveTurtle(is);
                if (baseIRI == null)
                    baseIRI = ProcessorConstants.BASE_IRI_VALUE;
            }

            outputStore.copyNameSpaces(rmlStore);

            if (baseIRI != null)
                if (options.getBaseIRIPrefix() != null)
                    outputStore.addNamespace(options.getBaseIRIPrefix(), baseIRI);
                else
                    outputStore.addNamespace("base", baseIRI);

            Executor executor;

            String fOptionValue = options.getFunctionFile();
            FunctionLoader functionLoader;

            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("GrelFunctions", GrelProcessor.class);
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);

            if (fOptionValue == null) {
                functionLoader = new FunctionLoader(null, null, libraryMap);
            } else {
                functionLoader = new FunctionLoader(Utils.getFile(fOptionValue), null, libraryMap);
            }

            // We have to get the InputStreams of the RML documents again,
            // because we can only use an InputStream once.
            lis = options.getMappings().stream()
                    .map(Utils::getInputStreamFromMOptionValue)
                    .collect(Collectors.toList());
            is = new SequenceInputStream(Collections.enumeration(lis));

            executor = new Executor(rmlStore, factory, functionLoader, outputStore, baseIRI);

            if (options.isNoCache())
                executor.setNoCache(true);
            if (options.isOrdered())
                executor.setOrdered(true);

            return executor;

        } catch (ParseException exp) {
            // oops, something went wrong
            logger.error("Parsing failed. Reason: " + exp.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}
