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

import com.cefriel.graph.*;
import com.cefriel.operations.GraphGet;
import com.cefriel.operations.GraphObtain;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphConsumer extends DefaultConsumer {
    private final GraphEndpoint endpoint;
    private static final Logger LOG = LoggerFactory.getLogger(GraphConsumer.class);

    public GraphConsumer(GraphEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }
    // todo only allow get

    @Override
    protected void doStart() throws Exception {

        super.doStart();
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
        // exchange.getMessage().setHeader(ChimeraConstants.CONFIGURATION, operationConfig);
        // RDFGraph graph = GraphGet.graphCreate(exchange);
        var rdfGraphAndExchange = GraphObtain.obtainGraph(exchange, operationConfig);
        RDFGraph graph = rdfGraphAndExchange.graph();
        Exchange ex = rdfGraphAndExchange.exchange();
        ex.getMessage().setBody(graph);
        // exchange.getMessage().setBody(graph);

        try {
            // send message to next processor in the route
            getProcessor().process(ex);
        } catch (Exception e) {
            exchange.setException(e);
        } finally {
            if (exchange.getException() != null) {
                getExceptionHandler().handleException("Error processing exchange", ex, ex.getException());
            }
            releaseExchange(ex, false);
        }
        doStop();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }
}
