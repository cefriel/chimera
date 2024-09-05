package com.cefriel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class OperationParameterTest extends CamelTestSupport {

    @Test
    public void testOperationParameter() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:operation");
        mock.expectedMessageCount(3);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() throws Exception {

                from("graph://?operation=get")
                        .to("mock:operation");

                from("graph://?operation=Get")
                        .to("mock:operation");

                from("graph://?operation=GET")
                        .to("mock:operation");
            }
        };
    }
}
