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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RdfTemplateStreamAgencyTest extends CamelTestSupport {

    static GraphBean bean = new GraphBean();
    static RdfTemplateBean rdfBean = new RdfTemplateBean();

    @BeforeAll
    static void fillBeans(){
        List<String> urls = new ArrayList<>();
        urls.add("file://./src/test/resources/file/agency/input.ttl");
        bean.setResources(urls);
        bean.setRdfFormat("turtle");
        rdfBean.setTemplatePath("file://./src/test/resources/file/agency/template.vm");
        rdfBean.setStream(true);
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

                getCamelContext().getRegistry().bind("config", bean);
                getCamelContext().getRegistry().bind("rdfConfig", rdfBean);

                from("graph://get")
                        .to("graph://add?baseConfig=#bean:config")
                        .to("rdft://rdf?rdfBaseConfig=#bean:rdfConfig")
                        .to("mock:rdfStreamAgency");

            }
        };
    }
}
