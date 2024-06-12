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
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GraphInferenceTest extends CamelTestSupport {
    private static ChimeraResourceBean ontology;
    private static ChimeraResourceBean triples;

    @Produce("direct:start")
    ProducerTemplate start;

    @BeforeAll
    static void fillBean(){
        ontology = new ChimeraResourceBean(
                "file://./src/test/resources/file/ontologies/ontology.owl",
                "rdfxml");
        triples = new ChimeraResourceBean(
                "file://./src/test/resources/file/template/enrich.ttl",
                "turtle");
    }

    @Test
    public void testNoInference() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:noInference");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:noInference", graph);

        mock.expectedMessageCount(1);

        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        IRI demoInstance = vf.createIRI("http://sprint-transport.eu/data/AMV_STATION_INFERENCE_DEMO");
        IRI classStation = vf.createIRI("http://vocab.gtfs.org/terms#Stop");
        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(demoInstance, RDF.TYPE,
                classStation);
        assert (statements.stream().count() == 0);

        mock.assertIsSatisfied();
    }

    @Test
    public void testInference() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:inference");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        Utils.populateRepository(graph.getRepository(), triples, null);
        start.sendBody("direct:inference", graph);

        mock.expectedMessageCount(1);

        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        IRI demoInstance = vf.createIRI("http://sprint-transport.eu/data/AMV_STATION_INFERENCE_DEMO");
        IRI classStation = vf.createIRI("http://vocab.gtfs.org/terms#Stop");
        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(demoInstance, RDF.TYPE,
                classStation);
        assert (statements.stream().count() == 1);

        mock.assertIsSatisfied();
    }

    @Test
    public void testInferenceWithoutSchema() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:inferenceNoSchema");

        MemoryRDFGraph graph = new MemoryRDFGraph();
        SimpleValueFactory vf = SimpleValueFactory.getInstance();
        Repository repo = graph.getRepository();
        try (var connection = repo.getConnection()) {
            connection.add(vf.createStatement(vf.createIRI("http://example.org/a"),
                    RDFS.SUBPROPERTYOF,
                    vf.createIRI("http://example.org/b")));

            connection.add(vf.createStatement(vf.createIRI("http://example.org/b"),
                    RDFS.SUBPROPERTYOF,
                    vf.createIRI("http://example.org/c")));
        }
        start.sendBody("direct:inferenceNoSchema", graph);

        mock.expectedMessageCount(1);

        RDFGraph result = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        IRI a = vf.createIRI("http://example.org/a");
        IRI c = vf.createIRI("http://example.org/c");
        RepositoryResult<Statement> statements = result.getRepository().getConnection().getStatements(a, RDFS.SUBPROPERTYOF,
                c);
        assert (statements.stream().count() == 1);

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                getCamelContext().getRegistry().bind("ontology", ontology);
                getCamelContext().getRegistry().bind("triples", triples);

                from("direct:noInference")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("mock:noInference");

                from("direct:inference")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("graph://inference?chimeraResource=#bean:ontology")
                        .to("mock:inference");

                from("direct:inferenceNoSchema")
                        .to("graph://add?chimeraResource=#bean:triples")
                        .to("graph://inference")
                        .to("mock:inferenceNoSchema");
            }
        };
    }
}
