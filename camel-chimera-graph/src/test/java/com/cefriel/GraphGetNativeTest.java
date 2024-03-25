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

package com.cefriel;

import com.cefriel.graph.*;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GraphGetNativeTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(GraphGetNativeTest.class);
    @Test
    public void testNativeGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:native");

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(NativeRDFGraph.class));
        NativeRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(NativeRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        // assert(graph.getRepository().getClass().equals(SailRepository.class));
        assert(graph.getRepository().getDataDir().getPath().equals("tempDir"));

        mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class).getRepository().shutDown();
        FileUtils.deleteDirectory(new File("tempDir"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("graph://get?pathDataDir=tempDir")
		    .to("mock:native");
            }
        };
    }
}
