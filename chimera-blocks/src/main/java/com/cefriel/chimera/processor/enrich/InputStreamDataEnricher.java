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
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class InputStreamDataEnricher implements Processor {

	private String format = "turtle";
	private String baseIRI = ProcessorConstants.BASE_CONVERSION_IRI;

	public void process(Exchange exchange) throws Exception {
		Repository repo;
        RDFParser rdfParser;
    	Model model = new LinkedHashModel();

		Message in = exchange.getIn();
		InputStream input_msg = in.getBody(InputStream.class);
		repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		try (RepositoryConnection con = repo.getConnection()) {
			RDFFormat rdfFormat = Utils.getRDFFormat(format);
        	rdfParser = Rio.createParser(rdfFormat);
        	rdfParser.setRDFHandler(new StatementCollector(model));
        	rdfParser.parse(input_msg, baseIRI);

			IRI contextIRI = Utils.getContextIRI(exchange);
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