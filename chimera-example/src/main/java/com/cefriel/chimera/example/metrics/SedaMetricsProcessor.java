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

package com.cefriel.chimera.example.metrics;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.seda.SedaEndpoint;

public class SedaMetricsProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String endpoint = exchange.getMessage().getHeader("seda_endpoint", String.class);
        CamelContext camelContext = exchange.getContext();
        SedaEndpoint seda = (SedaEndpoint) camelContext.getEndpoint(endpoint);
        int queueSize = seda.getCurrentQueueSize();

        exchange.getMessage().setHeader("seda_queue_size", queueSize);
    }
}
