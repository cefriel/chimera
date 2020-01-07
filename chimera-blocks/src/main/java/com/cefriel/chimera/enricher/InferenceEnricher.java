/*
 * Copyright 2018 Cefriel.
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
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.cefriel.chimera.context.MemoryRDFGraph;

public class InferenceEnricher implements AggregationStrategy{
	@Override
	public Exchange aggregate(Exchange mainExchange, Exchange resourceExchange) {
		Message in = mainExchange.getIn();
		Message ontologiesMsg = resourceExchange.getIn();

		MemoryRDFGraph graph=in.getBody(MemoryRDFGraph.class);
		Object ontologyBody=ontologiesMsg.getBody();

		Sail data=graph.getData();

		Repository schema_repo = new SailRepository( new MemoryStore());

		schema_repo.init();
		try (RepositoryConnection con = schema_repo.getConnection()) {

			if (List.class.isInstance(ontologyBody)) {
				List<Model> ontologies=(List)ontologyBody;
				for (Model ontologyModel: ontologies) {
					con.add(ontologyModel);
				}
			}
			else {
				Model ontology=(Model)ontologyBody;
				con.add(ontology);
			}
		}

		SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer((NotifyingSail)data, schema_repo, false);
		inferencer.initialize();
		graph.setData(inferencer);

		if (mainExchange.getPattern().isOutCapable()) {
			mainExchange.getOut().setBody(graph);
		} else {
			mainExchange.getIn().setBody(graph);
		}
		return mainExchange;
	}

}