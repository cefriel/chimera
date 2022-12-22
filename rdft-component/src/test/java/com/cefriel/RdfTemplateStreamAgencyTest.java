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
import com.cefriel.util.RdfTemplateConstants;
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RdfTemplateStreamAgencyTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;
    private static ChimeraResourcesBean triples;
    private static ChimeraResourceBean template;

    @BeforeAll
    static void fillBeans(){
        ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/agency/input.ttl", "turtle");
        triples = new ChimeraResourcesBean(List.of(r));
        template = new ChimeraResourceBean("file://./src/test/resources/file/agency/template.vm", null);
    }

    @Test
    public void testRdfTemplateAgencyStream() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rdfStreamAgency");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception{
                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("template", template);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .setProperty(RdfTemplateConstants.TEMPLATE_STREAM, constant(ResourceAccessor.open(template, context())))
                        .to("rdft://rdf?stream=true")
                        .to("mock:rdfStreamAgency");
            }
        };
    }
}
