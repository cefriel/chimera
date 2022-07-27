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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class GraphInferenceTest extends CamelTestSupport {

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

                from("graph://get")
                        .to("graph://add?rdfFormat=turtle&resources=file://./src/test/resources/file/template/my-source.ttl")
                        .to("graph://add?rdfFormat=turtle&resources=file://./src/test/resources/file/template/enrich.ttl")
                        .to("graph://inference?ontologyFormat=rdfxml&resources=file://./src/test/resources/file/ontologies/ontology.owl")
                        .to("mock:inference");
            }
        };
    }
}