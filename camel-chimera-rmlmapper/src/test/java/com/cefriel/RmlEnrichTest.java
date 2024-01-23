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
import com.cefriel.component.RmlBean;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import com.cefriel.util.RmlLiftingAggregationStrategy;
import com.cefriel.util.UniLoader;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RmlEnrichTest extends CamelTestSupport {

    private static ChimeraResourceBean triples;
    private static ChimeraResourceBean mappingRML;
    @BeforeAll
    static void fillBean(){
        mappingRML = new ChimeraResourceBean(
                "file://./src/test/resources/file/lifting/mapping.rml.ttl",
                "turtle");
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/base/test.ttl",
                "turtle");
    }

    @Test
    public void testRmlEnrich() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rmlEnrich");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("mappingRML", mappingRML);

                interceptSendToEndpoint("direct:adding").skipSendToOriginalEndpoint()
                        .process(exchange -> exchange.getMessage().setBody(UniLoader.open("file://./src/test/resources/file/sample-gtfs/stops.txt")))
                        .log("intercepted");

                from("graph://get")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .enrich("direct:adding", new RmlLiftingAggregationStrategy())
                        .to("rml://?streamName=stops.txt&mapping=#bean:mappingRML&ordered=true&singleRecordsFactory=true")
                        .to("graph://dump?dumpFormat=turtle&basePath=src/test/resources/file/result&filename=rmlEnrich1Result")
                        .to("mock:rmlEnrich");
            }
        };
    }
}
