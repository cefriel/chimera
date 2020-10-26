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
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.RMLProcessorConstants;
import com.cefriel.chimera.util.UniLoader;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RMLInitializerProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(RMLInitializerProcessor.class);

    private static Map<String, Initializer> cache = new HashMap<>();
    private String baseUrl;

    private String rmlMappings;
    private List<String> functionFiles;

    @Override
    public void process(Exchange exchange) throws Exception {

        String mappings = exchange.getMessage().getHeader(RMLProcessorConstants.RML_MAPPINGS, String.class);
        if (mappings == null)
            mappings = rmlMappings;
        if (mappings == null) {
            logger.error("RML Mappings not specified. Cannot create Initializer.");
            exchange.getMessage().setBody(null);
            return;
        }

        Initializer initializer = null;
        synchronized (cache) {
            initializer = cache.get(mappings);
            if (initializer != null) {
                logger.info("Cached initializer used for: " + mappings);
                exchange.getMessage().setBody(initializer, Initializer.class);
                return;
            }
        }

        String mappingsUrl = "";
        mappingsUrl = Utils.trailingSlash(baseUrl) + "rml/" + mappings;
        String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

        InputStream rmlIS = UniLoader.open(mappingsUrl, token);
        if (rmlIS == null) {
            logger.error("RML Mappings not found. Cannot create Initializer.");
            exchange.getMessage().setBody(null);
            return;
        }
        RDF4JStore rmlStore = new RDF4JStore();
        rmlStore.read(rmlIS, null, RDFFormat.TURTLE);

        //TODO If remote (provided in the configuration) then choose the remote functions file
        FunctionLoader functionLoader;
        functionLoader = RMLConfigurator.getFunctionLoader(functionFiles);

        logger.info("RML Initializer created");
        initializer = new Initializer(rmlStore, functionLoader);
        cache.put(mappings, initializer);
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
