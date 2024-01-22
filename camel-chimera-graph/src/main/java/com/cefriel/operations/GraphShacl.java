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
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ast.constraintcomponents.XoneConstraintComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GraphShacl {

    private static final Logger LOG = LoggerFactory.getLogger(GraphShacl.class);
    private record EndpointParams(ChimeraResourceBean shaclResource) {}
    private record OperationParams(RDFGraph graph, EndpointParams endpointParams) {}

    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(operationConfig.getChimeraResource());
    }
    private static OperationParams getOperationParams(Exchange exchange, GraphBean operationConfiguration) {
        return new OperationParams(
                exchange.getMessage().getBody(RDFGraph.class),
                getEndpointParams(operationConfiguration));
    }
    public static void graphShacl(Exchange exchange, GraphBean operationConfiguration) throws Exception {
        OperationParams operationParams = getOperationParams(exchange, operationConfiguration);

        List<ChimeraResourceBean> shaclUrls = List.of(operationParams.endpointParams().shaclResource());
        if (!shaclUrls.isEmpty()) {
            SailRepository sailRepository = new SailRepository(new ShaclSail(new MemoryStore()));
            sailRepository.init();
            try (SailRepositoryConnection connection = sailRepository.getConnection()) {
                for (ChimeraResourceBean shacleUrl : shaclUrls) {
                    InputStream is = ResourceAccessor.open(shacleUrl, exchange);
                    connection.begin();
                    RDFFormat format = Rio.getParserFormatForMIMEType(shacleUrl.getSerializationFormat()).orElse(RDFFormat.TURTLE);
                    connection.add(is, "", format, RDF4J.SHACL_SHAPE_GRAPH);
                    connection.commit();
                }

                connection.begin();
                RepositoryConnection data = operationParams.graph().getRepository().getConnection();
                //Enable inference
                connection.add(data.getStatements(null, null, null, true));
                data.close();
                try {
                    connection.commit();
                } catch (RepositoryException exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof ValidationException) {
                        Model validationReportModel = ((ValidationException) cause).validationReportAsModel();
                        String path = Utils.writeModelToDestination(exchange, validationReportModel, "validation-report");
                        LOG.error("Validation report dumped to file " + path);
                    }
                } finally {
                    connection.close();
                    sailRepository.shutDown();
                }
                LOG.info("Validation executed correctly");
            }
        }
    }
    public static void graphShacl(Exchange exchange) throws IOException {

        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = graph.getRepository();

        if (!configuration.getResources().isEmpty()) {
            SailRepository sailRepository = new SailRepository(new ShaclSail(new MemoryStore()));
            sailRepository.init();
            try (SailRepositoryConnection connection = sailRepository.getConnection()) {
                for (String shape : configuration.getResources()) {
                    InputStream is = UniLoader.open(shape,
                            exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class));
                    connection.begin();
                    RDFFormat format = Rio.getParserFormatForFileName(shape).orElse(RDFFormat.TURTLE);
                    connection.add(is, "", format, RDF4J.SHACL_SHAPE_GRAPH);
                    connection.commit();
                }

                connection.begin();
                RepositoryConnection data = repo.getConnection();
                //Enable inference
                connection.add(data.getStatements(null, null, null, true));
                data.close();
                try {
                    connection.commit();
                } catch (RepositoryException exception) {
                    Throwable cause = exception.getCause();
                    if (cause instanceof ValidationException) {
                        Model validationReportModel = ((ValidationException) cause).validationReportAsModel();
                        String path = Utils.writeModelToDestination(exchange, validationReportModel, "validation-report");
                        LOG.error("Validation report dumped to file " + path);
                    }
                } finally {
                    connection.close();
                    sailRepository.shutDown();
                }
                LOG.info("Validation executed correctly");
            }
        }
    }
}
