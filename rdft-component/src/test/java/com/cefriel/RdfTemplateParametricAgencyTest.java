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
import com.cefriel.component.RdfTemplateBean;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RdfTemplateParametricAgencyTest extends CamelTestSupport {

    private static ChimeraResourcesBean triples;
    private static ChimeraResourceBean template;
    private static ChimeraResourceBean query;

    @BeforeAll
    static void fillBeans(){
        ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/agency-parametric/input.ttl", "turtle");
        template = new ChimeraResourceBean("file://./src/test/resources/file/agency-parametric/template.vm", null);
        triples = new ChimeraResourcesBean(List.of(r));
        query = new ChimeraResourceBean("file://./src/test/resources/file/agency-parametric/query.txt", null);
    }

    @Test
    public void testRdfTemplateParametricAgency() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rdfParamAgency");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("template", template);
                getCamelContext().getRegistry().bind("query", query);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("rdft://rdf?template=#bean:template&query=#bean:query&basePath=./src/test/resources/file/result&fileName=agencyParametric.csv")
                        .to("mock:rdfParamAgency");
            }
        };
    }

}
