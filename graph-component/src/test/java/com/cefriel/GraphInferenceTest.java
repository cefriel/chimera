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

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class GraphInferenceTest extends CamelTestSupport {
    private static ChimeraResourcesBean ontologies;
    private static ChimeraResourcesBean triples;
    @BeforeAll
    static void fillBean(){
        ChimeraResourceBean r1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/ontologies/ontology.owl",
                "rdfxml");
        ontologies = new ChimeraResourcesBean(List.of(r1));

        ChimeraResourceBean r2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/my-source.ttl",
                "turtle");
        ChimeraResourceBean r3 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/enrich.ttl",
                "turtle");
        triples = new ChimeraResourcesBean(List.of(r2,r3));
    }

    @Test
    public void testInference() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:inference");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("ontologies", ontologies);
                getCamelContext().getRegistry().bind("triples", triples);
                // todo check this cant be right
                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:ontologies")
                        .to("graph://add?chimeraResources=#bean:ontologies")
                        .to("graph://inference?chimeraResources=#bean:ontologies")
                        .to("mock:inference");
            }
        };
    }
}