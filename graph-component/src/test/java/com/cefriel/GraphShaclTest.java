package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphShaclTest extends CamelTestSupport {
    private static ChimeraResourcesBean shaclUrls;
    private static ChimeraResourcesBean triples;

    @BeforeAll
    static void fillBean(){
        ChimeraResourceBean r1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/shacl/stop_shape.ttl",
                "turtle");
        shaclUrls = new ChimeraResourcesBean(List.of(r1));

        ChimeraResourceBean r3 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/enrich.ttl",
                "turtle");
        triples = new ChimeraResourcesBean(List.of(r3));
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

                getCamelContext().getRegistry().bind("shaclUrls", shaclUrls);
                getCamelContext().getRegistry().bind("triples", triples);

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("graph://shacl?chimeraResources=#bean:shaclUrls")
                        .to("mock:shacl");
            }
        };
    }

}
