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

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;
import java.util.List;

public class InferenceEnricher implements Processor {

	private List<String> ontologyUrls;
	private String ontologyRDFFormat;
	private boolean allRules = true;

	// Computes inference on triples in the repository using provided schema,
	// adds resulting triples to the repository.
	public void process(Exchange exchange) throws Exception {
		RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
		if (graph == null)
			throw new RuntimeException("RDF Graph not attached");
		Repository repo = graph.getRepository();

		if (ontologyUrls == null)
			ontologyUrls = exchange.getProperty(ProcessorConstants.ONTOLOGY_URLS, List.class);

		String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

		IRI contextIRI = graph.getContext();
		Repository schema = Utils.getSchemaRepository(ontologyUrls,
				ontologyRDFFormat, token);
		SchemaCachingRDFSInferencer inferencer = new SchemaCachingRDFSInferencer(new MemoryStore(), schema, allRules);
		Repository inferenceRepo = new SailRepository(inferencer);
		inferenceRepo.init();

		RepositoryConnection source = repo.getConnection();
		RepositoryConnection target = inferenceRepo.getConnection();
		//Enable inference
		if (contextIRI != null)
			target.add(source.getStatements(null, null, null, true, contextIRI));
		else
			target.add(source.getStatements(null, null, null, true));
		//Copy back
		if (contextIRI != null)
			source.add(target.getStatements(null, null, null, true), contextIRI);
		else
			source.add(target.getStatements(null, null, null, true));
		source.close();
		target.close();
	}

	public List<String> getOntologyUrls() {
		return ontologyUrls;
	}

	public void setOntologyUrls(List<String> ontologyUrls) {
		this.ontologyUrls = ontologyUrls;
	}

	public String getOntologyRDFFormat() {
		return ontologyRDFFormat;
	}

	public void setOntologyRDFFormat(String ontologyRDFFormat) {
		this.ontologyRDFFormat = ontologyRDFFormat;
	}

	public boolean isAllRules() {
		return allRules;
	}

	public void setAllRules(boolean allRules) {
		this.allRules = allRules;
	}

}