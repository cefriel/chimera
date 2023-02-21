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
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import com.cefriel.util.UniLoader;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RdfTemplateXmlTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourceBean template;

    @BeforeAll
    static void fillBean(){
        template = new ChimeraResourceBean("file://./src/test/resources/file/xml/template.vm", "");
    }
    @Test
    public void testRdfTemplateXml() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rdfXml");
        mock.expectedMessageCount(1);

        ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/xml/example.xml", "xml");
        start.sendBody(ResourceAccessor.open(r, camelTestSupportExtension.context()));

        mock.assertIsSatisfied();
        long filesEqual = Files.mismatch(Paths.get("./src/test/resources/file/xml/output-correct.ttl"),
                Paths.get(("./src/test/resources/file/result/output-xml.ttl")));
        boolean correctOutput = filesEqual == -1;
        assert (correctOutput);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getCamelContext().getRegistry().bind("template", template);

                from("direct:start")
                        .to("mapt://xml?template=#bean:template&basePath=./src/test/resources/file/result&fileName=output-xml.ttl")
                        .to("mock:rdfXml");
            }
        };
    }
}
