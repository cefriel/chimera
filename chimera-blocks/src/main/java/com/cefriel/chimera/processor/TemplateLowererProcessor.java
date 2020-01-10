package com.cefriel.chimera.processor;

import java.io.StringWriter;

import com.cefriel.chimera.graph.MemoryRDFGraph;
import com.cefriel.utils.rdf.RDFReader;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import com.cefriel.chimera.util.ProcessorConstants;

public class TemplateLowererProcessor implements Processor {

	private String templatePath = null;
	private VelocityEngine velocityEngine = null;
    private Logger log = LoggerFactory.getLogger(TemplateLowererProcessor.class); 

	public void process(Exchange exchange) throws Exception {
		Repository repo=null;
		String output=null;
		Message out = exchange.getOut();
		String lowering_template=null;
		//Singleton
		if (velocityEngine==null) {
			velocityEngine = new VelocityEngine();
			velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
			velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			velocityEngine.init();
			   
		}
		repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class).getRepository();

		RDFReader reader = new RDFReader();
		reader.setRepository(repo);

		if (templatePath==null) {
			lowering_template=exchange.getProperty(ProcessorConstants.LOWERING_TEMPLATE, String.class);
		}
		else {
			lowering_template=templatePath;
		}

		log.info("template path: "+templatePath);
		Template t = velocityEngine.getTemplate(lowering_template);		
		VelocityContext context = new VelocityContext();
		context.put("reader", reader);

		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		output=writer.toString();
		
		out.setHeader(Exchange.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
		out.setBody(output);
		
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

}
