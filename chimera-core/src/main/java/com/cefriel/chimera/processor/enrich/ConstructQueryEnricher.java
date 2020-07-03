/*
 * Copyright 2020 Cefriel.
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

package com.cefriel.chimera.processor.enrich;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConstructQueryEnricher implements Processor {

    private Logger logger = LoggerFactory.getLogger(ConstructQueryEnricher.class);

    private List<String> sparqlQueries;
    private String queriesUrl;

    @Override
    public void process(Exchange exchange) throws Exception {
        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = Utils.getContextAwareRepository(graph.getRepository(), graph.getContext());

        if (sparqlQueries == null)
            sparqlQueries = new ArrayList<>();

        if (queriesUrl != null) {
            String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);
            Model model = SemanticLoader.secure_load_data(queriesUrl, "turtle", token);
            logger.info("Triples loaded from source " + queriesUrl + ": " + model.size());

            ValueFactory factory = SimpleValueFactory.getInstance();
            IRI queryClass = factory.createIRI(ProcessorConstants.QUERY_CLASS);
            IRI constructProperty = factory.createIRI(ProcessorConstants.CONSTRUCT_PROPERTY);
            for (Resource query : model.filter(null, RDF.TYPE, queryClass).subjects()) {
                logger.info("Query " + query.stringValue());
                Optional<Literal> queries = Models.objectLiteral(model.filter(query, constructProperty, null));
                queries.ifPresent(q -> sparqlQueries.add(q.stringValue()));
            }
        }

        for(String query : sparqlQueries) {
            logger.info("Executing query on the graph: " + query);
            Model m = Repositories.graphQuery(repo, query, r -> QueryResults.asModel(r));
            try (RepositoryConnection con = repo.getConnection()) {
                con.add(m);
            }
        }
    }

    public List<String> getSparqlQueries() {
        return sparqlQueries;
    }

    public void setSparqlQueries(List<String> sparqlQueries) {
        this.sparqlQueries = sparqlQueries;
    }

    public String getQueriesUrl() {
        return queriesUrl;
    }

    public void setQueriesUrl(String queriesUrl) {
        this.queriesUrl = queriesUrl;
    }
}
