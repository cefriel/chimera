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

package com.cefriel.aggregationStrategy;

import com.cefriel.component.RdfTemplateBean;
import com.cefriel.util.RdfTemplateConstants;
import com.cefriel.util.TemplateLowererInitializer;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

// todo can be removed
public class TemplateLowererAggregationStrategy implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(TemplateLowererAggregationStrategy.class);

    public Exchange aggregate(Exchange exchange, Exchange tlInitializer) {

        RdfTemplateBean configuration = exchange.getMessage().getHeader(RdfTemplateConstants.RDF_CONFIG, RdfTemplateBean.class);
        if (tlInitializer != null) {
            TemplateLowererInitializer initializer = tlInitializer.getMessage().getBody(TemplateLowererInitializer.class);
            if (initializer != null) {
                logger.info("Template Lowerer Initializer extracted");
                for(int k = 0; k < initializer.getNumberTemplates(); k++) {
                    InputStream tlIS = null;
                    try {
                        tlIS = initializer.getTemplateStream(k);
                    } catch (IOException e) {
                        logger.error("Exception accessing template stream");
                    }
                    if (configuration.isStream()) {
                        byte[] buffer = new byte[0];
                        try {
                            buffer = new byte[tlIS.available()];
                            tlIS.read(buffer);
                        } catch (IOException e) {
                            logger.error("Exception reading template stream");
                        }
                        String templatePath = "./tmp/tmp-template-" + exchange.getExchangeId() + "-" + k + ".vm";
                        File templateFile = new File(templatePath);
                        templateFile.getParentFile().mkdirs();
                        try {
                            OutputStream outStream = new FileOutputStream(templateFile);
                            outStream.write(buffer);
                            outStream.close();
                        } catch (IOException e) {
                            logger.error("Exception writing template stream to file");
                        }
                        exchange.getMessage().setHeader(RdfTemplateConstants.TEMPLATE_PATH, templatePath);
                    } else {
                        exchange.setProperty(RdfTemplateConstants.TEMPLATE_STREAM, tlIS);
                    }

                    try {
                        //RdfTemplateProcessor.execute(exchange, configuration);
                    } catch (Exception e) {
                        logger.error("Error in Template Lowering process");
                        e.printStackTrace();
                    }
                }
            }
        }

        exchange.getMessage().removeHeaders("*template*");
        return exchange;
    }

}