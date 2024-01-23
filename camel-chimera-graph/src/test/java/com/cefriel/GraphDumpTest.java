package com.cefriel;

import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.Utils;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GraphDumpTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;
    static ChimeraResourceBean triples = new ChimeraResourceBean(
            "file://./src/test/resources/file/template/my-source.ttl",
            "turtle");
    @Test
    public void testDumpToFile() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:dumpToFile", graph);

        MockEndpoint mock = getMockEndpoint("mock:dumpToFile");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        File f = new File("./src/test/resources/file/result/dump.ttl");

        assert (f.exists());
    }

    @Test
    public void testDump() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:dump", graph);

        MockEndpoint mock = getMockEndpoint("mock:dump");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert (mock.getExchanges().get(0).getMessage().getBody() != null);

    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);

                from("direct:dumpToFile")
                        .to("graph://dump?filename=dump&basePath=src/test/resources/file/result&dumpFormat=turtle")
                        .to("mock:dumpToFile");

                from("direct:dump")
                        .to("graph://dump?dumpFormat=turtle")
                        .to("mock:dump");
            }
        };
}

}
