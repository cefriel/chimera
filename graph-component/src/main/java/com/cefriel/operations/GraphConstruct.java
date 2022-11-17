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
    // todo if the same query is given both with the query parameter and in the query list extracted from files deduplicate

   // todo check header params
   //  private record GraphConstructHeaderParams()
    private record GraphConstructParams(RDFGraph graph, String query, List<String> queryFilePaths, boolean newGraph) {}
     private static  GraphConstructParams configToGraphConstructParams(GraphBean operationConfig, Exchange e) {
         return new GraphConstructParams(
                 e.getMessage().getBody(RDFGraph.class),
                 operationConfig.getQuery(),
                 operationConfig.getResources(), // todo maybe add new queryFilePaths variable in config
                 operationConfig.isNewGraph()); // todo isNewGraph, maybe a better name would be inPlace (return new graph or do construct in place)
     }

    private boolean validParams(GraphConstructParams params) {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");

        if (params.query() == null || params.queryFilePaths() == null || params.queryFilePaths().size() == 0)
            // todo throw exception and print warning
            throw new IllegalArgumentException("No query and no queryFilePaths specified");

        return true;
    }
    private static List<String> mergeQueries (String query, List<String> queries) {
        if (queries != null && query != null){
            queries.add(query);
            return queries.stream().distinct().toList();
        } else if (query != null){
            return List.of(query);
        } else return List.of();
    }
    public static List<String> readSparqlQueries(List<String> queryFilePaths, String jwtToken) throws IOException {

        List<String> sparqlQueries = new ArrayList<>();

        for (String query : queryFilePaths){
            InputStream inputStream = UniLoader.open(query, jwtToken);
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
        return sparqlQueries;
    }
    // can either return a reference to the same input graph or a reference to a new graph
    public static RDFGraph executeQuery(RDFGraph graph, String query, boolean inPlace) {
        Model m = Repositories.graphQuery(graph.getRepository(), query, QueryResults::asModel);

        if (inPlace) {
            MemoryRDFGraph newGraph = new MemoryRDFGraph();
            try (RepositoryConnection con = newGraph.getRepository().getConnection()) {
                LOG.info("Added " + m.size() + " triples");
                con.add(m);
                return newGraph;
            }
        }
        else {
            try (RepositoryConnection con = graph.getRepository().getConnection()) {
                LOG.info("Added " + m.size() + " triples");
                con.add(m);
                return graph;
            }
        }
    }
    private static void graphConstruct(GraphConstructParams params, Exchange exchange) throws IOException {
        RDFGraph graph = params.graph();
        String token = exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class);
        List<String> sparqlQueries = mergeQueries(params.query(), readSparqlQueries(params.queryFilePaths(), token));

        for (String query : sparqlQueries) {
            graph = executeQuery(graph, query, params.newGraph());
        }
        // todo check maybe if some headers have to be removed or not
        exchange.getMessage().setBody(graph, RDFGraph.class);
    }
    public static void graphConstruct(Exchange exchange, GraphBean operationConfig) throws IOException {
        GraphConstructParams params = configToGraphConstructParams(operationConfig, exchange);
        graphConstruct(params, exchange);
    }
}
