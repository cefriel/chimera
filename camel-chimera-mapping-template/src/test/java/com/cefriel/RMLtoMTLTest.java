package com.cefriel;

import com.cefriel.template.io.Reader;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class RMLtoMTLTest extends CamelTestSupport {

    @Produce
    ProducerTemplate producerTemplate;

    @Produce("direct:start")
    ProducerTemplate start;

    private static ChimeraResourceBean rmlMultipleReaderMapping;

    @BeforeAll
    static void fillBean(){
        rmlMultipleReaderMapping = new ChimeraResourceBean("file://./src/test/resources/file/rml/mapping.ttl", "rml");
    }

    @Test
    public void testRMLtoMTLMultipleReaders() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:testRMLtoMTLMultipleReaders");
        mock.expectedMessageCount(1);

        // sources are read directly by the compiled mapping, so no readers are provided
        producerTemplate.sendBody("direct:testRMLtoMTLMultipleReaders", new HashMap<String, Reader>());
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
        assertTrue(Models.isomorphic(result, expected));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("rmlMultipleReaderMapping", rmlMultipleReaderMapping);

                from("direct:testRMLtoMTLMultipleReaders")
                        .to("mapt://readers?rml=#bean:rmlMultipleReaderMapping&basePath=./src/test/resources/file/rml")
                        .to("mock:testRMLtoMTLMultipleReaders");
            }
        };
    }
}
