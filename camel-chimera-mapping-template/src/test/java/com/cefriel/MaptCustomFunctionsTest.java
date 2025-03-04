package com.cefriel;

import com.cefriel.template.utils.TemplateFunctions;
import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MaptCustomFunctionsTest extends CamelTestSupport {

    public static class CustomFunctionsBean extends TemplateFunctions {
        public String printMessage() {
            return "test";
        }
    }

    @Produce("direct:start")
    ProducerTemplate start;

    @Produce("direct:start2")
    ProducerTemplate start2;
    private static ChimeraResourceBean template;
    private static ChimeraResourceBean resourceCustomFunctions;
    private static TemplateFunctions customFunctionsBean;

    @BeforeAll
    static void fillBeans(){
        template = new ChimeraResourceBean(
                "file://./src/test/resources/custom-functions/template.vm",
                "vtl");
        resourceCustomFunctions = new ChimeraResourceBean(
                "file://./src/test/resources/custom-functions/CustomFunctions.java",
                "java");

        customFunctionsBean = new CustomFunctionsBean();
    }

    @Test
    public void testCustomFunctionsResource() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:resourceCustomFunctionsTest");
        mock.expectedMessageCount(1);
        start.sendBody(template);
        mock.assertIsSatisfied();
        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        // the custom function only prints "test"
        assert(result.equals("test"));
    }

    @Test
    public void testCustomFunctionsBean() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:customFunctionsTest");
        mock.expectedMessageCount(1);
        start2.sendBody(template);
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
                getCamelContext().getRegistry().bind("resourceCustomFunctions", resourceCustomFunctions);


                from("direct:start")
                        .to("mapt://?template=#bean:template&resourceCustomFunctions=#bean:resourceCustomFunctions")
                        .to("mock:resourceCustomFunctionsTest");

                from("direct:start2")
                        .to("mapt://?template=#bean:template&customFunctions=#class:com.cefriel.MaptCustomFunctionsTest$CustomFunctionsBean")
                        .to("mock:customFunctionsTest");
            }
        };
    }
}
