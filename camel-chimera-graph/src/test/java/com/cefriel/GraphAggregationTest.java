package com.cefriel;

import com.cefriel.component.GraphAggregation;
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import com.cefriel.util.Utils;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphAggregationTest extends CamelTestSupport {

    // Start two different routes, each one with an rdf graph with some content
    // aggregate and count the statements

    @Produce("direct:start1")
    ProducerTemplate start1;

    @Produce("direct:start2")
    ProducerTemplate start2;


    @Test
    public void testGraphAggregation() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:end-graph-aggregate");

        ChimeraResourceBean r1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/my-source.ttl",
                "turtle");
        var triples1 = new ChimeraResourcesBean(List.of(r1));
        ChimeraResourceBean r2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/random.ttl",
                "turtle");
        var triples2 = new ChimeraResourcesBean(List.of(r2));

        // random.ttl and my-source.ttl different data
        var graph1 = new MemoryRDFGraph();
        Utils.populateRepository(graph1.getRepository(), triples1, camelTestSupportExtension.context());
        var graph2 = new MemoryRDFGraph();
        Utils.populateRepository(graph2.getRepository(), triples2, camelTestSupportExtension.context());

        var expectedNumberOfStatements = graph1.getRepository().getConnection().size() +
                graph2.getRepository().getConnection().size();

        start1.sendBody("direct:aggregate", graph1);
        start1.sendBody("direct:aggregate", graph2);

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph resultGraph = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        var statements = resultGraph.getRepository().getConnection().getStatements(null,null,null);

        assert (statements.stream().count() == expectedNumberOfStatements);
    }

    @Test
    public void testGraphAggregationToFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:end-graph-aggregate-file");

        ChimeraResourceBean r1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/my-source.ttl",
                "turtle");
        var triples1 = new ChimeraResourcesBean(List.of(r1));
        ChimeraResourceBean r2 = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/random.ttl",
                "turtle");
        var triples2 = new ChimeraResourcesBean(List.of(r2));

        // random.ttl and my-source.ttl different data
        var graph1 = new MemoryRDFGraph();
        Utils.populateRepository(graph1.getRepository(), triples1, camelTestSupportExtension.context());
        var graph2 = new MemoryRDFGraph();
        Utils.populateRepository(graph2.getRepository(), triples2, camelTestSupportExtension.context());

        var expectedNumberOfStatements = graph1.getRepository().getConnection().size() +
                graph2.getRepository().getConnection().size();

        start2.sendBody("direct:aggregate-to-file", graph1);
        start2.sendBody("direct:aggregate-to-file", graph2);

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph resultGraph = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        var statements = resultGraph.getRepository().getConnection().getStatements(null,null,null);

        assert (statements.stream().count() == expectedNumberOfStatements);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                from("direct:aggregate")
                        .aggregate(constant(true), new GraphAggregation())
                        .completionSize(2)
                        .to("mock:end-graph-aggregate");

                from("direct:aggregate-to-file")
                        .aggregate(constant(true), new GraphAggregation())
                        .completionSize(2)
                        .to("graph://dump?filename=dumpGraphAggregation&basePath=src/test/resources/file/result&dumpFormat=turtle")
                        .to("mock:end-graph-aggregate-file");
            }
        };
    }
}
