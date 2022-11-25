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
import com.cefriel.util.StreamParser;
import com.cefriel.util.UniLoader;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GraphAdd {

    private static final Logger LOG = LoggerFactory.getLogger(GraphAdd.class);

    // needs jwt token, rdf format, resources (ontologyUrls),
    // contentType (MimeType) set to outoging exchange
    private record HeaderParams(String rdfFormat) {}
    private record EndpointParams(String rdfFormat, List<String> ontologyPaths) {}
    private record OperationParams(RDFGraph graph, String jwtToken, EndpointParams operationParams) {}
    private static boolean validParams(OperationParams params) {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");

        if (params.operationParams().rdfFormat() == null)
            throw new IllegalArgumentException("rdfFormat parameter can not be null");

        if (!ChimeraConstants.SUPPORTED_RDF_FORMATS.contains(params.operationParams().rdfFormat()))
            throw new IllegalArgumentException("Invalid specified rdfFormat, supported formats are: " +
                    String.join(",", ChimeraConstants.SUPPORTED_RDF_FORMATS));

        if (params.operationParams().ontologyPaths() == null || params.operationParams().ontologyPaths().size() == 0)
            throw new IllegalArgumentException("No ontology url specified");

        return true;
    }
    private static HeaderParams getHeaderParams(Exchange e) {
        return new HeaderParams(e.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class));
    }
    private static EndpointParams getEndpointParams(GraphBean config) {
        return new EndpointParams(config.getRdfFormat(), config.getResources());
    }
    private static EndpointParams mergeHeaderParams(HeaderParams headerParams, EndpointParams params) {
        return new EndpointParams(
                headerParams.rdfFormat() != null ? headerParams.rdfFormat() : params.rdfFormat(),
                params.ontologyPaths());
    }
    private static OperationParams getOperationParams(RDFGraph graph, Exchange exchange, GraphBean operationConfig) {
        return new OperationParams(
                graph,
                exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class),
                mergeHeaderParams(getHeaderParams(exchange), getEndpointParams(operationConfig)));
    }
    record GraphAndExchange (RDFGraph graph, Exchange exchange) {}

    public static GraphAndExchange graphAdd(Exchange exchange, GraphBean operationConfig) throws IOException {
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        return graphAdd(graph, exchange, operationConfig);
    }
    public static GraphAndExchange graphAdd(RDFGraph graph, Exchange exchange, GraphBean operationConfig) throws IOException {
        OperationParams params = getOperationParams(graph, exchange, operationConfig);
        if (validParams(params))
            return graphAdd(params, exchange);
        return null;
    }
    private static GraphAndExchange graphAdd(OperationParams params, Exchange exchange) throws IOException {
        if (validParams(params)){
            for (String ontologyUrl : params.operationParams().ontologyPaths()) {
                Model model = parseOntology(exchange, ontologyUrl, params.operationParams().rdfFormat(), params.jwtToken());
                Repository repo = params.graph().getRepository();
                try (RepositoryConnection con = repo.getConnection()) {
                    con.add(model);
                    for (Namespace ns : model.getNamespaces()) {
                        con.setNamespace(ns.getPrefix(), ns.getName());
                    }
                }
                LOG.info(model.size() + " triples added to the graph");
            }
            return new GraphAndExchange(params.graph(), exchange);
        }
        throw new IllegalArgumentException("One or more parameters for the GraphAdd operation are invalid");
    }

    // todo this method might be refactored to utils
    public static Model parseOntology(Exchange exchange, String ontologyUrl, String rdfFormat, String jwtToken) throws IOException {
        InputStream inputStream = UniLoader.open(ontologyUrl, jwtToken);
        LOG.info("InputStream loaded from path " + ontologyUrl);
        Rio.getParserFormatForFileName(ontologyUrl)
                .ifPresent(format -> exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, format.getMIMETypes()));
        return StreamParser.parse(inputStream, exchange);
    }
}
