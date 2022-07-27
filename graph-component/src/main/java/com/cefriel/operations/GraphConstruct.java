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

package com.cefriel.operations;

import com.cefriel.component.GraphBean;
import com.cefriel.graph.MemoryRDFGraph;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.UniLoader;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GraphConstruct {

    private static final Logger LOG = LoggerFactory.getLogger(GraphConstruct.class);
    private static final Map<String, List<String>> cache = new HashMap<>();

    public static void graphConstruct(Exchange exchange) throws IOException {

        List<String> sparqlQueries = new ArrayList<>();
        /*String baseUrl = "";
        String queriesUrl;*/
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);

        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = graph.getRepository();
        RDFGraph newGraph;

        String token = exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class);
        Utils.setConfigurationRDFHeader(exchange, configuration.getRdfFormat());

        //queriesId is in endpoint & removed msgQueriesId
        //TODO: add constructquery in converterConfiguration
        /*if (configuration.getQueriesId()!= null) {
            synchronized (cache) {
                List<String> cachedQueries = cache.get(configuration.getQueriesId());
                if (cachedQueries != null) {
                    LOG.info("Cached queries used for: " + configuration.getQueriesId());
                    sparqlQueries.addAll(cachedQueries);
                }

                else {
                    if(configuration.getSparqlFile()!=null){
                        queriesUrl = configuration.getSparqlFile();
                    }

                    else {
                        baseUrl = Utils.trailingSlash(baseUrl);
                        queriesUrl = baseUrl + configuration.getQueriesId();
                    }
                    //Model model = SemanticLoader.secure_load_data(queriesUrl, ChimeraConstants.RDF_FORMAT_TURTLE, token);
                    InputStream inputStream = UniLoader.open(queriesUrl, token);
                }
            }
        }*/

        if(configuration.getQuery()!=null){
            sparqlQueries.add(configuration.getQuery());
            LOG.info("Query passed");
        }

        if(configuration.getResources()!=null){
            for (String query : configuration.getResources()){
                InputStream inputStream = UniLoader.open(query, token);
                //Creating a Scanner object
                Scanner scanner = new Scanner(inputStream);
                //Reading line by line from scanner to StringBuffer
                StringBuilder stringBuilder = new StringBuilder();
                while(scanner.hasNext()){
                    stringBuilder.append(scanner.nextLine());
                }
                sparqlQueries.add(stringBuilder.toString());
            }
            LOG.info("All queries extracted");
        }


        if(configuration.isNewGraph()){
            //TODO Use graph.create currently problem with config
            newGraph = new MemoryRDFGraph();
            Repository repo2 = newGraph.getRepository();
            for(String query : sparqlQueries) {
                LOG.info("Executing query on the graph: " + query);
                Model m = Repositories.graphQuery(repo, query, QueryResults::asModel);
                try (RepositoryConnection con = repo2.getConnection()) {
                    LOG.info("Added " + m.size() + " triples");
                    con.add(m);
                }
            }
            exchange.getMessage().setBody(newGraph, RDFGraph.class);
        }

        else {
            for (String query : sparqlQueries) {
                LOG.info("Executing query on the graph: " + query);
                Model m = Repositories.graphQuery(repo, query, QueryResults::asModel);
                try (RepositoryConnection con = repo.getConnection()) {
                    LOG.info("Added " + m.size() + " triples");
                    con.add(m);
                }
            }
            exchange.getMessage().setBody(graph);
        }
    }
}
