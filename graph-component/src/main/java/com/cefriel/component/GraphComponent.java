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

import java.util.Map;

import com.cefriel.component.GraphEndpoint;
import org.apache.camel.Endpoint;

import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.apache.camel.spi.annotations.Component("graph")
public class GraphComponent extends DefaultComponent {
    private static final Logger LOG = LoggerFactory.getLogger(GraphComponent.class);
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Endpoint endpoint = new GraphEndpoint(uri, remaining, this);
        setProperties(endpoint, parameters);
        LOG.info("created endpoint " +endpoint);
        LOG.info(endpoint.getEndpointBaseUri());
        return endpoint;
    }
}
