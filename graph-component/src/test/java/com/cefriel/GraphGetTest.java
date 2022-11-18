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
import com.cefriel.util.UniLoader;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphGetTest extends CamelTestSupport {

    private static final Logger log = LoggerFactory.getLogger(GraphGetTest.class);

    @Produce("direct:start")
    ProducerTemplate start;

    @Test
    public void testInMemoryGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:memory");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(MemoryRDFGraph.class));
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        assert(graph.getRepository().getClass().equals(SailRepository.class));
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
        assert(graph.getNamedGraph() == null);
        assert(graph.getBaseIRI() == null);
    }

    @Test
    public void testSPARQLEndpointGraph() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:sparql");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(SPARQLEndpointGraph.class));
        SPARQLEndpointGraph graph = mock.getExchanges().get(0).getMessage().getBody(SPARQLEndpointGraph.class);
        assert(graph.getRepository().isInitialized());
        assert(graph.getRepository().getClass().equals(SPARQLRepository.class));
        assert(graph.getNamedGraph() == null);
        assert(graph.getBaseIRI() == null);
    }

    @Test
    public void testToGetInMemoryTest() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:toMemory");
        mock.expectedMessageCount(1);

        start.sendBody(UniLoader.open("file://./src/test/resources/file/base/test.ttl"));

        mock.assertIsSatisfied();

        assert(mock.getExchanges().get(0).getMessage().getBody().getClass().equals(MemoryRDFGraph.class));
        MemoryRDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(MemoryRDFGraph.class);
        assert(graph.getRepository().isInitialized());
        assert(graph.getRepository().getClass().equals(SailRepository.class));
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("graph://get")
                        .to("mock:memory");
                // TODO Add tests for other parameters (namedGraph, baseIRI, etc.)
                from("graph://get?serverUrl=MY_SERVER_URL&repositoryId=MY_REPOSITORY_ID")
                        .to("mock:http");
                from("graph://get?sparqlEndpoint=MY_SPARQL_ENDPOINT")
                        .to("mock:sparql");
                from("direct:start")
                        .to("graph://get?rdfFormat=turtle")
                        .to("mock:toMemory");



            }
        };
    }
}
