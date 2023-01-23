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

import com.cefriel.component.GraphBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ChimeraResourcesBean;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GraphConstructTest extends CamelTestSupport {
    private static ChimeraResourcesBean triples;
    private static ChimeraResourcesBean constructQueries;
    private static final String baseIri = "http://example.org/";
    private static final String namedGraph = baseIri + "newContext";
    @BeforeAll
    static void fillBean(){
        ChimeraResourceBean r1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/base/test.ttl",
                "turtle");
        triples = new ChimeraResourcesBean(List.of(r1));

        ChimeraResourceBean q1 = new ChimeraResourceBean(
                "file://./src/test/resources/file/construct/construct.txt",
                "txt"
        );
        constructQueries = new ChimeraResourcesBean(List.of(q1));
    }
    @Test
    public void testConstructNew() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructNew");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);
        RepositoryResult<Statement> statements = graph.getRepository().getConnection().getStatements(null,null,
                null, Values.iri(namedGraph));
        assert (statements.stream().count() == 9);
    }
    @Test
    public void testConstructOld() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:constructOld");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
        RDFGraph graph = mock.getExchanges().get(0).getMessage().getBody(RDFGraph.class);

        RepositoryResult<Statement> statements = graph.getRepository().getConnection().getStatements(
                null,
                Values.iri("https://schema.org/name"),
                null);

        assert (statements.stream().count() == 9);
    }
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            public void configure() {

                getCamelContext().getRegistry().bind("triples", triples);
                getCamelContext().getRegistry().bind("constructQueries", constructQueries);

                from("graph://get?defaultGraph=false&baseIri=" + baseIri + "&namedGraph=" + baseIri + "Pre")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("graph://construct?namedGraph=" + namedGraph + "&chimeraResources=#bean:constructQueries")
                        .to("mock:constructNew");

                from("graph://get")
                        .to("graph://add?chimeraResources=#bean:triples")
                        .to("graph://construct?chimeraResources=#bean:constructQueries")
                        .to("mock:constructOld");
            }
        };
    }
}
