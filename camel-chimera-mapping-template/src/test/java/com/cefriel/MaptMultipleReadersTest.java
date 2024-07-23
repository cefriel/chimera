package com.cefriel;

import com.cefriel.template.io.Reader;
import com.cefriel.template.io.csv.CSVReader;
import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class MaptMultipleReadersTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourceBean templateMultipleReaders;

    @BeforeAll
    static void fillBeans(){
        templateMultipleReaders = new ChimeraResourceBean(
                "file://./src/test/resources/file/multiple-readers/template.vm",
                "");
    }

    @Test
    public void testMultipleReaders() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:multipleReaders");
        mock.expectedMessageCount(1);

        Map<String, Reader> readers = Map.of("reader1",
                new CSVReader("""
                        a,b,c
                        1,2,3
                        """),
                "reader2",
                new CSVReader("""
                        d,e,f
                        4,5,6
                        """));
        start.sendBody(readers);
        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        String expectedOutput = "1,2,3,4,5,6".replaceAll("\\r\\n", "\n");
        assert expectedOutput.equals(result);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("templateMultipleReaders", templateMultipleReaders);
                from("direct:start")
                        .to("mapt://readers?template=#bean:templateMultipleReaders")
                        .to("mock:multipleReaders");
            }
        };
    }
}
