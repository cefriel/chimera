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
import com.cefriel.util.*;
import org.apache.camel.CamelContext;
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
    private record EndpointParams(String literalQuery, ChimeraResourcesBean queryUrls, String namedGraph) {}
    private record OperationParams(RDFGraph graph, EndpointParams endpointParams) {}
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getQuery(),
                operationConfig.getChimeraResources(),
                operationConfig.getNamedGraph());
    }
    private static OperationParams getOperationParams (Exchange e, GraphBean operationConfig) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                getEndpointParams(operationConfig));
    }

    private static boolean validParams(OperationParams params) {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");

        if (params.endpointParams().literalQuery() == null &&
                (params.endpointParams().queryUrls() == null || params.endpointParams().queryUrls().getResources().size() == 0))
            throw new IllegalArgumentException("No query and no queryUrls specified");

        return true;
    }
    private static List<String> mergeQueries (String query, List<String> queries) {
        if (queries != null && query != null){
            queries.add(query);
            return queries.stream().distinct().toList();
        } else if (query != null){
            return List.of(query);
        } else return queries;
    }
    public static List<String> readSparqlQueries(ChimeraResourcesBean queryUrls, CamelContext context) throws IOException {

        List<String> sparqlQueries = new ArrayList<>();

        for(ChimeraResourceBean queryUrl : queryUrls.getResources()) {
            InputStream inputStream = ResourceAccessor.open(queryUrl, context);
            //Creating a Scanner object
            Scanner scanner = new Scanner(inputStream);
            //Reading line by line from scanner to StringBuffer
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNext()){
                stringBuilder.append(scanner.nextLine());
            }
            sparqlQueries.add(stringBuilder.toString());
            inputStream.close();
        }
        LOG.info("All queryUrls extracted");
        return sparqlQueries;
    }
    private static void executeQueryOnGraph(RDFGraph graph, String query, String namedGraph) {
        Model m = Repositories.graphQuery(graph.getRepository(), query, QueryResults::asModel);
        RepositoryConnection con = graph.getRepository().getConnection();
        LOG.info("Added " + m.size() + " triples");
        if(namedGraph != null)
            con.add(m, Utils.stringToIRI(namedGraph));
        else
            con.add(m);
        con.close();
    }

    public static void graphConstruct(Exchange exchange, GraphBean operationConfig) throws IOException {
        OperationParams operationParams = getOperationParams(exchange, operationConfig);

        if (validParams(operationParams)) {
            RDFGraph graph = operationParams.graph();
            graphConstruct(graph, operationParams, exchange.getContext());
            exchange.getMessage().setBody(graph, RDFGraph.class);
        }
    }
    private static void graphConstruct(RDFGraph graph, OperationParams params, CamelContext context) throws IOException {
        List<String> sparqlQueries = mergeQueries(
                params.endpointParams().literalQuery(),
                readSparqlQueries(params.endpointParams().queryUrls(), context));

        for (String query : sparqlQueries) {
            executeQueryOnGraph(graph, query, params.endpointParams().namedGraph());
        }
    }
}
