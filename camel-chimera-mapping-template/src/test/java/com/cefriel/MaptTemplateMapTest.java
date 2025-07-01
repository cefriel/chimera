package com.cefriel;

import com.cefriel.template.TemplateMap;
import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MaptTemplateMapTest extends CamelTestSupport {

    private final TemplateMap templateMap = new TemplateMap(Map.of("key1", "1", "key2","2"));

    @Test
    public void testTemplateMap() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.send("direct:start", ExchangeBuilder.anExchange(context()).build());
        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        Assertions.assertEquals(result, "1,2");
        mock.assertIsSatisfied();
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                getCamelContext().getRegistry().bind("templateMap", templateMap);
                getCamelContext().getRegistry().bind("mtlTemplate", new ChimeraResourceBean("file://./src/test/resources/file/templateMap/template.vm", ""));

                from("direct:start")
                        .to("mapt://?template=#bean:mtlTemplate&templateMap=#bean:templateMap")
                        .to("mock:result");
            }
        };
    }


}
