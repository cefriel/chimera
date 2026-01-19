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
import com.cefriel.util.ParameterUtils;
import com.cefriel.util.RDFSerializer;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Provides operations for serializing and exporting RDF graph data.
 * <p>
 * This class handles the export of complete RDF graphs, including all triples and
 * namespace declarations, to various serialization formats. The output can be written
 * to files or set as the exchange message body. Supported formats include Turtle, RDF/XML,
 * N-Triples, JSON-LD, and others supported by RDF4J.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see RDFSerializer
 */
public class GraphDump {

    private static final Logger LOG = LoggerFactory.getLogger(GraphDump.class);

    /**
     * Dumps the complete RDF graph to a specified format.
     * <p>
     * This method extracts all statements and namespaces from the RDF graph and
     * serializes them according to the specified format. The output destination is
     * determined by the configuration:
     * </p>
     * <ul>
     *   <li>If both {@code basePath} and {@code filename} are provided, writes to a file</li>
     *   <li>Otherwise, sets the serialized content as the exchange message body</li>
     * </ul>
     * <p>
     * The filename can be specified either in the configuration or via the
     * {@code ChimeraConstants.FILENAME} header, with the header taking precedence.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph to dump
     * @param config the configuration bean containing the dump format, base path, and filename
     * @throws IllegalArgumentException if no dump format is specified
     * @throws IOException if an error occurs during file writing or serialization
     */
    public static void graphDump(Exchange exchange, GraphBean config) throws IOException {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);

        String dumpFormat = config.getDumpFormat();
        if (dumpFormat == null) {
            throw new IllegalArgumentException("No dumpFormat parameter supplied to DUMP operation");
        }

        String headerValue = exchange.getMessage().getHeader(ChimeraConstants.FILENAME, String.class);
        String filename = ParameterUtils.resolveParam(headerValue, config.getFilename());

        try (RepositoryConnection con = graph.getRepository().getConnection()) {
            RepositoryResult<Statement> dump = con.getStatements(null, null, null);
            Model dumpModel = QueryResults.asModel(dump);

            RepositoryResult<Namespace> namespaces = con.getNamespaces();
            for (Namespace n : namespaces.stream().toList()) {
                dumpModel.setNamespace(n);
            }

            if (config.getBasePath() != null && filename != null) {
                String path = Utils.writeModelToDestination(dumpModel, dumpFormat, config.getBasePath(), filename);
                LOG.info("Graph dumped to file {}", path);
            } else {
                RDFSerializer.serialize(dumpModel, dumpFormat, exchange);
                LOG.info("Model dump set as body");
            }
        }
    }
}
