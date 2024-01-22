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

import com.cefriel.component.GraphBean;
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphAddTest extends CamelTestSupport {

    private static ChimeraResourceBean triples;
    private static ChimeraResourceBean triples2;
    @BeforeAll
    static void fillBean(){
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/template_agency.ttl",
                "turtle");

        triples2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/random.ttl",
                "turtle");
    }

    @Test
    public void testAdd() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:add");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert (graph.getRepository().isInitialized());
        assert (graph.getRepository().getConnection().size() > 0);
    }

    @Test
    public void testAdd2() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:add2");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert (graph.getRepository().isInitialized());
        assert (graph.getRepository().getConnection().size() > 0);
    }
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("triples2", triples2);

                from("graph://get")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("mock:add");

                from("graph://get?namedGraph=http://example.org/Picasso")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("mock:add2");
            }
        };
    }
}
