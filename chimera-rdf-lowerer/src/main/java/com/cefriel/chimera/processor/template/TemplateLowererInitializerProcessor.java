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
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.TemplateProcessorConstants;
import com.cefriel.chimera.util.UniLoader;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class TemplateLowererInitializerProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(TemplateLowererInitializerProcessor.class);

    private static Map<String, TemplateLowererInitializer> cache = new HashMap<>();
    private String loweringTemplate;
    private String baseUrl;

    @Override
    public void process(Exchange exchange) throws Exception {

        String templateId = exchange.getMessage().getHeader(TemplateProcessorConstants.LOWERING_TEMPLATE, String.class);
        if (templateId != null)
            loweringTemplate = templateId;
        if (loweringTemplate == null) {
            logger.error("Lowering Template not specified. Cannot create Template Lowerer Initializer.");
            exchange.getMessage().setBody(null);
            return;
        }

        TemplateLowererInitializer initializer = null;
        synchronized (cache) {
            initializer = cache.get(loweringTemplate);
            if (initializer != null) {
                logger.info("Cached initializer used for: " + loweringTemplate);
                exchange.getMessage().setBody(initializer, TemplateLowererInitializer.class);
                return;
            }
        }

        if (baseUrl == null)
            baseUrl = "";
        baseUrl = Utils.trailingSlash(baseUrl);
        String templateUrl = baseUrl + loweringTemplate;
        String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

        InputStream tlIS = UniLoader.open(templateUrl, token);
        if (tlIS == null) {
            logger.error("Lowering Template not found. Cannot create Template Lowerer Initializer.");
            exchange.getMessage().setBody(null);
            return;
        }

        byte[] buffer = new byte[tlIS.available()];
        tlIS.read(buffer);
        String templatePath = "./tmp/" + loweringTemplate + ".vm";
        File templateFile = new File(templatePath);
        templateFile.getParentFile().mkdirs();
        OutputStream outStream = new FileOutputStream(templateFile);
        outStream.write(buffer);

        logger.info("Template Lowerer Initializer created");
        initializer = new TemplateLowererInitializer(templatePath);
        if (initializer != null)
            cache.put(loweringTemplate, initializer);
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
