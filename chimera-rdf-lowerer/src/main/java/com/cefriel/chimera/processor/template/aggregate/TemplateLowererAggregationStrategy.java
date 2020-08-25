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
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateLowererAggregationStrategy implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(TemplateLowererAggregationStrategy.class);

    private TemplateLowererOptions tlOptions;

    public Exchange aggregate(Exchange exchange, Exchange tlInitializer) {
        TemplateLowererProcessor processor = new TemplateLowererProcessor();
        if (tlOptions != null)
            processor.setDefaultTLOptions(tlOptions);
        if (tlInitializer != null) {
            TemplateLowererInitializer initializer = tlInitializer.getIn().getBody(TemplateLowererInitializer.class);
            if (initializer != null) {
                logger.info("Template Lowerer Initialization extracted");
                exchange.getMessage().setHeader(TemplateProcessorConstants.TEMPLATE_PATH, initializer.getTemplatePath());
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
}
