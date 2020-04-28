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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.NotifyingSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.eclipse.rdf4j.sail.shacl.ShaclSailValidationException;

import com.cefriel.chimera.graph.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;

public class ShaclValidationProcessor implements Processor {

	private List<String> shaclRulesUrls;

	// Works only for IN-MEMORY Repositories
	public void process(Exchange exchange) throws Exception {
		Model current_ruleset;
		ValueFactory vf = SimpleValueFactory.getInstance();

		MemoryRDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class);
		if (graph == null)
			throw new RuntimeException("RDF Graph not attached");
		NotifyingSail data = (NotifyingSail) graph.getData();
		ShaclSail shaclSail = new ShaclSail(data);
		shaclSail.setIgnoreNoShapesLoadedException(true);
		SailRepository sailRepository = new SailRepository(shaclSail);
		sailRepository.init();

		if (shaclRulesUrls == null)
			shaclRulesUrls = exchange.getProperty(ProcessorConstants.SHACL_RULES, List.class);

		try (SailRepositoryConnection connection = sailRepository.getConnection()) {
			try {
				connection.begin();
				for (String url : shaclRulesUrls) {
					current_ruleset = SemanticLoader.load_data(url);
					connection.add(current_ruleset, vf.createIRI(url));
				}
				connection.commit();
			} catch (RepositoryException exception) {
				Throwable cause = exception.getCause();
				if (cause instanceof ShaclSailValidationException) {
					// Use validationReport or validationReportModel to understand validation violations
					Model validationReportModel = ((ShaclSailValidationException) cause).validationReportAsModel();
					// Add report to the message header
					ByteArrayOutputStream outstream = new ByteArrayOutputStream();
					Rio.write(validationReportModel, outstream, RDFFormat.TURTLE);
					String output = new String(outstream.toByteArray(), StandardCharsets.UTF_8);
					exchange.getMessage().setHeader("validation-shacl-report", output);
				}
				throw exception;
			}
		}
	}

	public List<String> getShaclRulesUrls() {
		return shaclRulesUrls;
	}

	public void setShaclRulesUrls(List<String> shaclRulesUrls) {
		this.shaclRulesUrls = shaclRulesUrls;
	}
}