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

import com.cefriel.util.*;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RmlPollEnrichTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourcesBean mappingsRML;
    @BeforeAll
    static void fillBean(){
        var mapping = new ChimeraResourceBean(
                "file://./src/test/resources/file/lifting/mapping.rml.ttl",
                "turtle");
        mappingsRML = new ChimeraResourcesBean(List.of(mapping));
    }

    @Test
    public void testRmlPollEnrich() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:rmlPollEnrich");
        mock.expectedMessageCount(1);

        var r = new ChimeraResourceBean("file://./src/test/resources/file/sample-gtfs/stops.txt", null);
        start.sendBody(ResourceAccessor.open(r, camelTestSupportExtension.context()));

        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("mappingsRML", mappingsRML);

                from("direct:start")
                        .pollEnrich("graph://get", new RmlLiftingAggregationStrategy())
                        .to("rml://?streamName=stops.txt&mappings=#bean:mappingsRML")
                        .to("graph://dump?basePath=src/test/resources/file/result&dumpFormat=turtle&filename=rmlEnrich2Result")
                        .to("mock:rmlPollEnrich");
            }
        };
    }
}
