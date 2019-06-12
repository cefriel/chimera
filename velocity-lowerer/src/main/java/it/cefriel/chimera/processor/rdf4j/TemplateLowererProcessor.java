package it.cefriel.chimera.processor.rdf4j;

import java.io.StringWriter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.rdf4j.repository.Repository;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.RDFReader;

public class TemplateLowererProcessor  implements Processor{ 

	private String templatePath;

	public void process(Exchange exchange) throws Exception {
		Repository repo=null;
		Message in = exchange.getIn();
		String lowering_template=null;
		//Singleton
		Velocity.init();
     
		repo=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

		RDFReader reader = new RDFReader();
		reader.setRepository(repo);
		if (templatePath==null) {
			lowering_template=in.getHeader(ProcessorConstants.LOWERING_TEMPLATE, String.class);
    	}
		
		Template t = Velocity.getTemplate(lowering_template);		
		VelocityContext context = new VelocityContext();
		context.put("reader", reader);

		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		in.setBody(writer.toString());
	}

	public String getTemplatePath() {
		return templatePath;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
}
