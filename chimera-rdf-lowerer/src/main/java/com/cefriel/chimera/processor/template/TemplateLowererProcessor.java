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
import com.cefriel.chimera.util.Utils;
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

import java.io.File;
import java.io.InputStream;
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
	private String destinationPath = "/tmp/";

	public void process(Exchange exchange) throws Exception {
		Repository repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		// Template Lowerer configuration
		TemplateLowererOptions templateLowererOptions = exchange.getIn()
					.getHeader(TemplateProcessorConstants.TEMPLATE_CONFIG, TemplateLowererOptions.class);
		if (templateLowererOptions != null)
			exchange.getIn().removeHeader(TemplateProcessorConstants.TEMPLATE_CONFIG);
		else {
			templateLowererOptions = defaultTLOptions;
			if (templateLowererOptions == null)
				throw new IllegalArgumentException("TemplateLowererOptions config should be provided in the header");
		}

		String context = exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
		String localDestPath = destinationPath;
		if (!(localDestPath.substring(localDestPath.length() - 1)).equals("/"))
			localDestPath += '/';
		if (context != null)
			localDestPath = localDestPath + context + "/";

		LoweringUtils lu = new LoweringUtils();
		if (templateLowererOptions.getUtils() != null)
			switch (templateLowererOptions.getUtils()) {
				case "transmodel":
					lu = new TransmodelLoweringUtils();
					break;
			}

		String baseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
		if (baseIRI == null)
			baseIRI = ProcessorConstants.BASE_IRI_VALUE;
		lu.setPrefix(baseIRI);

		IRI contextIRI = Utils.getContextIRI(exchange);

		RDFReader reader = new RDFReader(repo, contextIRI);
		TemplateLowerer tl = new TemplateLowerer(reader, lu);

		if (templateLowererOptions.getKeyValueCsvPath() != null)
			tl.setKeyValueCsvPath(templateLowererOptions.getKeyValueCsvPath());
		if (templateLowererOptions.getKeyValuePairsPath() != null)
			tl.setKeyValuePairsPath(templateLowererOptions.getKeyValuePairsPath());
		if (templateLowererOptions.getFormat() != null)
			tl.setFormat(templateLowererOptions.getFormat());
		if (templateLowererOptions.isTrimTemplate())
			tl.setTrimTemplate(true);

		new File(localDestPath).mkdirs();

		if (templateLowererOptions.getQueryFile() != null)
			tl.lower(templateLowererOptions.getTemplatePath(),
					localDestPath + templateLowererOptions.getDestFileName(), templateLowererOptions.getQueryFile());
		else
			tl.lower(templateLowererOptions.getTemplatePath(),
					localDestPath + templateLowererOptions.getDestFileName());

		String filename = templateLowererOptions.getDestFileName().replaceFirst("[.][^.]+$", "");

		if (templateLowererOptions.isAttachmentToExchange() && templateLowererOptions.getQueryFile() == null) {
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

}
