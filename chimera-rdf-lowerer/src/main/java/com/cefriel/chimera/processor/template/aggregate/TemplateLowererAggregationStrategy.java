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

public class TemplateLowererAggregationStrategy implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(TemplateLowererAggregationStrategy.class);

    private TemplateLowererOptions tlOptions;
    private boolean stream;

    public Exchange aggregate(Exchange exchange, Exchange tlInitializer) {
        TemplateLowererProcessor processor = new TemplateLowererProcessor();
        if (tlOptions != null)
            processor.setDefaultTLOptions(tlOptions);
        if (tlInitializer != null) {
            TemplateLowererInitializer initializer = tlInitializer.getIn().getBody(TemplateLowererInitializer.class);
            if (initializer != null) {
                logger.info("Template Lowerer Initialization extracted");
                InputStream tlIS = null;
                try {
                    tlIS = initializer.getTemplateStream();
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
                    String templatePath = "./tmp/tmp-template-" + exchange.getExchangeId() + ".vm";
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
                    processor.setStream(stream);
                    exchange.setProperty(TemplateProcessorConstants.TEMPLATE_STREAM, tlIS);
                }
            }
        }
        try {
            processor.process(exchange);
        } catch (Exception e) {
            logger.error("Error in Template Lowering process");
            e.printStackTrace();
        }
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
}
