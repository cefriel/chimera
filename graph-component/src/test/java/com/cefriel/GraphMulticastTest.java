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
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphMulticastTest extends CamelTestSupport {

    static GraphBean bean = new GraphBean();

    @BeforeAll
    static void fillBean(){
        List<String> urls = new ArrayList<>();
        urls.add("file://./src/test/resources/file/base/test.ttl");
        bean.setResources(urls);
        bean.setRdfFormat("turtle");
        bean.setBasePath("src/test/resources/file/result");
        bean.setDumpFormat("turtle");
        bean.setClear(true);
        bean.setRouteOff(true);
    }

    @Test
    public void testMulticast() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:multicast");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("config", bean);

                from("graph://get?baseConfig=#bean:config")
                        .to("graph://config?baseConfig=#bean:config")
                        .setHeader(ChimeraConstants.RDF_FORMAT, constant("turtle"))
                        .to("graph://add")
                        .to("graph://add?resources=file://./src/test/resources/file/template/my-source.ttl")
                            .to("graph://construct?queryUrls=file://./src/test/resources/file/construct/construct.txt")
                        .multicast()
                        .to("graph://dump?filename=beforeEnrich")
                        .to("graph://add?resources=file://./src/test/resources/file/template/enrich.ttl")
                        .to("graph://dump?filename=afterEnrich")
                        .to("graph://detach")
                        .end()
                        .to("mock:multicast");
            }
        };
    }
}
