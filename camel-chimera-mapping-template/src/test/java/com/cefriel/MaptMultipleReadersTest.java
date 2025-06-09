package com.cefriel;

import com.cefriel.aggregationStrategy.ReadersAggregation;
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

import java.io.IOException;
import java.util.Map;

public class MaptMultipleReadersTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;

    @Produce
    ProducerTemplate template;


    private static ChimeraResourceBean templateMultipleReaders;

    @BeforeAll
    static void fillBeans(){
        templateMultipleReaders = new ChimeraResourceBean(
                "file://./src/test/resources/file/multiple-readers/template.vm",
                "");
    }

    @Test
    public void testMultipleReaders() throws InterruptedException, IOException {
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
        String expectedOutput = "1,2,3,4,5,6";
        assert expectedOutput.equals(result);
        mock.assertIsSatisfied();
    }

    @Test
    public void testMultipleReadersAggregation() throws InterruptedException, IOException {
        MockEndpoint mock = getMockEndpoint("mock:multipleReadersAggregation");
        mock.expectedMessageCount(1);

        template.sendBody("direct:reader1", null);
        template.sendBody("direct:reader2", null);
        mock.assertIsSatisfied();

        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        String expectedOutput = "1,2,3,4,5,6";
        assert expectedOutput.equals(result);

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

                from("direct:reader1")
                        .setVariable("readerFormat", constant("csv"))
                        .setVariable("readerName", constant("reader1"))
                        .setBody(constant("""
                        a,b,c
                        1,2,3
                        """))
                        .to("direct:aggregation");

                from("direct:reader2")
                        .setVariable("readerFormat", constant("csv"))
                        .setVariable("readerName", constant("reader2"))
                        .setBody(constant("""
                        d,e,f
                        4,5,6
                        """))
                        .to("direct:aggregation");

                from("direct:aggregation")
                        .aggregate(constant(true), new ReadersAggregation())
                        .completionSize(2)
                        .to("mapt://readers?template=#bean:templateMultipleReaders")
                        .to("mock:multipleReadersAggregation");
            }
        };
    }
}
