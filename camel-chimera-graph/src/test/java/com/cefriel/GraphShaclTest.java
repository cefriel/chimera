package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphShaclTest extends CamelTestSupport {
    private static ChimeraResourceBean shaclResource;
    private static ChimeraResourceBean triples;

    @BeforeAll
    static void fillBean(){
        shaclResource = new ChimeraResourceBean(
                "file://./src/test/resources/file/shacl/stop_shape.ttl",
                "turtle");

        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/enrich.ttl",
                "turtle");
    }

    @Test
    public void testShacl() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:shacl");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() throws Exception {

                getCamelContext().getRegistry().bind("shaclResource", shaclResource);
                getCamelContext().getRegistry().bind("triples", triples);

                from("graph://get")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("graph://shacl?chimeraResource=#bean:shaclResource")
                        .to("mock:shacl");
            }
        };
    }

}
