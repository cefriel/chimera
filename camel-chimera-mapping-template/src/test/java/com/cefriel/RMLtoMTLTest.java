package com.cefriel;

import com.cefriel.aggregationStrategy.ReadersAggregation;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class RMLtoMTLTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producerTemplate;

    private static ChimeraResourceBean rmlMapping;

    @BeforeAll
    static void fillBean(){
        rmlMapping = new ChimeraResourceBean("file://./src/test/resources/file/rml/mapping.ttl", "rml");
    }

    @Test
    public void testRMLtoMTL() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:testInvalidMapping");
        mock.expectedMessageCount(1);

        ChimeraResourceBean chimeraResourceBean = new ChimeraResourceBean("file://./src/test/resources/file/rml/student.csv", "csv");
        ChimeraResourceBean chimeraResourceBean2 = new ChimeraResourceBean("file://./src/test/resources/file/rml/sport.csv", "csv");

        producerTemplate.sendBody("direct:reader1", ResourceAccessor.open(chimeraResourceBean, null));
        producerTemplate.sendBody("direct:reader2", ResourceAccessor.open(chimeraResourceBean2, null));
        mock.assertIsSatisfied();

        String mappedOutput = mock.getExchanges().get(0).getMessage().getBody(String.class);

        // clean up generated MTL template file
        Path templatePath = Path.of("./src/test/resources/file/rml/template.rml.vm");
        if (Files.exists(templatePath)) {
            Files.delete(templatePath);
        }

        String correctOutput = Files.readString(Path.of("./src/test/resources/file/rml/output.nq"));
        Model result = TestUtils.rdfToModel(mappedOutput, "nquads", null);
        Model expected = TestUtils.rdfToModel(correctOutput, "nquads", null);
        assert (Models.isomorphic(result, expected));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("rmlMapping", rmlMapping);

                from("direct:reader1")
                        .setVariable("readerFormat", constant("csv"))
                        .setVariable("readerName", constant("reader1"))
                        .to("direct:aggregation");

                from("direct:reader2")
                        .setVariable("readerFormat", constant("csv"))
                        .setVariable("readerName", constant("reader2"))
                        .to("direct:aggregation");

                from("direct:aggregation")
                        .aggregate(constant(true), new ReadersAggregation())
                        .completionSize(2)
                        .to("mapt://readers?rml=#bean:rmlMapping&basePath=./src/test/resources/file/rml")
                        .to("mock:testInvalidMapping");
            }
        };
    }
}
