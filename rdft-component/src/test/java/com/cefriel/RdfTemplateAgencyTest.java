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
import com.cefriel.util.UniLoader;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RdfTemplateAgencyTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    static GraphBean bean = new GraphBean();
    static RdfTemplateBean rdfBean = new RdfTemplateBean();

    @BeforeAll
    static void fillBeans(){
        List<String> urls = new ArrayList<>();
        urls.add("file://./src/test/resources/file/agency/input.ttl");
        bean.setResources(urls);
        bean.setRdfFormat("turtle");
        rdfBean.setTemplatePath("file://./src/test/resources/file/agency/template.vm");
        rdfBean.setBasePath("src/test/resources/file/result");
        rdfBean.setFilename("agency.csv");
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

        start.sendBody(UniLoader.open("file://./src/test/resources/file/agency-multiple-input/input.ttl"));

        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("config", bean);
                getCamelContext().getRegistry().bind("rdfConfig", rdfBean);

                from("graph://get?baseConfig=#bean:config")
                        .to("graph://config?baseConfig=#bean:config")
                        .to("graph://add")
                        .to("rdft://rdf?rdfBaseConfig=#bean:rdfConfig")
                        .to("mock:rdfAgency");

                from("direct:start")
                        .to("graph://get?rdfFormat=turtle")
                        .to("graph://add?resources=file://./src/test/resources/file/agency-multiple-input/input2.ttl&rdfFormat=turtle")
                        .to("rdft://rdf?templatePath=file://./src/test/resources/file/agency/template.vm&basePath=src/test/resources/file/result&filename=multipleAgency.csv")
                        .to("mock:rdfMultipleAgency");

            }
        };
    }

}