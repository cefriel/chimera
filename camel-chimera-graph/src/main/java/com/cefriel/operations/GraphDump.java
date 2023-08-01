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
import com.cefriel.component.GraphEndpoint;
import com.cefriel.graph.RDFGraph;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.RDFSerializer;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class GraphDump {

    private static final Logger LOG = LoggerFactory.getLogger(GraphDump.class);

    // todo (maybe this can be changed) removed rdfFormat from headers because it would cause problems in a multicast operation (dump and add for example)
    private record HeaderParams(String fileName) {}
    private record EndpointParams(String dumpRDFFormat, String basePath, String fileName) {}
    private record OperationParams(RDFGraph graph, EndpointParams endpointParams) {}

    private static HeaderParams getHeaderParams(Exchange e) {
        return new HeaderParams(e.getMessage().getHeader(ChimeraConstants.FILENAME, String.class));
    }
    private static EndpointParams getEndpointParams(GraphBean operationConfig) {
        return new EndpointParams(
                operationConfig.getDumpFormat(),
                operationConfig.getBasePath(),
                operationConfig.getFilename());
    }

    private static EndpointParams mergeHeaders(HeaderParams h, EndpointParams p) {
        return new EndpointParams(
                p.dumpRDFFormat(),
                p.basePath(),
                h.fileName() == null ? p.fileName() : h.fileName());
    }
    private static OperationParams getOperationParams(Exchange e, GraphBean operationConfig) {
        return new OperationParams(
                e.getMessage().getBody(RDFGraph.class),
                mergeHeaders(getHeaderParams(e), getEndpointParams(operationConfig)));
    }
    private static Boolean validParams(OperationParams params) {
        if (params.graph == null)
            throw new IllegalArgumentException("Graph in body of exchange can not be null");

        if (params.endpointParams().dumpRDFFormat() == null)
            throw new IllegalArgumentException("No dumpFormat parameter supplied to DUMP operation");

        return true;
    }
    public static void graphDump(Exchange exchange, GraphBean operationConfig) throws IOException {
         OperationParams params = getOperationParams(exchange, operationConfig);
         if (validParams(params))
             graphDump(params, exchange);
    }
    private static void graphDump(OperationParams params, Exchange exchange) throws IOException {
        try(RepositoryConnection con = params.graph().getRepository().getConnection()) {
            RepositoryResult<Statement> dump;
            dump = con.getStatements(null, null, null);
            Model dumpModel = QueryResults.asModel(dump);

            RepositoryResult<Namespace> namespaces = con.getNamespaces();
            for (Namespace n : namespaces.stream().toList())
                dumpModel.setNamespace(n);

            if (params.endpointParams().basePath() != null && params.endpointParams().fileName() != null) {
                String path = Utils.writeModelToDestination(
                        dumpModel,
                        params.endpointParams().dumpRDFFormat(),
                        params.endpointParams().basePath(),
                        params.endpointParams().fileName());
		
                LOG.info("Graph dumped to file " + path);
            } else {
		// sets serialized model as body of the exchange passed as input
                RDFSerializer.serialize(dumpModel, params.endpointParams().dumpRDFFormat(), exchange);
                LOG.info("Model dump set as body");
            }
        }
    }
}
