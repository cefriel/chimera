/*
 * Copyright 2020 Cefriel.
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

package com.cefriel.chimera.processor.rml;

import be.ugent.rml.Initializer;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.store.RDF4JStore;
import com.cefriel.chimera.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RMLInitializerProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(RMLInitializerProcessor.class);

    private static Map<String, Initializer> cache = new ConcurrentHashMap<>();
    private String baseUrl;

    private String rmlMappings;
    private List<String> functionFiles;

    @Override
    public void process(Exchange exchange) throws Exception {
        String cacheInvalidation = exchange.getMessage().getHeader(ProcessorConstants.CACHE_INVALIDATION, String.class);
        if (cacheInvalidation != null)
            if (cacheInvalidation.toLowerCase().equals("true")) {
            cache = new ConcurrentHashMap<>();
            logger.info("Cache invalidated.");
        }

        String mappingsId = "";
        List<String> mappingsUrl = new ArrayList<>();
        ConverterConfiguration configuration =
                exchange.getMessage().getHeader(ProcessorConstants.CONVERTER_CONFIGURATION, ConverterConfiguration.class);
        if (configuration != null && configuration.getLiftingMappings() != null) {
            logger.info("Converter configuration found in the exchange, lifting mappings extracted");
            mappingsId = configuration.getConverterId();
            mappingsUrl = configuration.getLiftingMappings().stream()
                    .filter(r -> r.getSerialization().equals(ProcessorConstants.RML_SERIALIZATION_KEY))
                    .map(r -> r.getUrl())
                    .collect(Collectors.toList());
        }
        if (mappingsUrl.isEmpty()) {
            mappingsId = exchange.getMessage().getHeader(RMLProcessorConstants.RML_MAPPINGS, String.class);
            if (mappingsId == null)
                mappingsId = rmlMappings;
            if (mappingsId == null)
                throw new IllegalArgumentException("RML Mappings not specified. Cannot create Initializer.");
            mappingsUrl.add(Utils.trailingSlash(baseUrl) + mappingsId);
        }

        Initializer initializer = null;
        synchronized (cache) {
            initializer = cache.get(mappingsId);
            if (initializer != null) {
                logger.info("Cached initializer used for: " + mappingsId);
                exchange.getMessage().setBody(initializer, Initializer.class);
                return;
            }
        }

        String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);
        RDF4JStore rmlStore = new RDF4JStore();

        for(String url : mappingsUrl) {
            InputStream rmlIS = UniLoader.open(url, token);
            if (rmlIS == null)
                throw new IllegalArgumentException("RML mappings [" + url +  "] not found. Cannot create Initializer.");
            rmlStore.read(rmlIS, null, RDFFormat.TURTLE);
        }

        FunctionLoader functionLoader;
        functionLoader = RMLConfigurator.getFunctionLoader(functionFiles);

        logger.info("RML Initializer created");
        initializer = new Initializer(rmlStore, functionLoader);
        cache.put(mappingsId, initializer);
        exchange.getMessage().setBody(initializer, Initializer.class);
    }

    public String getRmlMappings() {
        return rmlMappings;
    }

    public void setRmlMappings(String rmlMappings) {
        this.rmlMappings = rmlMappings;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getFunctionFiles() {
        return functionFiles;
    }

    public void setFunctionFiles(List<String> functionFiles) {
        this.functionFiles = functionFiles;
    }

}
