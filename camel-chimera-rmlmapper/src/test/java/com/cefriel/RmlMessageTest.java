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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class RmlMessageTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourceBean mappingRML;

    @BeforeAll
    static void fillBean(){
        mappingRML = new ChimeraResourceBean(
                "file://./src/test/resources/file/lifting/mapping.rml.ttl",
                "turtle");
    }

    @Test
    public void testRmlMessage() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:rmlMessage");
        mock.expectedMessageCount(1);
        var r = new ChimeraResourceBean("file://./src/test/resources/file/sample-gtfs/stops.txt", null);
        start.sendBody(ResourceAccessor.open(r, null));

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                getCamelContext().getRegistry().bind("mappingRML", mappingRML);

                from("direct:start")
                        .to("rml://?streamName=stops.txt&mapping=#bean:mappingRML&useMessage=true&baseIri=http://example.org/")
                        .to("graph://dump?basePath=src/test/resources/file/result&dumpFormat=turtle&filename=rmlMessageResult")
                        .to("mock:rmlMessage");
            }
        };
    }
}
