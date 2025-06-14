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

import com.cefriel.operations.GraphGet;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ScheduledPollConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphConsumer extends ScheduledPollConsumer {
    private final GraphEndpoint endpoint;
    private static final Logger LOG = LoggerFactory.getLogger(GraphConsumer.class);

    public GraphConsumer(GraphEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected int poll() throws Exception {
        final Exchange exchange = createExchange(false);
        GraphBean operationConfig;
        if (endpoint.getBaseConfig() != null) {
            operationConfig = new GraphBean(endpoint.getBaseConfig());
            exchange.getMessage().setHeader(ChimeraConstants.BASE_CONFIGURATION, endpoint.getBaseConfig());
            LOG.info("getting a base configuration");
        } else {
            operationConfig = new GraphBean();
        }
        operationConfig.setEndpointParameters(endpoint);

        if ("get".equals(endpoint.getName())) {
            GraphGet.obtainGraph(exchange, operationConfig);
            try {
                getProcessor().process(exchange);
            } catch (Exception e) {
                exchange.setException(e);
            } finally {
                if (exchange.getException() != null) {
                    getExceptionHandler().handleException("Error processing exchange", exchange, exchange.getException());
                }
                releaseExchange(exchange, false);
            }
            return 1; // number of polled exchanges
        } else {
            throw new UnsupportedOperationException("Graph Consumer only allows GET operations");
        }
    }
}