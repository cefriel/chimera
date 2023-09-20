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
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MaptTemplateCsvTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    @Produce("direct:foo")
    ProducerTemplate foo;

    private static ChimeraResourceBean template;
    private static ChimeraResourceBean templateNoInput;

    @BeforeAll
    static void fillBean(){
        template = new ChimeraResourceBean("file://./src/test/resources/file/csv/template.vm", "");
        templateNoInput = new ChimeraResourceBean("file://./src/test/resources/file/csv/template-no-input.vm", "");
    }

    @Test
    public void testRdfTemplateCsv() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rdfCsv");
        mock.expectedMessageCount(1);

        ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/csv/example.csv", "csv");
        start.sendBody(ResourceAccessor.open(r, null));

        mock.assertIsSatisfied();
        long filesEqual = Files.mismatch(Paths.get("./src/test/resources/file/csv/output-correct.ttl"),
                Paths.get(("./src/test/resources/file/result/output-csv.ttl")));
        boolean correctOutput = filesEqual == -1;
        assert (correctOutput);
    }

    @Test
    public void testRdfTemplateCsvNoInput() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rdfCsvNoInput");
        mock.expectedMessageCount(1);

        // ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/csv/example.csv", "csv");
        // foo.sendBody(ResourceAccessor.open(r, camelTestSupportExtension.context()));
        foo.send(ExchangeBuilder.anExchange(context()).build());
        mock.assertIsSatisfied();
        long filesEqual = Files.mismatch(Paths.get("./src/test/resources/file/csv/output-correct.ttl"),
                Paths.get(("./src/test/resources/file/result/output-csv-no-input.ttl")));
        boolean correctOutput = filesEqual == -1;
        assert (correctOutput);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                getCamelContext().getRegistry().bind("template", template);
                getCamelContext().getRegistry().bind("templateNoInput", templateNoInput);

                from("direct:start")
                        .to("mapt://csv?template=#bean:template&basePath=./src/test/resources/file/result&fileName=output-csv.ttl")
                        .to("mock:rdfCsv");

                from("direct:foo")
                        .to("mapt://?template=#bean:templateNoInput&basePath=./src/test/resources/file/result&fileName=output-csv-no-input.ttl")
                        .to("mock:rdfCsvNoInput");
            }
        };
    }
}
