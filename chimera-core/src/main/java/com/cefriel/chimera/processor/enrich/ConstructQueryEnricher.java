/*
 * Copyright (c) 2019-2021 Cefriel.
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

import java.util.*;

public class ConstructQueryEnricher implements Processor {

    private Logger logger = LoggerFactory.getLogger(ConstructQueryEnricher.class);

    private List<String> sparqlQueries;

    private static Map<String, List<String>> cache = new HashMap<>();
    private String queriesId;
    private String baseUrl;

    @Override
    public void process(Exchange exchange) throws Exception {
        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = Utils.getContextAwareRepository(graph.getRepository(), graph.getContext());

        if (sparqlQueries == null)
            sparqlQueries = new ArrayList<>();

        String msgQueriesId = exchange.getMessage().getHeader(ProcessorConstants.QUERIES_ID, String.class);
        if (msgQueriesId != null)
            queriesId = msgQueriesId;
        if (queriesId != null) {
            synchronized (cache) {
                List<String> cachedQueries = cache.get(queriesId);
                if (cachedQueries != null) {
                    logger.info("Cached queries used for: " + queriesId);
                    sparqlQueries.addAll(cachedQueries);
                }
                else {
                    if (baseUrl == null)
                        baseUrl = "";
                    baseUrl = Utils.trailingSlash(baseUrl);
                    String queriesUrl = baseUrl + queriesId;
                    String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

                    Model model = SemanticLoader.secure_load_data(queriesUrl, ProcessorConstants.RDF_FORMAT_TURTLE, token);
                    logger.info("Triples loaded from source " + queriesUrl + ": " + model.size());

                    ValueFactory factory = SimpleValueFactory.getInstance();
                    IRI queryClass = factory.createIRI(ProcessorConstants.QUERY_CLASS);
                    IRI constructProperty = factory.createIRI(ProcessorConstants.CONSTRUCT_PROPERTY);
                    List<String> extractedQueries = new ArrayList<>();
                    for (Resource query : model.filter(null, RDF.TYPE, queryClass).subjects()) {
                        logger.info("Query " + query.stringValue());
                        Optional<Literal> queries = Models.objectLiteral(model.filter(query, constructProperty, null));
                        queries.ifPresent(q -> extractedQueries.add(q.stringValue()));
                    }
                    sparqlQueries.addAll(extractedQueries);
                    cache.put(queriesId, new ArrayList<>(extractedQueries));
                }
            }
        }

        for(String query : sparqlQueries) {
            logger.info("Executing query on the graph: " + query);
            Model m = Repositories.graphQuery(repo, query, r -> QueryResults.asModel(r));
            try (RepositoryConnection con = repo.getConnection()) {
                logger.info("Added " + m.size() + " triples");
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

    public String getQueriesId() {
        return queriesId;
    }

    public void setQueriesId(String queriesId) {
        this.queriesId = queriesId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
