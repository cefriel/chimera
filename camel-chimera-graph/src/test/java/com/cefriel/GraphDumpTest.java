package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphDumpTest extends CamelTestSupport {
    static ChimeraResourcesBean triples;

    @BeforeAll
    static void fillBean(){
        triples = new ChimeraResourcesBean(List.of((
                new ChimeraResourceBean(
                        "file://./src/test/resources/file/template/my-source.ttl",
                        "turtle"))));
    }

    @Test
    public void testDump() throws Exception {

        MockEndpoint mock = getMockEndpoint("mock:dump");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("graph://dump?filename=dump.ttl&basePath=src/test/resources/file/result&dumpFormat=turtle")
                        .to("mock:dump");
            }
        };
}

}
