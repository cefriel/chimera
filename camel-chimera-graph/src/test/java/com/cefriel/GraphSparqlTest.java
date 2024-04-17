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
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GraphSparqlTest extends CamelTestSupport {
    @Produce("direct:start")
    ProducerTemplate start;
    private static ChimeraResourceBean triples;
    private static ChimeraResourceBean resourceQuery;
    private static final String sparqlQuery = "SELECT ?s ?p ?o WHERE { ?s ?p ?o}";

    @BeforeAll
    static void fillBean(){
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/base/test.ttl",
                "turtle");

        resourceQuery = new ChimeraResourceBean(
                "file://./src/test/resources/file/sparql/sparql.txt",
                "txt");
    }

    @Test
    public void testSparqlQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:sparqlQuery", graph);

        MockEndpoint mock = getMockEndpoint("mock:sparqlQuery");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        TupleQueryResult result = mock.getExchanges().get(0).getMessage().getBody(TupleQueryResult.class);
        List<String> queryBindings = result.getBindingNames();
        assert (queryBindings.size() == 3);
        assert (queryBindings.get(0).equals("s"));
        assert (queryBindings.get(1).equals("p"));
        assert (queryBindings.get(2).equals("o"));

    }

    @Test
    public void testSparqlResourceQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:sparqlResourceQuery", graph);

        MockEndpoint mock = getMockEndpoint("mock:sparqlResourceQuery");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        TupleQueryResult result = mock.getExchanges().get(0).getMessage().getBody(TupleQueryResult.class);
        List<String> queryBindings = result.getBindingNames();
        assert (queryBindings.size() == 3);
        assert (queryBindings.get(0).equals("s"));
        assert (queryBindings.get(1).equals("p"));
        assert (queryBindings.get(2).equals("o"));
    }

    @Test
    public void testSparqlQueryUnsupportedFormat() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);

        Assertions.assertThrows(CamelExecutionException.class,
                () -> {
                    try {
                        start.sendBody("direct:sparqlQueryUnsupportedFormat", graph);
                    }
                    catch (CamelExecutionException e) {
                        Throwable cause = e.getCause();
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {throw cause;});
                        throw e;
                    }
                });

        MockEndpoint mock = getMockEndpoint("mock:sparqlQueryUnsupportedFormat");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();
    }

    @Test
    public void testSparqlQueryNoQueries() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);

        Assertions.assertThrows(CamelExecutionException.class,
                () -> {
                    try {
                        start.sendBody("direct:sparqlQueryNoQueries", graph);
                    }
                    catch (CamelExecutionException e) {
                        Throwable cause = e.getCause();
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {throw cause;});
                        throw e;
                    }
                });

        MockEndpoint mock = getMockEndpoint("mock:sparqlQueryNoQueries");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("resourceQuery", resourceQuery);

                from("direct:sparqlQuery")
                        .setVariable("sparqlQuery", constant(sparqlQuery))
                        .toD("graph://sparql?dumpFormat=memory&query=${variable.sparqlQuery}")
                        .to("mock:sparqlQuery");

                from("direct:sparqlResourceQuery")
                        .toD("graph://sparql?dumpFormat=memory&chimeraResource=#bean:resourceQuery")
                        .to("mock:sparqlResourceQuery");

                from("direct:sparqlQueryUnsupportedFormat")
                        .setVariable("sparqlQuery", constant(sparqlQuery))
                        .toD("graph://sparql?dumpFormat=parquet&query=${variable.sparqlQuery}")
                        .to("mock:sparqlQueryUnsupportedFormat");

                from("direct:sparqlQueryNoQueries")
                        .setVariable("sparqlQuery", constant(""))
                        .toD("graph://sparql?dumpFormat=parquet&query=${variable.sparqlQuery}")
                        .to("mock:sparqlQueryUnsupportedFormat");
            }
        };
    }
}
