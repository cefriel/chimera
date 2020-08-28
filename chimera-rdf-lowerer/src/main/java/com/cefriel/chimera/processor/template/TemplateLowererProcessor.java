/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.processor.template;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.UniLoader;
import com.cefriel.lowerer.TemplateLowerer;
import com.cefriel.utils.LoweringUtils;
import com.cefriel.utils.TransmodelLoweringUtils;
import com.cefriel.utils.rdf.RDFReader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefriel.chimera.util.TemplateProcessorConstants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateLowererProcessor implements Processor {

	private Logger logger = LoggerFactory.getLogger(TemplateLowererProcessor.class);

	private TemplateLowererOptions defaultTLOptions;
	private String destinationPath = "./tmp/";
	private boolean stream;

	public void process(Exchange exchange) throws Exception {
		RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
		if (graph == null)
			throw new RuntimeException("RDF Graph not attached");
		Repository repo = graph.getRepository();

		// Template Lowerer configuration
		TemplateLowererOptions tlo = exchange.getIn()
					.getHeader(TemplateProcessorConstants.TEMPLATE_CONFIG, TemplateLowererOptions.class);
		if (tlo != null)
			exchange.getIn().removeHeader(TemplateProcessorConstants.TEMPLATE_CONFIG);
		else {
			tlo = defaultTLOptions;
			if (tlo == null)
				throw new IllegalArgumentException("TemplateLowererOptions config should be provided in the header");
		}

		LoweringUtils lu = new LoweringUtils();
		if (tlo.getUtils() != null)
			switch (tlo.getUtils()) {
				case "transmodel":
					lu = new TransmodelLoweringUtils();
					break;
			}

		String graphID = exchange.getProperty(ProcessorConstants.GRAPH_ID, String.class);
		String baseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
		if (baseIRI == null)
			baseIRI = ProcessorConstants.BASE_IRI_VALUE;
		lu.setPrefix(baseIRI);

		IRI contextIRI = graph.getContext();
		RDFReader reader = new RDFReader(repo, contextIRI);
		TemplateLowerer tl = new TemplateLowerer(reader, lu);

		if (tlo.getKeyValueCsvPath() != null)
			tl.setKeyValueCsvPath(tlo.getKeyValueCsvPath());
		if (tlo.getKeyValuePairsPath() != null)
			tl.setKeyValuePairsPath(tlo.getKeyValuePairsPath());
		if (tlo.getFormat() != null)
			tl.setFormat(tlo.getFormat());
		if (tlo.isTrimTemplate())
			tl.setTrimTemplate(true);

		String templatePath = exchange.getIn().getHeader(TemplateProcessorConstants.TEMPLATE_PATH, String.class);
		if (templatePath == null)
			templatePath = tlo.getTemplatePath();

		String destFileName = exchange.getIn().getHeader(TemplateProcessorConstants.DEST_FILE_NAME, String.class);
		if (destFileName == null)
			destFileName = tlo.getDestFileName();

		String localDestPath = destinationPath;
		if (!(localDestPath.substring(localDestPath.length() - 1)).equals("/"))
			localDestPath += '/';
		if (graphID != null)
			localDestPath = localDestPath + graphID + "/";

		new File(localDestPath).mkdirs();

		String filepath = localDestPath + destFileName;

		if (stream) {
			logger.info("Template processed as a stream");
			InputStream template = exchange.getProperty(TemplateProcessorConstants.TEMPLATE_STREAM, InputStream.class);
			if (tlo.getQueryFile() != null)
				logger.warn("Parametric templates not supported for streams");
			if (template != null) {
				String result = tl.lower(template);
				if (tlo.isAttachmentToExchange())
					exchange.getMessage().setBody(result, String.class);
				else
					try (PrintWriter out = new PrintWriter(filepath)) {
						out.println(result);
					}
			}
		} else {
			tl.lower(templatePath, filepath, tlo.getQueryFile());

			if (tlo.isAttachmentToExchange()) {
				if (tlo.getQueryFile() != null) {
					//Attach all file created with parametric template
					String filename = destFileName.replaceFirst("[.][^.]+$", "");
					List<String> result;
					try (Stream<Path> walk = Files.walk(Paths.get(localDestPath))) {
						result = walk.map(x -> x.getFileName().toString())
								.filter(f -> f.contains(filename))
								.collect(Collectors.toList());
					}
					Map<String, InputStream> outputs = new HashMap<>();
					for (String f : result)
						outputs.put(f, UniLoader.open("file://" + localDestPath + f));
					exchange.getMessage().setBody(outputs, Map.class);
				} else {
					//Attach the result as InputStream
					exchange.getMessage().setBody(UniLoader.open("file://" + filepath), InputStream.class);
				}
			}
		}
	}

	public TemplateLowererOptions getDefaultTLOptions() {
		return defaultTLOptions;
	}

	public void setDefaultTLOptions(TemplateLowererOptions defaultTLOptions) {
		this.defaultTLOptions = defaultTLOptions;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		this.stream = stream;
	}

}
