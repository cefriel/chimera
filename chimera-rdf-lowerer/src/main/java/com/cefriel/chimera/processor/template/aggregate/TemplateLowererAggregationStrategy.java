/*
 * Copyright (c) 2019-2021 Cefriel.
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

package com.cefriel.chimera.processor.template.aggregate;

import com.cefriel.chimera.lowerer.TemplateLowererInitializer;
import com.cefriel.chimera.processor.template.TemplateLowererOptions;
import com.cefriel.chimera.processor.template.TemplateLowererProcessor;
import com.cefriel.chimera.util.TemplateProcessorConstants;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TemplateLowererAggregationStrategy implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(TemplateLowererAggregationStrategy.class);

    private TemplateLowererOptions tlOptions;
    private boolean stream;
    private boolean attachmentToExchange;

    public Exchange aggregate(Exchange exchange, Exchange tlInitializer) {
        TemplateLowererProcessor processor = new TemplateLowererProcessor();
        if (tlOptions != null)
            processor.setDefaultTLOptions(tlOptions);
        if (tlInitializer != null) {
            TemplateLowererInitializer initializer = tlInitializer.getIn().getBody(TemplateLowererInitializer.class);
            if (initializer != null) {
                logger.info("Template Lowerer Initializer extracted");
                for(int k = 0; k < initializer.getNumberTemplates(); k++) {
                    InputStream tlIS = null;
                    try {
                        tlIS = initializer.getTemplateStream(k);
                    } catch (IOException e) {
                        logger.error("Exception accessing template stream");
                    }
                    if (!stream) {
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
                        exchange.getMessage().setHeader(TemplateProcessorConstants.TEMPLATE_PATH, templatePath);
                    } else {
                        processor.setStream(true);
                        exchange.setProperty(TemplateProcessorConstants.TEMPLATE_STREAM, tlIS);
                    }

                    if(initializer.getNumberTemplates() > 1)
                        exchange.setProperty(TemplateProcessorConstants.DEST_FILE_NAME_ID, Integer.toString(k));

                    processor.setAttachmentToExchange(attachmentToExchange);

                    try {
                        processor.process(exchange);
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

    public TemplateLowererOptions getTlOptions() {
        return tlOptions;
    }

    public void setTlOptions(TemplateLowererOptions tlOptions) {
        this.tlOptions = tlOptions;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public boolean isAttachmentToExchange() {
        return attachmentToExchange;
    }

    public void setAttachmentToExchange(boolean attachmentToExchange) {
        this.attachmentToExchange = attachmentToExchange;
    }
}
