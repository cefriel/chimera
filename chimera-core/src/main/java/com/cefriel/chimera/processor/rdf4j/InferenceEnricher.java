/*
 * Copyright (c) 2019-2021 Cefriel.
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
package com.cefriel.chimera.processor.rdf4j;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InferenceEnricher implements Processor {

	private Logger logger = LoggerFactory.getLogger(InferenceEnricher.class);

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

		List<String> urls;
		ConverterConfiguration configuration =
				exchange.getMessage().getHeader(ProcessorConstants.CONVERTER_CONFIGURATION, ConverterConfiguration.class);
		if (configuration != null
				&& configuration.getOntologies() != null
				&& configuration.getOntologies().size() > 0) {
			logger.info("Converter configuration found in the exchange, ontologies extracted");
			ontologyRDFFormat = configuration.getOntologies().get(0).getSerialization();
			urls = configuration.getOntologies().stream()
					.map(ConverterResource::getUrl)
					.collect(Collectors.toList());
		} else {
			urls = new ArrayList<>();
			if (ontologyUrls != null)
				urls.addAll(ontologyUrls);
		}

		if (!urls.isEmpty()) {
			String token = exchange.getProperty(ProcessorConstants.JWT_TOKEN, String.class);

			IRI contextIRI = graph.getContext();
			Repository schema = Utils.getSchemaRepository(urls,
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