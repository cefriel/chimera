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
import com.cefriel.util.ConverterConfiguration;
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

public class GraphInference {

    private static final Logger LOG = LoggerFactory.getLogger(GraphInference.class);

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
}
