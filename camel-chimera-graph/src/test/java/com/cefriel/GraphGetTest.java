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

import com.cefriel.graph.HTTPRDFGraph;
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.SPARQLEndpointGraph;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.contextaware.ContextAwareRepository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphGetTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(GraphGetTest.class);
    @Produce("direct:start")
    ProducerTemplate start;

    @Produce("direct:start2")
    ProducerTemplate start2;

    @Test
    public void testInMemoryGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:memory");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(MemoryRDFGraph.class));
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        // assert(graph.getRepository().getClass().equals(SailRepository.class));
    }
    @Test
    public void testEmptyInput() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:emptyInput");
        mock.expectedMessageCount(1);
        start2.sendBodyAndHeader(null, ChimeraConstants.RDF_FORMAT, "turtle");
        mock.assertIsSatisfied();

        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getRepository().getConnection().size() == 0);
    }

    /*
    @Test
    public void testNamedGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:namedGraph");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getNamedGraph().toString().equals("http://example.org/testName"));
    }

     */
    /*
    @Test
    public void testBaseIRI() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:baseIRI");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getBaseIRI().toString().equals("http://example.org/"));
        assert(graph.getNamedGraph().toString().equals("http://example.org/" + mock.getExchanges().get(0).getExchangeId()));
    }

     */

    @Test
    public void testDefaultGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:defaultGraph");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        // test that a graph is returned, that it is a context aware repo, that the context is RDF4J.NIL and that the graph query returns nothing
        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(MemoryRDFGraph.class));
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert (graph.getNamedGraph() == null);
        var repo = graph.getRepository();
        try (var conn = repo.getConnection()) {
            SimpleValueFactory vf = SimpleValueFactory.getInstance();
            IRI alice = vf.createIRI("http://example.org/alice");
            IRI knows = vf.createIRI("http://xmlns.com/foaf/0.1/knows");
            IRI bob = vf.createIRI("http://example.org/bob");
            // Add triple to the default graph
            conn.add(alice, knows, bob);
            // Add triples to non default graph
            conn.add(alice, knows, alice, vf.createIRI("http://example.org/someOtherGraph"));
           }

        try (var conn = repo.getConnection()) {
            String queryTriples = "SELECT * WHERE { ?s ?p ?o }";
            TupleQuery tupleQuery = conn.prepareTupleQuery(queryTriples);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                assert (result.stream().count() == 2);
            }

            String queryGraphs = "SELECT DISTINCT ?graph WHERE { GRAPH ?graph {?s ?p ?o}}";
            TupleQuery tupleQueryGraphs = conn.prepareTupleQuery(queryGraphs);
            try (TupleQueryResult result = tupleQueryGraphs.evaluate()) {
                assert (result.stream().count() == 1);
            }
        }
    }

    @Test
    public void testMultipleContexts() {
        var memoryRDFGraph = new MemoryRDFGraph();
        var repo = memoryRDFGraph.getRepository();

        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        IRI alice = vf.createIRI("http://example.org/alice");
        IRI knows = vf.createIRI("http://xmlns.com/foaf/0.1/knows");
        IRI bob = vf.createIRI("http://example.org/bob");

        try (var connection = repo.getConnection()) {
            connection.add(vf.createStatement(alice, knows, bob, vf.createIRI("http://example.org/graph1")));

            connection.add(vf.createStatement(vf.createIRI("http://example.org/a"),
                    vf.createIRI("http://example.org/b"),
                    vf.createIRI("http://example.org/c"),
                    vf.createIRI("http://example.org/graph2")));

            connection.add(vf.createStatement(vf.createIRI("http://example.org/1"),
                    vf.createIRI("http://example.org/2"),
                    vf.createIRI("http://example.org/3"),
                    vf.createIRI("http://example.org/graph3")));

            connection.add(vf.createStatement(
                    vf.createIRI("http://example.org/I"),
                    vf.createIRI("http://example.org/II"),
                    vf.createIRI("http://example.org/III")));


            connection.add(vf.createStatement(
                    vf.createIRI("http://example.org/einz"),
                    vf.createIRI("http://example.org/zwei"),
                    vf.createIRI("http://example.org/trei"),
                    RDF4J.NIL));
        }

        String queryTriples = "SELECT * WHERE { ?s ?p ?o }";

        try (var connection = repo.getConnection()) {
            var query = connection.prepareTupleQuery(queryTriples);
            var result = query.evaluate();
            // return all three triples
            assert (result.stream().toList().size() == 5);

            String queryGraphs = "SELECT DISTINCT ?graph WHERE { GRAPH ?graph {?s ?p ?o}}";
            TupleQuery tupleQueryGraphs = connection.prepareTupleQuery(queryGraphs);
            var graphResult = tupleQueryGraphs.evaluate();
            // all three graphs are detected
            assert (graphResult.stream().toList().size() == 4);
        }


        var cRepo = new ContextAwareRepository(repo);
        // add graph1 as read context
        cRepo.setReadContexts(vf.createIRI("http://example.org/graph1"));

        try (var connection = cRepo.getConnection()) {
            var query = connection.prepareTupleQuery(queryTriples);
            var result = query.evaluate();
            assert (result.stream().toList().size() == 1);

            String queryGraphs = "SELECT DISTINCT ?graph WHERE { GRAPH ?graph {?s ?p ?o}}";
            TupleQuery tupleQueryGraphs = connection.prepareTupleQuery(queryGraphs);
            var graphResult = tupleQueryGraphs.evaluate();
            assert (graphResult.stream().findAny().isEmpty());
        }

        cRepo.setReadContexts(vf.createIRI("http://example.org/graph1"),
                vf.createIRI("http://example.org/graph2"));

        try (var connection = cRepo.getConnection()) {
            var query = connection.prepareTupleQuery(queryTriples);
            var result = query.evaluate();
            assert (result.stream().toList().size() == 2);

            String queryGraphs = "SELECT DISTINCT ?graph WHERE { GRAPH ?graph {?s ?p ?o}}";
            TupleQuery tupleQueryGraphs = connection.prepareTupleQuery(queryGraphs);
            var graphResult = tupleQueryGraphs.evaluate();
            assert (graphResult.stream().findAny().isEmpty());
        }
    }

    @Test public void testNonDefaultGraph() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:nonDefaultGraph");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getNamedGraph().toString().equals("http://example.org/testName"));
        assert(graph.getBaseIRI().toString().equals("http://example.org/"));
    }

    @Test
    public void testHTTPRDFGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:http");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(HTTPRDFGraph.class));
        HTTPRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(HTTPRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        assert(graph.getRepository().getClass().equals(HTTPRepository.class));

        String baseIri = ChimeraConstants.DEFAULT_BASE_IRI;
        assert(graph.getBaseIRI().toString().equals(baseIri));
        assert(graph.getNamedGraph() == null);
    }

    @Test
    public void testSPARQLEndpointGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:sparql");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(SPARQLEndpointGraph.class));
        SPARQLEndpointGraph graph = mock.getExchanges().get(0).getMessage().getBody(SPARQLEndpointGraph.class);
        assert(graph.getRepository().isInitialized());
        // assert(graph.getRepository().getClass().equals(SPARQLRepository.class));
        // assert(graph.getNamedGraph() == null);
    }

    @Test
    public void testToGetInMemoryTest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:toMemory");
        mock.expectedMessageCount(1);

        ChimeraResourceBean r = new ChimeraResourceBean("file://./src/test/resources/file/base/test.ttl", "turtle");
        start.sendBodyAndHeader(ResourceAccessor.open(r, null),
                ChimeraConstants.RDF_FORMAT, r.getSerializationFormat());

        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(MemoryRDFGraph.class));
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        // assert(graph.getRepository().getClass().equals(SailRepository.class));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("graph://get")
                        .to("mock:memory");

                from("graph://get?defaultGraph=true&serverUrl=MY_SERVER_URL&repositoryId=MY_REPOSITORY_ID")
                        .to("mock:http");

                from("graph://get?sparqlEndpoint=MY_SPARQL_ENDPOINT")
                        .to("mock:sparql");

                from("direct:start")
                        .to("graph://get")
                        .to("mock:toMemory");

                from("direct:start2").
                        to("graph://get").
                        to("mock:emptyInput");


                // from("graph://get?defaultGraph=false&namedGraph=http://example.org/testName")
                   //     .to("mock:namedGraph");

                //from("graph://get?defaultGraph=false&baseIRI=http://example.org/")
                //        .to("mock:baseIRI");

                from("graph://get?defaultGraph=false&baseIRI=http://example.org/&namedGraph=http://example.org/testName")
                        .to("mock:nonDefaultGraph");

                from("graph://get?defaultGraph=true")
                        .to("mock:defaultGraph");
            }
        };
    }
}
