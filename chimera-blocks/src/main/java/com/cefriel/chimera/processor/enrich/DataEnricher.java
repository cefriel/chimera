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
package com.cefriel.chimera.processor.enrich;

import java.util.List;

import com.cefriel.chimera.graph.MemoryRDFGraph;
import com.cefriel.chimera.graph.RDFGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;

public class DataEnricher implements Processor{

	private List<String> additionalSourcesUrls;

	public void process(Exchange exchange) throws Exception {
		Model additionalDataset;
		Repository repo;
		ValueFactory vf = SimpleValueFactory.getInstance();
	
		if (additionalSourcesUrls ==null)
			additionalSourcesUrls =exchange.getProperty(ProcessorConstants.ADDITIONAL_SOURCES, List.class);

		repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
		String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

		try (RepositoryConnection con = repo.getConnection()) {
			for (String url: additionalSourcesUrls) {
				additionalDataset = SemanticLoader.load_data(url, token);
				con.add(additionalDataset, vf.createIRI(url));
			}
		}
	}

	public List<String> getAdditionalSourcesUrls() {
		return additionalSourcesUrls;
	}

	public void setAdditionalSourcesUrls(List<String> additionalSourcesUrls) {
		this.additionalSourcesUrls = additionalSourcesUrls;
	}
}