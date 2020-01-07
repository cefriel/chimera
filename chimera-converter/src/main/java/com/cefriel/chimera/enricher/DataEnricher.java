/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.enricher;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import com.cefriel.chimera.context.MemoryRDFGraph;

public class DataEnricher  implements AggregationStrategy{

	@Override
	public Exchange aggregate(Exchange mainExchange, Exchange resourceExchange) {
		Message in = mainExchange.getIn();
		Message datasetMsg = resourceExchange.getIn();

		MemoryRDFGraph graph=in.getBody(MemoryRDFGraph.class);
		Object datasetBody=datasetMsg.getBody();

		Repository schema_repo = graph.getRepository();

		try (RepositoryConnection con = schema_repo.getConnection()) {

			if (List.class.isInstance(datasetBody)) {
				List<Model> datasets=(List)datasetBody;
				for (Model datasetModel: datasets) {
					con.add(datasetModel);
				}
			}
			else {
				Model dataset=(Model)datasetBody;
				con.add(dataset);
			}
		}

		if (mainExchange.getPattern().isOutCapable()) {
			mainExchange.getOut().setBody(graph);
		} else {
			mainExchange.getIn().setBody(graph);
		}
		return mainExchange;
	}
}