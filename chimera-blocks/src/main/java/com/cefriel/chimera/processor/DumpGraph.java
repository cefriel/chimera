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
package com.cefriel.chimera.processor;
import java.nio.charset.StandardCharsets;

import com.cefriel.chimera.graph.RDFGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefriel.chimera.util.ProcessorConstants;


public class DumpGraph implements Processor {

    private Logger logger = LoggerFactory.getLogger(DumpGraph.class);

    public void process(Exchange exchange) throws Exception {
		Repository repo;
		String output;
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();

		repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		String context = ProcessorConstants.BASE_CONVERSION_IRI
				+ exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
		ValueFactory vf = SimpleValueFactory.getInstance();
		IRI contextIRI = vf.createIRI(context);

		try (RepositoryConnection con = repo.getConnection()) {
			RepositoryResult<Statement> dump = con.getStatements(null, null, null, contextIRI);
			Model dump_model = QueryResults.asModel(dump);

			Rio.write(dump_model, outstream, RDFFormat.TURTLE);
			output = new String(outstream.toByteArray(), StandardCharsets.UTF_8);

			exchange.getOut().setBody(output);
			exchange.getOut().setHeader(Exchange.CONTENT_TYPE, "text/turtle");
		}
    }

}