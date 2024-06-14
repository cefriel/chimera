/*
 * Copyright (c) 2019-2022 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cefriel;

import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.Utils;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphConstructTest extends CamelTestSupport {
    private static ChimeraResourceBean triples;
    private static ChimeraResourceBean constructQuery;
    private static final String baseIri = "http://example.org/";
    private static final String namedGraph = baseIri + "newContext";

    private static final String multipleNamedGraphs = baseIri + "newContext1" + ";" + baseIri + "newContext2";
    private static final String sparqlSelectQuery = "SELECT ?s ?p ?o WHERE { ?s ?p ?o}";
    @Produce("direct:start")
    ProducerTemplate start;
    @BeforeAll
    static void fillBean(){
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/base/test.ttl",
                "turtle");

        constructQuery = new ChimeraResourceBean(
                "file://./src/test/resources/file/construct/construct.txt",
                "txt");
    }
    @Test
    public void testConstructNew() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructNew");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:constructNew", graph);

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(null,null,
                null, Values.iri(namedGraph));
        assert (statements.stream().count() == 9);
    }

    @Test
    public void testConstructNewMultiple() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructNewMultiple");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:constructNewMultiple", graph);

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(null,
                null,
                null,
                Values.iri(namedGraph + "1"));
        assert (statements.stream().count() == 9);
        statements = result.getRepository().getConnection().getStatements(null,
                null,
                null,
                Values.iri(namedGraph + "2"));
        assert (statements.stream().count() == 9);
    }

    @Test
    public void testConstructOld() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructOld");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:constructOld", graph);

        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);

        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(
                null,
                Values.iri("https://schema.org/name"),
                null);


        assert (statements.stream().count() == 9);
    }
    @Test
    public void testConstructSparqlSelectQuery() throws Exception {
        var graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);

        MockEndpoint mock = getMockEndpoint("mock:sparqlSelectQuery");
        mock.expectedMessageCount(0);
        mock.assertIsSatisfied();

        Assertions.assertThrows(CamelExecutionException.class,
                () -> {
                    try {
                        start.sendBody("direct:sparqlSelectQuery", graph);
                    }
                    catch (CamelExecutionException e) {
                        Throwable cause = e.getCause();
                        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {throw cause;});
                        throw e;
                    }
                });
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("constructQuery", constructQuery);

                from("direct:constructNew")
                        .to("graph://construct?namedGraph=" + namedGraph + "&chimeraResource=#bean:constructQuery")
                        .to("mock:constructNew");

                from("direct:constructNewMultiple")
                        .to("graph://construct?namedGraph=" + multipleNamedGraphs + "&chimeraResource=#bean:constructQuery")
                        .to("mock:constructNewMultiple");

                from("direct:constructOld")
                        .to("graph://construct?chimeraResource=#bean:constructQuery")
                        .to("mock:constructOld");

                from("direct:sparqlSelectQuery")
                        .setVariable("sparqlSelectQuery", constant(sparqlSelectQuery))
                        .toD("graph://construct?query=${variable.sparqlSelectQuery}")
                        .to("mock:sparqlSelectQuery");
            }
        };
    }
}
