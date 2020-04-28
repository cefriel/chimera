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
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConstructQueryEnricher implements Processor {

    private Logger logger = LoggerFactory.getLogger(ConstructQueryEnricher.class);

    private List<String> sparqlQueries;

    @Override
    public void process(Exchange exchange) throws Exception {
        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = Utils.getContextAwareRepository(graph.getRepository(), graph.getContext());

        for(String query : sparqlQueries) {
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
}
