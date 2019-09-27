package it.cefriel.chimera.processor.rdf4j;

import java.io.StringWriter;

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
import nu.xom.ParsingException;
import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.RDFReader;

public class TemplateLowererProcessor  implements Processor{ 
	private String templatePath=null;
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
		repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

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
		try {
			output=format(output);
		} catch (Exception e) {
			log.debug("Parsing Exception: XML cannot be correctly serialized.")
		}
		
		out.setHeader(Exchange.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
		out.setBody(output);
		
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String format(String xml) throws ParsingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serializer serializer = new Serializer(out);
        serializer.setIndent(4);
        serializer.write(new Builder().build(xml, ""));
        return out.toString("UTF-8");
    }
}
