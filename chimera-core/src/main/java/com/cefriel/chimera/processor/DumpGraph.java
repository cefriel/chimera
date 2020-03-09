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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
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
	private String destinationPath;

    public void process(Exchange exchange) throws Exception {
		Repository repo;

		repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		IRI contextIRI = Utils.getContextIRI(exchange);

		String format = exchange.getMessage().getHeader(ProcessorConstants.DUMP_FORMAT, String.class);
		if (format == null)
			format = "turtle";
		RDFFormat rdfFormat = Utils.getRDFFormat(format);


		try (RepositoryConnection con = repo.getConnection()) {
			RepositoryResult<Statement> dump;
			if (contextIRI != null)
				dump = con.getStatements(null, null, null, contextIRI);
			else
				dump = con.getStatements(null, null, null);
			Model dump_model = QueryResults.asModel(dump);

			RepositoryResult<Namespace> namespaces = con.getNamespaces();
			for(Namespace n : Iterations.asList(namespaces))
				dump_model.setNamespace(n);

			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			Rio.write(dump_model, outstream, rdfFormat);

			if (destinationPath == null) {
				String output = new String(outstream.toByteArray(), StandardCharsets.UTF_8);
				exchange.getMessage().setBody(output);
				exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, rdfFormat.getDefaultMIMEType());
				exchange.getMessage().setHeader(ProcessorConstants.FILE_EXTENSION, rdfFormat.getDefaultFileExtension());
				logger.info("Graph dumped to message body");
			} else {
				String context = exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
				String localDestPath = destinationPath;
				if (!(localDestPath.substring(localDestPath.length() - 1)).equals("/"))
					localDestPath += '/';
				new File(localDestPath).mkdirs();
				localDestPath += "graph-dump";
				if (context != null)
					localDestPath = localDestPath + "-" + context;
				localDestPath = localDestPath + "." + rdfFormat.getDefaultFileExtension();
				OutputStream fileOutputStream = new FileOutputStream(new File(localDestPath));
				outstream.writeTo(fileOutputStream);
				logger.info("Graph dumped to file " + localDestPath);
			}
		}
    }

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

}