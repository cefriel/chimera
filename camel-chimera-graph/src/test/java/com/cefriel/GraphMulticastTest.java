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
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphMulticastTest extends CamelTestSupport {

    static ChimeraResourcesBean queries;
    static ChimeraResourcesBean triples1;
    static ChimeraResourcesBean triples2;

    @BeforeAll
    static void fillBean(){
        queries = new ChimeraResourcesBean(List.of(
                new ChimeraResourceBean(
                        "file://./src/test/resources/file/construct/construct.txt",
                        "turtle")));

        triples1 = new ChimeraResourcesBean(List.of((
                new ChimeraResourceBean(
                        "file://./src/test/resources/file/template/my-source.ttl",
                        "turtle"))));

        triples2 = new ChimeraResourcesBean(List.of((
                new ChimeraResourceBean(
                        "file://./src/test/resources/file/template/enrich.ttl",
                        "turtle"))));
    }

    @Test
    public void testMulticast() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:multicast");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("queries", queries);
                getCamelContext().getRegistry().bind("triples1", triples1);
                getCamelContext().getRegistry().bind("triples2", triples2);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples1")
                        .to("graph://construct?chimeraResources=#bean:queries")
                        .multicast()
                        .to("graph://dump?filename=beforeEnrich&basePath=src/test/resources/file/result&dumpFormat=turtle")
                        .to("graph://add?chimeraResources=#bean:triples2")
                        .to("graph://dump?filename=afterEnrich&basePath=src/test/resources/file/result&dumpFormat=turtle")
                        .to("graph://detach?clear=true&routeOff=true")
                        .end()
                        .to("mock:multicast");
            }
        };
    }
}
