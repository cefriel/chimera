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

package com.cefriel.rdf;

import com.cefriel.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
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

        String cacheInvalidation = exchange.getMessage().getHeader(ChimeraConstants.CACHE_INVALIDATION, String.class);
        if (cacheInvalidation != null)
            if (cacheInvalidation.toLowerCase().equals("true")) {
                cache = new ConcurrentHashMap<>();
                logger.info("Cache invalidated.");
            }

        String templateId = "";
        List<String> templateUrls = new ArrayList<>();
        ConverterConfiguration configuration =
                exchange.getMessage().getHeader(ChimeraConstants.CONVERTER_CONFIGURATION, ConverterConfiguration.class);
        if (configuration != null && configuration.getLoweringMappings() != null) {
            logger.info("Converter configuration found in the exchange, lowering mappings extracted");
            templateId = configuration.getConverterId();
            templateUrls = configuration.getLoweringMappings().stream()
                    .filter(r -> r.getSerialization().equals(RdfTemplateConstants.RDF_LOWERER_SERIALIZATION_KEY))
                    .map(r -> r.getUrl())
                    .collect(Collectors.toList());
        }
        if (templateUrls.isEmpty()) {
            templateId = exchange.getMessage().getHeader(RdfTemplateConstants.LOWERING_TEMPLATE, String.class);
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

        List<InputStream> templateStreams = new ArrayList<>();
        String token = exchange.getProperty(ChimeraConstants.JWT_TOKEN, String.class);

        for (String url : templateUrls) {
            InputStream tlIS = UniLoader.open(url, token);
            if (tlIS == null)
                throw new IllegalArgumentException("Lowering Template not found. Cannot create Template Lowerer Initializer.");
            templateStreams.add(tlIS);
        }

        logger.info("Template Lowerer Initializer created");
        initializer = new TemplateLowererInitializer(templateStreams);
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
