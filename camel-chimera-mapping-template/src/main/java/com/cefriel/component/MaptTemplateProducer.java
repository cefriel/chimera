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

package com.cefriel.component;

import com.cefriel.rdf.MaptTemplateProcessor;
import com.cefriel.util.MaptTemplateConstants;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaptTemplateProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(MaptTemplateProducer.class);
    private final MaptTemplateEndpoint endpoint;

    public MaptTemplateProducer(MaptTemplateEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }
    public void process(Exchange exchange) throws Exception {
        final MaptTemplateBean operationConfig;

        if (endpoint.getRdfBaseConfig() != null) {
            operationConfig = new MaptTemplateBean(endpoint.getRdfBaseConfig());
        } else if (exchange.getMessage().getHeader(MaptTemplateConstants.RDF_BASECONFIG) != null) {
            operationConfig = new MaptTemplateBean(exchange.getMessage().getHeader(MaptTemplateConstants.RDF_BASECONFIG, MaptTemplateBean.class));
            LOG.info("Configuration from exchange");
        } else {
            operationConfig = new MaptTemplateBean();
        }
        operationConfig.setConfig(endpoint);
        switch (endpoint.getName()){
            case "rdf" -> MaptTemplateProcessor.execute(exchange, operationConfig, "rdf");
            case "xml" -> MaptTemplateProcessor.execute(exchange, operationConfig, "xml");
            case "json" -> MaptTemplateProcessor.execute(exchange, operationConfig, "json");
            case "csv" -> MaptTemplateProcessor.execute(exchange, operationConfig, "csv");
            case "" -> MaptTemplateProcessor.execute(exchange, operationConfig, null);
            default -> throw new IllegalArgumentException("Invalid INPUT FORMAT: " + endpoint.getName());
        }
    }
}
