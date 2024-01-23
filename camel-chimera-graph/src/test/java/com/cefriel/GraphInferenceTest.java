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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphInferenceTest extends CamelTestSupport {
    private static ChimeraResourceBean ontology;
    private static ChimeraResourceBean triples1;
    private static ChimeraResourceBean triples2;
    @BeforeAll
    static void fillBean(){
        ontology = new ChimeraResourceBean(
                "file://./src/test/resources/file/ontologies/ontology.owl",
                "rdfxml");
        triples1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/my-source.ttl",
                "turtle");
        triples2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/enrich.ttl",
                "turtle");
    }

    @Test
    public void testInference() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:inference");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        // todo check that the inferred result is correct
    }
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("ontology", ontology);
                getCamelContext().getRegistry().bind("triples1", triples1);
                getCamelContext().getRegistry().bind("triples2", triples2);

                from("graph://get")
                        .to("graph://add?chimeraResource=#bean:triples1")
                        .to("graph://add?chimeraResource=#bean:triples2")
                        .to("graph://inference?chimeraResource=#bean:ontology")
                        .to("mock:inference");
            }
        };
    }
}
