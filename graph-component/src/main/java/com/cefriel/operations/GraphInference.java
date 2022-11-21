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
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class GraphInference {

    private static final Logger LOG = LoggerFactory.getLogger(GraphInference.class);
    // todo rdfFormat can be a header param, used like ontologyFormat
    private record HeaderParams(String rdfFormat) {}
    private record EndpointParams(String ontologyFormat, List<String> ontologyUrls, boolean allRules){}
    private record OperationParams(RDFGraph graph, String jwtToken, EndpointParams endpointParams){}
    private static HeaderParams getHeaderParams(Exchange e) {
        return new HeaderParams(e.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class));
    }
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getOntologyFormat(),
                operationConfig.getResources(),
                operationConfig.isAllRules());
    }
    private static EndpointParams mergeHeaders(HeaderParams h, EndpointParams p) {
        return new EndpointParams(
                h.rdfFormat() == null ? p.ontologyFormat() : h.rdfFormat(),
                p.ontologyUrls(),
                p.allRules());
    }
    private static OperationParams getOperationParams(Exchange e, GraphBean operationConfig) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                e.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class),
                mergeHeaders(getHeaderParams(e), getEndpointParams(operationConfig)));
    }

    private static boolean validParams(OperationParams params) {
        if (params.endpointParams().ontologyFormat() == null)
            throw new IllegalArgumentException("No rdfFormat specified for INFERENCE operation");

        return true;
    }
    public static void graphInference(Exchange exchange, GraphBean operationConfig) throws IOException {
        var p = getOperationParams(exchange, operationConfig);
        if (validParams(p)) {
            graphInference(p, exchange);
        }
    }
    public static void graphInference(OperationParams params, Exchange exchange) throws IOException {
        Repository schema = Utils.getSchemaRepository(params.endpointParams().ontologyUrls(), params.jwtToken(), params.endpointParams().ontologyFormat(), exchange);
        SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer(new MemoryStore(), schema, params.endpointParams().allRules());
        Repository inferenceRepo = new SailRepository(inferencer);
        inferenceRepo.init();

        RepositoryConnection source = params.graph().getRepository().getConnection();
        RepositoryConnection target = inferenceRepo.getConnection();
        //Enable inference
        target.add(source.getStatements(null, null, null, true));
        //Copy back
        source.add(target.getStatements(null, null, null, true));
        source.close();
        target.close();
        exchange.getMessage().setBody(params.graph(), RDFGraph.class);
    }
    /*
    public static void graphInference(Exchange exchange) throws IOException {

        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = graph.getRepository();

        configuration.getResources().addAll(ConverterConfiguration.setConverterConfiguration(configuration, exchange));
        if(configuration.getOntologyFormat() != null)
            exchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, configuration.getOntologyFormat());
        if (!configuration.getResources().isEmpty()) {

            Repository schema = Utils.getSchemaRepository(configuration, exchange);
            SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer(new MemoryStore(), schema, configuration.isAllRules());
            Repository inferenceRepo = new SailRepository(inferencer);
            inferenceRepo.init();

            RepositoryConnection source = repo.getConnection();
            RepositoryConnection target = inferenceRepo.getConnection();
            //Enable inference
            target.add(source.getStatements(null, null, null, true));
            //Copy back
            source.add(target.getStatements(null, null, null, true));
            source.close();
            target.close();
            exchange.getMessage().setBody(graph, RDFGraph.class);
        }
        exchange.getMessage().removeHeader(ChimeraConstants.RDF_FORMAT);
    }

     */
}
