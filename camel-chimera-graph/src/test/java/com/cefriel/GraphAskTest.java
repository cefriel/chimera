package com.cefriel;

import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.Utils;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphAskTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;
    private static ChimeraResourceBean triples;
    private static ChimeraResourceBean resourceQuery;
    private static final String sparqlSelectQuery = "SELECT ?s ?p ?o WHERE { ?s ?p ?o}";
    private static final String sparqlAskQuery = "ASK WHERE { ?s ?p ?o}";

    @BeforeAll
    static void fillBean(){
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/base/test.ttl",
                "turtle");

        resourceQuery = new ChimeraResourceBean(
                "file://./src/test/resources/file/ask/ask.txt",
                "txt");
    }

    @Test
    public void testSparqlQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:sparqlAskQuery", graph);

        MockEndpoint mock = getMockEndpoint("mock:sparqlAskQuery");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        Boolean result = mock.getExchanges().get(0).getMessage().getBody(Boolean.class);
        assert (result);
    }

    @Test
    public void testSparqlSelectQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);

        MockEndpoint mock = getMockEndpoint("mock:sparqlAskSelectQuery");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        Assertions.assertThrows(CamelExecutionException.class,
                () -> {
                    try {
                        start.sendBody("direct:sparqlAskSelectQuery", graph);
                    }
                    catch (CamelExecutionException e) {
                        Throwable cause = e.getCause();
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {throw cause;});
                        throw e;
                    }
                });
    }

    @Test
    public void testSparqlResourceQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:sparqlAskResourceQuery", graph);

        MockEndpoint mock = getMockEndpoint("mock:sparqlAskResourceQuery");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        Boolean result = mock.getExchanges().get(0).getMessage().getBody(Boolean.class);
        assert(result);
    }
    @Test
    public void testSparqlQueryNoQueries() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);

        Assertions.assertThrows(CamelExecutionException.class,
                () -> {
                    try {
                        start.sendBody("direct:sparqlAskQueryNoQueries", graph);
                    }
                    catch (CamelExecutionException e) {
                        Throwable cause = e.getCause();
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {throw cause;});
                        throw e;
                    }
                });

        MockEndpoint mock = getMockEndpoint("mock:sparqlAskQueryNoQueries");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("resourceQuery", resourceQuery);

                from("direct:sparqlAskQuery")
                        .setVariable("sparqlAskQuery", constant(sparqlAskQuery))
                        .toD("graph://ask?query=${variable.sparqlAskQuery}")
                        .to("mock:sparqlAskQuery");

                from("direct:sparqlAskSelectQuery")
                        .setVariable("sparqlSelectQuery", constant(sparqlSelectQuery))
                        .toD("graph://ask?query=${variable.sparqlSelectQuery}")
                        .to("mock:sparqlAskQuery");

                from("direct:sparqlAskResourceQuery")
                        .toD("graph://ask?chimeraResource=#bean:resourceQuery")
                        .to("mock:sparqlAskResourceQuery");

                from("direct:sparqlAskQueryNoQueries")
                        .setVariable("sparqlQuery", constant(""))
                        .toD("graph://ask?query=${variable.sparqlQuery}")
                        .to("mock:sparqlAskQueryUnsupportedFormat");
            }
        };
    }
}
