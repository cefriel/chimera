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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphConstructTest extends CamelTestSupport {

    static GraphBean bean = new GraphBean();

    @BeforeAll
    static void fillBean(){
        List<String> urls = new ArrayList<>();
        bean.setNamedGraph("http://example.org/Picasso");
        urls.add("file://./src/test/resources/file/base/test.ttl");
        bean.setResources(urls);
        bean.setRdfFormat("turtle");
        bean.setBasePath("src/test/resources/file/result");
        bean.setDumpFormat("turtle");
    }

    @Test
    public void testConstructNew() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructNew");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Test
    public void testConstructOld() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructOld");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                getCamelContext().getRegistry().bind("config", bean);

                from("graph://get")
                        .to("graph://config?baseConfig=#bean:config")
                        .to("graph://add")
                        .to("graph://construct?newGraph=true&resources=file://./src/test/resources/file/construct/construct.txt")
                        .to("graph://dump?filename=newConstructDump")
                        .to("mock:constructNew");

                from("graph://get?namedGraph=http://example.org/Picasso")
                        .to("graph://config?baseConfig=#bean:config")
                        .to("graph://add")
                        .to("graph://construct?resources=file://./src/test/resources/file/construct/construct.txt")
                        .to("graph://dump?filename=oldConstructDump")
                        .to("mock:constructOld");
            }
        };
    }
}
