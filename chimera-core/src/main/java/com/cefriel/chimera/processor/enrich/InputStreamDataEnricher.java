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

import java.io.InputStream;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class InputStreamDataEnricher implements Processor {

	private String format;
	private String baseIRI = ProcessorConstants.BASE_IRI_VALUE;

	public void process(Exchange exchange) throws Exception {
		InputStream is = exchange.getIn().getBody(InputStream.class);
		RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
		if (graph == null)
			throw new RuntimeException("RDF Graph not attached");
		Repository repo = graph.getRepository();
		String headerBaseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
		if (headerBaseIRI != null)
			baseIRI = headerBaseIRI;

		Model model = new TreeModel();

		RDFFormat rdfFormat = RDFFormat.TURTLE;
		if (format != null)
			rdfFormat = Utils.getRDFFormat(format);
		String headerFormat = exchange.getMessage().getHeader(ProcessorConstants.ENRICHMENT_FORMAT, String.class);
		if (headerFormat != null)
			rdfFormat = Utils.getRDFFormat(headerFormat);
		RDFParser rdfParser = Rio.createParser(rdfFormat);

		rdfParser.setRDFHandler(new StatementCollector(model));
		rdfParser.parse(is, baseIRI);

		IRI contextIRI = graph.getContext();

		try (RepositoryConnection con = repo.getConnection()) {
			if (contextIRI != null)
				con.add(model, contextIRI);
			else
				con.add(model);
		}
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getBaseIRI() {
		return baseIRI;
	}

	public void setBaseIRI(String baseIRI) {
		this.baseIRI = baseIRI;
	}

}