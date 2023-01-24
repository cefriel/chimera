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

package com.cefriel.component;

import com.cefriel.graph.RDFGraph;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class GraphAggregation implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        RDFGraph oldGraph = oldExchange.getMessage().getBody(RDFGraph.class);
        RDFGraph newGraph = newExchange.getMessage().getBody(RDFGraph.class);
        Repository oldRepo = oldGraph.getRepository();
        Repository newRepo = newGraph.getRepository();

        try (RepositoryConnection conn = oldRepo.getConnection()) {
            try (RepositoryConnection source = newRepo.getConnection()) {
                conn.add(source.getStatements(null, null, null, true));
            }
        }
        return oldExchange;
    }
}
