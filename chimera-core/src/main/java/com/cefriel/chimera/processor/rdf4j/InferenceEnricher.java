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
package com.cefriel.chimera.processor.rdf4j;

import java.util.List;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.cefriel.chimera.graph.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;

public class InferenceEnricher implements Processor {

	private List<String> ontologyUrls;
	private String token;
	private String ontologyRDFFormat;

	// Works only for IN-MEMORY Repositories, TODO NATIVE repositories
	public void process(Exchange exchange) throws Exception {

        ValueFactory vf = SimpleValueFactory.getInstance();
		MemoryRDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class);
		Sail data = graph.getData();
		Repository repo = graph.getRepository();

		if (ontologyUrls == null)
			ontologyUrls = exchange.getProperty(ProcessorConstants.ONTOLOGY_URLS, List.class);

		IRI contextIRI = Utils.getContextIRI(exchange);

		try (RepositoryConnection con = repo.getConnection()) {
        	for (String url: ontologyUrls) {
				if (contextIRI != null)
					con.add(SemanticLoader.secure_load_data(url, ontologyRDFFormat, token), contextIRI, vf.createIRI(url));
				else
					con.add(SemanticLoader.secure_load_data(url, ontologyRDFFormat, token), vf.createIRI(url));
        	}
        }

		SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer((NotifyingSail) data, repo, false);
		graph.setData(inferencer);
	}

	public List<String> getOntologyUrls() {
		return ontologyUrls;
	}

	public void setOntologyUrls(List<String> ontologyUrls) {
		this.ontologyUrls = ontologyUrls;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOntologyRDFFormat() {
		return ontologyRDFFormat;
	}

	public void setOntologyRDFFormat(String ontologyRDFFormat) {
		this.ontologyRDFFormat = ontologyRDFFormat;
	}

}