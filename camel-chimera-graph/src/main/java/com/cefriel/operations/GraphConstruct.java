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
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GraphConstruct {

    private static final Logger LOG = LoggerFactory.getLogger(GraphConstruct.class);
    private record EndpointParams(String literalQuery, ChimeraResourceBean queryResource, String namedGraph, boolean newGraph) {}
    private record OperationParams(RDFGraph graph, EndpointParams endpointParams) {}
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getQuery(),
                operationConfig.getChimeraResource(),
                operationConfig.getNamedGraph(),
                operationConfig.isNewGraph());
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
                params.endpointParams().queryResource() == null)
            throw new IllegalArgumentException("No query and no queryResource specified");

        return true;
    }
    public static String readSparqlQuery(ChimeraResourceBean queryResource, Exchange exchange) throws Exception {
        InputStream inputStream = ResourceAccessor.open(queryResource, exchange);
        //Creating a Scanner object
        Scanner scanner = new Scanner(inputStream);
        //Reading line by line from scanner to StringBuffer
        StringBuilder stringBuilder = new StringBuilder();
        while(scanner.hasNext()){
            stringBuilder.append(scanner.nextLine());
        }
        String result = (stringBuilder.toString());
        inputStream.close();

        return result;
    }
    private static RDFGraph executeQueryOnGraph(RDFGraph graph, String query, String namedGraphs, boolean newGraph) {
        Model constructResult = Repositories.graphQuery(graph.getRepository(), query, QueryResults::asModel);
        RDFGraph resultGraph = newGraph ? new MemoryRDFGraph() : graph;

        try (RepositoryConnection con = resultGraph.getRepository().getConnection()) {
            if (namedGraphs != null) {
                List<IRI> namedGraphsList = Arrays.stream(namedGraphs.split(";")).map(Utils::stringToIRI).toList();
                for (IRI namedGraph : namedGraphsList) {
                    con.add(constructResult, namedGraph);
                    LOG.info("Added {} triples to {}", constructResult.size(), namedGraph);
                }
            } else {
                Utils.populateRepository(resultGraph.getRepository(), constructResult);
                LOG.info("Added {} triples", constructResult.size());
            }
        }

        return resultGraph;
    }

    public static void graphConstruct(Exchange exchange, GraphBean operationConfig) throws Exception {
        OperationParams operationParams = getOperationParams(exchange, operationConfig);

        if (validParams(operationParams)) {
            RDFGraph graph = operationParams.graph();
            RDFGraph result = graphConstruct(graph, operationParams, exchange);
            exchange.getMessage().setBody(result, RDFGraph.class);
        }
    }
    private static RDFGraph graphConstruct(RDFGraph graph, OperationParams params, Exchange exchange) throws Exception {

        if (params.endpointParams().literalQuery() != null) {
            return executeQueryOnGraph(graph, params.endpointParams().literalQuery(), params.endpointParams().namedGraph(), params.endpointParams().newGraph());
        } else if (params.endpointParams().queryResource() != null) {
            return executeQueryOnGraph(graph, readSparqlQuery(params.endpointParams().queryResource(), exchange),
                    params.endpointParams().namedGraph(), params.endpointParams().newGraph());
        }
        else {
            throw new IllegalArgumentException("No query and no queryResource specified");
        }
    }
}
