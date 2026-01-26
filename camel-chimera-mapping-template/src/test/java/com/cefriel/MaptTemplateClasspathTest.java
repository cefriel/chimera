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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MaptTemplateClasspathTest extends CamelTestSupport {

    private static ChimeraResourceBean classpathTemplate;

    @BeforeAll
    static void fillBeans() {
        classpathTemplate = new ChimeraResourceBean(
                "classpath://classpath/template.vm",
                "");
    }

    @Test
    public void testClasspathTemplateMapping() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:classpathResult");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        assertTrue(result.contains("Hello World"), "Result should contain 'Hello World' from classpath template");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                getCamelContext().getRegistry().bind("classpathTemplate", classpathTemplate);

                from("graph://get")
                        .to("mapt://rdf?template=#bean:classpathTemplate")
                        .to("mock:classpathResult");
            }
        };
    }
}
