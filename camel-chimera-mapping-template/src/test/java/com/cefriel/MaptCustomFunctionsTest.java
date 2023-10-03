package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MaptCustomFunctionsTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourceBean template;
    private static ChimeraResourceBean templateFunctions;

    @BeforeAll
    static void fillBeans(){
        template = new ChimeraResourceBean(
                "file://./src/test/resources/custom-functions/template.vm",
                "vtl");
        templateFunctions = new ChimeraResourceBean(
                "file://./src/test/resources/custom-functions/CustomFunctions.java",
                "java");
    }

    @Test
    public void testCustomFunctions() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:customFunctionsTest");
        mock.expectedMessageCount(1);
        start.sendBody(template);
        mock.assertIsSatisfied();
        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        // the custom function only prints "test"
        assert(result.equals("test"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("template", template);
                getCamelContext().getRegistry().bind("templateFunctions", templateFunctions);

                from("direct:start")
                        .to("mapt://?template=#bean:template&templateFunctions=#bean:templateFunctions")
                        .to("mock:customFunctionsTest");
            }
        };
    }
}
