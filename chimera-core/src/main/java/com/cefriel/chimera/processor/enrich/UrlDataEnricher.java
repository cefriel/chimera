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

import java.util.ArrayList;
import java.util.List;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.processor.onexception.OnExceptionInspectProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlDataEnricher implements Processor {

	private Logger logger = LoggerFactory.getLogger(UrlDataEnricher.class);

	private List<String> additionalSourcesUrls;

	public void process(Exchange exchange) throws Exception {
		Model additionalDataset;
		RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
		if (graph == null)
			throw new RuntimeException("RDF Graph not attached");
		Repository repo = graph.getRepository();
	
		if (additionalSourcesUrls == null)
			additionalSourcesUrls = exchange.getProperty(ProcessorConstants.ADDITIONAL_SOURCES, List.class);

		if (additionalSourcesUrls == null)
			additionalSourcesUrls = new ArrayList<>();

		String additionalSource = exchange.getMessage().getHeader(ProcessorConstants.ADDITIONAL_SOURCE, String.class);
		if (additionalSource != null)
			additionalSourcesUrls.add(additionalSource);

		String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

		try (RepositoryConnection con = repo.getConnection()) {
			for (String url : additionalSourcesUrls) {
				additionalDataset = SemanticLoader.load_data(url, token);
				if (additionalDataset == null)
					logger.error("No data loaded! URL: " + url);
				else {
					IRI contextIRI = graph.getContext();
					if (contextIRI != null)
						con.add(additionalDataset, contextIRI);
					else
						con.add(additionalDataset);
				}
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