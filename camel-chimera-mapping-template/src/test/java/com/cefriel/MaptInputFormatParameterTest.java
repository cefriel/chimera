package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MaptInputFormatParameterTest extends CamelTestSupport {

    private static ChimeraResourceBean template;

    @BeforeAll
    static void fillBeans(){
        template = new ChimeraResourceBean("file://./src/test/resources/template.vm", null);
    }

    @Test
    public void testInputFormatParameter() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:inputformat");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("template", template);

                from("graph://get")
                        .to("mapt://csv?inputFormat=csv&template=#bean:template")
                        .to("mock:inputformat");
            }
        };
    }
}
