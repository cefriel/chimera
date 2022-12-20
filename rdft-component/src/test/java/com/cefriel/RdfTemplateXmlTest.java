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

import com.cefriel.component.RdfTemplateBean;
import com.cefriel.util.UniLoader;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
// todo change to non-secret xmls
public class RdfTemplateXmlTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    static RdfTemplateBean rdfBean = new RdfTemplateBean();

    @BeforeAll
    static void fillBean(){
        rdfBean.setTemplatePath("file://./src/test/resources/file/secret/demo-lifting.vm");
        rdfBean.setBasePath("src/test/resources/file/secret/result");
        rdfBean.setFilename("result.ttl");
    }

    @Test
    public void testRdfTemplateXml() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:rdfXml");
        mock.expectedMessageCount(1);

        start.sendBody(UniLoader.open("file://./src/test/resources/file/secret/base-gen.xml"));

        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("rdfConfig", rdfBean);

                from("direct:start")
                        .to("rdft://xml?rdfBaseConfig=#bean:rdfConfig")
                        .to("mock:rdfXml");
            }
        };
    }
}
