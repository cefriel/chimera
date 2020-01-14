package com.cefriel.chimera.processor.template;

import com.cefriel.chimera.graph.RDFGraph;
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
import org.springframework.http.MediaType;

import com.cefriel.chimera.util.ProcessorConstants;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TemplateLowererProcessor implements Processor {

	private Logger logger = LoggerFactory.getLogger(TemplateLowererProcessor.class);

	private TemplateLowererOptions templateLowererOptions;
	private String destinationPath = "/tmp/";

	public void process(Exchange exchange) throws Exception {
		Repository repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		if (templateLowererOptions == null)
			templateLowererOptions = exchange.getProperty(ProcessorConstants.TEMPLATE_CONFIG, TemplateLowererOptions.class);

		IRI context =  Utils.getContextIRI(exchange);

		if (!(destinationPath.substring(destinationPath.length() - 1)).equals("/"))
			destinationPath += '/';
		if (context != null)
			destinationPath = destinationPath + context.stringValue() + "/";

		LoweringUtils lu = new LoweringUtils();
		if (templateLowererOptions.getUtils() != null)
			switch (templateLowererOptions.getUtils()) {
				case "transmodel":
					lu = new TransmodelLoweringUtils();
					break;
			}

		RDFReader reader = new RDFReader(repo, context);
		TemplateLowerer tl = new TemplateLowerer(reader, lu);

		if (templateLowererOptions.getKeyValueCsvPath() != null)
			tl.setKeyValueCsvPath(templateLowererOptions.getKeyValueCsvPath());
		if (templateLowererOptions.getKeyValuePairsPath() != null)
			tl.setKeyValuePairsPath(templateLowererOptions.getKeyValuePairsPath());
		if (templateLowererOptions.getFormat() != null)
			tl.setFormat(templateLowererOptions.getFormat());

		if (templateLowererOptions.getQueryFile() != null)
			tl.lower(templateLowererOptions.getTemplatePath(),
					destinationPath + templateLowererOptions.getDestFileName(), templateLowererOptions.getQueryFile());
		else
			tl.lower(templateLowererOptions.getTemplatePath(),
					destinationPath + templateLowererOptions.getDestFileName());

		reader.shutDown();

		if (templateLowererOptions.isAttachmentToExchange() && templateLowererOptions.getQueryFile() == null) {
			List<String> result;
			try (Stream<Path> walk = Files.walk(Paths.get(destinationPath))) {
				result = walk.map(x -> x.getFileName().toString())
						.filter(f -> f.contains(templateLowererOptions.getDestFileName()))
						.collect(Collectors.toList());
			}
			Map<String, InputStream> outputs = new HashMap<>();
			for (String f : result)
				outputs.put(f, UniLoader.open(destinationPath + f));
			exchange.getOut().setBody(outputs, Map.class);
		}
	}

	public TemplateLowererOptions getTemplateLowererOptions() {
		return templateLowererOptions;
	}

	public void setTemplateLowererOptions(TemplateLowererOptions templateLowererOptions) {
		this.templateLowererOptions = templateLowererOptions;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}


}
