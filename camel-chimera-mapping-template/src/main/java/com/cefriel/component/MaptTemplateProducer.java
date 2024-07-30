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
import com.cefriel.template.TemplateExecutor;
import com.cefriel.util.MaptTemplateConstants;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class  MaptTemplateProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(MaptTemplateProducer.class);
    private final MaptTemplateEndpoint endpoint;
    private TemplateExecutor templateExecutor;

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

        if (this.templateExecutor == null) {
            // check if the input format for the readers is supported
            if (Set.of("rdf", "xml", "json", "csv", "readers", "").contains(endpoint.getName())) {
                this.templateExecutor = MaptTemplateProcessor.templateExecutor(exchange, operationConfig, endpoint.getName());
                MaptTemplateProcessor.execute(exchange, operationConfig, endpoint.getName(), this.templateExecutor);
            }
            else
                throw new IllegalArgumentException("Invalid INPUT FORMAT: " + endpoint.getName());
        }
    }
}
