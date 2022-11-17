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
    private record GraphAddHeaderParams(String rdfFormat) {}
    private record GraphAddParams(String rdfFormat,
                                  List<String> ontologyPaths,
                                  RDFGraph graph) {}


    private boolean validParams(GraphAddParams params) {
        if (params.graph() == null)
            throw new RuntimeException("graph in Exchange body cannot be null");

        if (params.rdfFormat() == null)
            // todo throw exception and print warning
            throw new IllegalArgumentException("rdfFormat parameter can not be null");

        if (!ChimeraConstants.SUPPORTED_RDF_FORMATS.contains(params.rdfFormat()))
            throw new IllegalArgumentException("Invalid specified rdfFormat, supported formats are: " +
                    String.join(",", ChimeraConstants.SUPPORTED_RDF_FORMATS));

        if (params.ontologyPaths() == null || params.ontologyPaths().size() == 0)
            // todo throw exception and print warning
            throw new IllegalArgumentException("No ontology url specified");

        return true;
    }

    private static GraphAddHeaderParams exchangeToGraphAddHeaderParams(Exchange e) {
        return new GraphAddHeaderParams(e.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class));
    }
    private static GraphAddParams configToGraphAddParams(GraphBean config, Exchange e) {
        return new GraphAddParams(config.getRdfFormat(), config.getResources(),
                e.getMessage().getBody(RDFGraph.class));
    }

    private static GraphAddParams mergeHeaderParams(GraphAddHeaderParams headerParams, GraphAddParams params) {
        return new GraphAddParams(
                headerParams.rdfFormat() != null ? headerParams.rdfFormat() : params.rdfFormat(),
                params.ontologyPaths(),
                params.graph());
    }
    record GraphAndExchange (RDFGraph graph, Exchange exchange) {}
    public static GraphAndExchange graphAdd(RDFGraph graph, Exchange exchange, GraphBean config) throws IOException {

        // todo handle this in validParams, graph as param
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");

        GraphAddParams params = mergeHeaderParams(exchangeToGraphAddHeaderParams(exchange), configToGraphAddParams(config, exchange));
        for (String ontologyUrl : params.ontologyPaths()) {
            Model model = parseOntology(exchange, ontologyUrl, params.rdfFormat(), exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class));
            Repository repo = graph.getRepository();
            try (RepositoryConnection con = repo.getConnection()) {
                con.add(model);
                for (Namespace ns : model.getNamespaces()) {
                    con.setNamespace(ns.getPrefix(), ns.getName());
                }
            }
            LOG.info(model.size() + " triples added to the graph");
        }
        return new GraphAndExchange(graph, exchange);
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
