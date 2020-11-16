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

package com.cefriel.chimera.processor.template;

import com.cefriel.chimera.lowerer.TemplateLowererInitializer;
import com.cefriel.chimera.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TemplateLowererInitializerProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateLowererInitializerProcessor.class);

    private static Map<String, TemplateLowererInitializer> cache = new ConcurrentHashMap<>();
    private String loweringTemplate;
    private String baseUrl;

    @Override
    public void process(Exchange exchange) throws Exception {

        String cacheInvalidation = exchange.getMessage().getHeader(ProcessorConstants.CACHE_INVALIDATION, String.class);
        if (cacheInvalidation != null)
            if (cacheInvalidation.toLowerCase().equals("true")) {
                cache = new ConcurrentHashMap<>();
                logger.info("Cache invalidated.");
            }

        String templateId = "";
        List<String> templateUrls = new ArrayList<>();
        ConverterConfiguration configuration =
                exchange.getMessage().getHeader(ProcessorConstants.CONVERTER_CONFIGURATION, ConverterConfiguration.class);
        if (configuration != null) {
            logger.info("Converter configuration found in the exchange");
            templateId = configuration.getConverterId();
            templateUrls = configuration.getLoweringMappings().stream()
                    .filter(r -> r.getSerialization().equals(ProcessorConstants.RDF_LOWERER_SERIALIZATION_KEY))
                    .map(r -> r.getUrl())
                    .collect(Collectors.toList());
        }
        if (templateUrls.isEmpty()) {
            templateId = exchange.getMessage().getHeader(TemplateProcessorConstants.LOWERING_TEMPLATE, String.class);
            if (templateId == null)
                templateId = loweringTemplate;
            if (templateId == null)
                throw new IllegalArgumentException("Lowering Template not specified. Cannot create Template Lowerer Initializer.");
            templateUrls.add(Utils.trailingSlash(baseUrl) + templateId);
        }

        TemplateLowererInitializer initializer = null;
        synchronized (cache) {
            initializer = cache.get(templateId);
            if (initializer != null) {
                logger.info("Cached initializer used for: " + templateId);
                exchange.getMessage().setBody(initializer, TemplateLowererInitializer.class);
                return;
            }
        }

        //TODO Create Initializer for multiple templates
        String templateUrl = templateUrls.get(0);
        String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

        InputStream tlIS = UniLoader.open(templateUrl, token);
        if (tlIS == null) {
            logger.error("Lowering Template not found. Cannot create Template Lowerer Initializer.");
            exchange.getMessage().setBody(null);
            return;
        }

        logger.info("Template Lowerer Initializer created");
        initializer = new TemplateLowererInitializer(tlIS);
        cache.put(templateId, initializer);
        exchange.getMessage().setBody(initializer, TemplateLowererInitializer.class);

    }

    public String getLoweringTemplate() {
        return loweringTemplate;
    }

    public void setLoweringTemplate(String loweringTemplate) {
        this.loweringTemplate = loweringTemplate;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


}
