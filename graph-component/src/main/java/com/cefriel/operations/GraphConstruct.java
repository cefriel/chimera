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
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.UniLoader;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class GraphConstruct {

    private static final Logger LOG = LoggerFactory.getLogger(GraphConstruct.class);
    // todo if the same query is given both with the query parameter and in the query list extracted from files deduplicate
    private record HeaderParams() {}
    private record EndpointParams(String query, List<String> queryUrls, Boolean newGraph) {}
    private record OperationParams(RDFGraph graph, String jwtToken, EndpointParams endpointParams) {}
    private static EndpointParams getEndpointParams(GraphBean operationConfig, Exchange e) {
        return new EndpointParams(
                operationConfig.getQuery(),
                operationConfig.getQueryUrls(),
                operationConfig.isNewGraph());
    }
    private static OperationParams getOperationParams (Exchange e, GraphBean operationConfig) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                e.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class),
                getEndpointParams(operationConfig, e));
    }

    private static boolean validParams(OperationParams params) {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");

        if (params.endpointParams().query() == null &&
                (params.endpointParams().queryUrls() == null || params.endpointParams().queryUrls().size() == 0))
            // todo throw exception and print warning
            throw new IllegalArgumentException("No query and no queryUrls specified");

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
    public static List<String> readSparqlQueries(List<String> queryUrls, String jwtToken) throws IOException {

        List<String> sparqlQueries = new ArrayList<>();

        for (String query : queryUrls){
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
    private static void executeQueryOnGraph(RDFGraph graph, String query) {
        Model m = Repositories.graphQuery(graph.getRepository(), query, QueryResults::asModel);
        try (RepositoryConnection con = graph.getRepository().getConnection()) {
            LOG.info("Added " + m.size() + " triples");
            con.add(m);
        }
    }
    public static void graphConstruct(Exchange exchange, GraphBean operationConfig) throws IOException {
        OperationParams operationParams = getOperationParams(exchange, operationConfig);

        if (validParams(operationParams)) {
            if (operationParams.endpointParams().newGraph()) {
                RDFGraph newGraph = GraphObtain.obtainGraph(exchange, operationConfig).graph();
                graphConstruct(newGraph, operationParams, exchange);
            }
            else {
                graphConstruct(operationParams.graph(), operationParams, exchange);
            }
        }
    }

    private static void graphConstruct(RDFGraph graph, OperationParams params, Exchange exchange) throws IOException {
        List<String> sparqlQueries = mergeQueries(
                params.endpointParams().query(),
                readSparqlQueries(params.endpointParams().queryUrls(), params.jwtToken()));

        for (String query : sparqlQueries) {
            executeQueryOnGraph(graph, query);
        }
        
        exchange.getMessage().setBody(graph, RDFGraph.class);
    }
}
