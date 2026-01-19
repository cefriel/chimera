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
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.common.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Provides operations for validating RDF graphs against SHACL (Shapes Constraint Language) shapes.
 * <p>
 * This class handles the validation of RDF graph data using SHACL constraints. It loads
 * SHACL shape definitions from external resources, validates the graph data against these
 * shapes, and handles validation results. If validation fails, a validation report is
 * generated and can be exported for analysis.
 * </p>
 *
 * @see RDFGraph
 * @see GraphBean
 * @see ShaclSail
 */
public class GraphShacl {

    private static final Logger LOG = LoggerFactory.getLogger(GraphShacl.class);

    /**
     * Validates an RDF graph against SHACL shapes specified in the configuration.
     * <p>
     * This method loads SHACL shape definitions from the {@code chimeraResource}
     * specified in the configuration, then validates all triples in the RDF graph
     * against these shapes. If validation fails, a detailed validation report is
     * generated and written to the exchange's configured destination.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph to validate
     * @param config the configuration bean containing the SHACL shapes resource
     * @throws IllegalArgumentException if no SHACL resource is specified
     * @throws Exception if shape loading, validation execution, or report generation fails
     */
    public static void graphShacl(Exchange exchange, GraphBean config) throws Exception {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);
        ChimeraResourceBean shaclResource = config.getChimeraResource();

        if (shaclResource == null) {
            throw new IllegalArgumentException("No SHACL resource specified");
        }

        executeValidation(graph, shaclResource, exchange);
    }

    /**
     * Executes SHACL validation on an RDF graph using the provided shapes resource.
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *   <li>Creates a temporary SHACL-enabled Sail repository</li>
     *   <li>Loads the SHACL shapes from the specified resource into the shapes graph</li>
     *   <li>Loads the data from the RDF graph to be validated</li>
     *   <li>Commits the transaction, triggering automatic SHACL validation</li>
     *   <li>If validation fails, generates and exports a validation report</li>
     * </ol>
     * <p>
     * The RDF format for the shapes resource is detected from the serialization format
     * specified in the {@code ChimeraResourceBean}, defaulting to Turtle if not specified.
     * </p>
     *
     * @param graph the RDF graph containing data to validate
     * @param shaclResource the resource containing SHACL shape definitions
     * @param exchange the Camel exchange providing context for resource resolution and report output
     * @throws Exception if shape loading, validation execution, or report generation fails
     */
    private static void executeValidation(RDFGraph graph, ChimeraResourceBean shaclResource, Exchange exchange) throws Exception {
        SailRepository sailRepository = new SailRepository(new ShaclSail(new MemoryStore()));
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {
            // Load SHACL shapes
            try (InputStream is = ResourceAccessor.open(shaclResource, exchange)) {
                connection.begin();
                RDFFormat format = Rio.getParserFormatForMIMEType(shaclResource.getSerializationFormat())
                        .orElse(RDFFormat.TURTLE);
                connection.add(is, "", format, RDF4J.SHACL_SHAPE_GRAPH);
                connection.commit();
            }

            // Validate data
            connection.begin();
            try (RepositoryConnection data = graph.getRepository().getConnection()) {
                connection.add(data.getStatements(null, null, null, true));
            }

            try {
                connection.commit();
                LOG.info("Validation executed correctly");
            } catch (RepositoryException exception) {
                handleValidationException(exception, exchange);
            }
        } finally {
            sailRepository.shutDown();
        }
    }

    /**
     * Handles validation exceptions by extracting and exporting the validation report.
     * <p>
     * When SHACL validation fails, the RDF4J SHACL Sail throws a {@link RepositoryException}
     * wrapping a {@link ValidationException}. This method extracts the validation report
     * model from the exception and writes it to a file for analysis.
     * </p>
     *
     * @param exception the repository exception containing the validation failure
     * @param exchange the Camel exchange providing context for report file generation
     * @throws IOException if the validation report cannot be written to file
     */
    private static void handleValidationException(RepositoryException exception, Exchange exchange) throws IOException {
        Throwable cause = exception.getCause();
        if (cause instanceof ValidationException validationException) {
            Model validationReportModel = validationException.validationReportAsModel();
            String path = Utils.writeModelToDestination(exchange, validationReportModel, "validation-report");
            LOG.error("Validation report dumped to file " + path);
        }
    }

    /**
     * Validates an RDF graph against multiple SHACL shapes from a resources list in the configuration header.
     * <p>
     * This alternative validation method retrieves the configuration from the exchange's
     * {@code ChimeraConstants.CONFIGURATION} header, which contains a list of SHACL shape
     * resources. It loads all shapes into the validation repository and validates the
     * graph against the combined constraints. This approach is useful when validation
     * shapes are distributed across multiple files.
     * </p>
     * <p>
     * If the resources list is empty, the method returns without performing validation.
     * </p>
     *
     * @param exchange the Camel exchange containing the RDF graph and configuration header
     * @throws IOException if shape loading, validation execution, or report generation fails
     */
    public static void graphShacl(Exchange exchange) throws IOException {
        RDFGraph graph = ParameterUtils.requireGraph(exchange);
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);

        if (configuration.getResources().isEmpty()) {
            return;
        }

        SailRepository sailRepository = new SailRepository(new ShaclSail(new MemoryStore()));
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {
            // Load all SHACL shapes
            for (String shape : configuration.getResources()) {
                try (InputStream is = UniLoader.open(shape,
                        exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class))) {
                    connection.begin();
                    RDFFormat format = Rio.getParserFormatForFileName(shape).orElse(RDFFormat.TURTLE);
                    connection.add(is, "", format, RDF4J.SHACL_SHAPE_GRAPH);
                    connection.commit();
                }
            }

            // Validate data
            connection.begin();
            try (RepositoryConnection data = graph.getRepository().getConnection()) {
                connection.add(data.getStatements(null, null, null, true));
            }

            try {
                connection.commit();
                LOG.info("Validation executed correctly");
            } catch (RepositoryException exception) {
                handleValidationException(exception, exchange);
            }
        } finally {
            sailRepository.shutDown();
        }
    }
}
