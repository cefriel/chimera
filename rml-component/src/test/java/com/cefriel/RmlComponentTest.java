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
import com.cefriel.util.ChimeraResourcesBean;
import com.cefriel.util.UniLoader;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RmlComponentTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    static GraphBean bean = new GraphBean();
    static RmlBean rmlBean = new RmlBean();

    @BeforeAll
    static void fillBean(){
        ChimeraResourcesBean maps = new ArrayList<>();
        maps.add("file://./src/test/resources/file/lifting/mapping.rml.ttl");
        rmlBean.setStreamName("stops.txt");
        rmlBean.setMappings(maps);
        rmlBean.setSingleRecordsFactory(true);
        rmlBean.setOrdered(true);
        bean.setBasePath("src/test/resources/file/result");
        bean.setDumpFormat("turtle");
        bean.setFilename("rmlResult");
    }

    @Test
    public void testRml() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        start.sendBody(UniLoader.open("file://./src/test/resources/file/sample-gtfs/stops.txt"));

        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("config", bean);
                getCamelContext().getRegistry().bind("rmlConfig", rmlBean);

                from("direct:start")
                        .to("rml://?rmlBaseConfig=#bean:rmlConfig")
                        .to("graph://dump?baseConfig=#bean:config")
                        .to("mock:result");
            }
        };
    }
}
