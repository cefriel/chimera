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

    public static void graphDump(Exchange exchange) throws IOException {

        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);
        Repository repo = graph.getRepository();

        if(configuration.getDumpFormat() != null)
            exchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, configuration.getDumpFormat());

        try(RepositoryConnection con = repo.getConnection()) {
            RepositoryResult<Statement> dump;
            dump = con.getStatements(null, null, null);
            Model dumpModel = QueryResults.asModel(dump);

            RepositoryResult<Namespace> namespaces = con.getNamespaces();
            for (Namespace n : Iterations.asList(namespaces))
                dumpModel.setNamespace(n);


            //TODO Change this, add specific option to save as file or set as body

            //TODO Change this, add specific option to save as file or set as body
            if (configuration.getBasePath() != null) {
                String path = Utils.writeModelToDestination(exchange, dumpModel, "graph-dump");
                LOG.info("Graph dumped to file " + path);
            } else {
                InputStream inputStream = RDFSerializer.serialize(dumpModel, exchange);
                exchange.getMessage().setBody(inputStream);
                LOG.info("Model dump set as body");
            }
        }
    }
}
