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
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MaptTemplateAgencyTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;
    private static ChimeraResourcesBean triples;
    private static ChimeraResourcesBean triples2;
    private static ChimeraResourceBean template;
    private static ChimeraResourceBean templateMultiple;
    @BeforeAll
    static void fillBeans(){
        ChimeraResourceBean r = new ChimeraResourceBean(
                "file://./src/test/resources/file/agency/input.ttl",
                "turtle");
        triples = new ChimeraResourcesBean(List.of(r));
        ChimeraResourceBean r2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/agency-multiple-input/input2.ttl",
                "turtle");
        triples2 = new ChimeraResourcesBean(List.of(r, r2));

        template = new ChimeraResourceBean(
                "file://./src/test/resources/file/agency/template.vm",
                "");
        templateMultiple = new ChimeraResourceBean(
                "file://./src/test/resources/file/agency-multiple-input/template.vm",
                "");
    }

    @Test
    public void testRdfTemplateAgency() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:rdfAgency");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Test
    public void testRdfMultipleTemplateAgency() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:rdfMultipleAgency");
        mock.expectedMessageCount(1);
        ChimeraResourceBean file = new ChimeraResourceBean(
                "file://./src/test/resources/file/agency-multiple-input/input.ttl",
                "turtle");
        start.sendBody(ResourceAccessor.open(file, null));
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("template", template);
                getCamelContext().getRegistry().bind("triples2", triples2);
                getCamelContext().getRegistry().bind("templateMultiple", templateMultiple);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("mapt://rdf?template=#bean:template&basePath=./src/test/resources/file/result&fileName=agency.csv")
                        .to("mock:rdfAgency");

                from("direct:start")
                        .to("graph://get?rdfFormat=turtle")
                        .to("graph://add?chimeraResources=#bean:triples2")
                        .to("mapt://rdf?template=#bean:templateMultiple&basePath=./src/test/resources/file/result&filename=multipleAgency.csv")
                        .to("mock:rdfMultipleAgency");
            }
        };
    }

}
