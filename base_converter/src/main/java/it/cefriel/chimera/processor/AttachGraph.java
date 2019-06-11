package it.cefriel.chimera.processor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;


public class AttachGraph implements Processor {

	
    public void process(Exchange exchange) throws Exception {
    	RDFGraph graph=new RDFGraph();

    	Message msg=exchange.getIn();
        msg.setHeader(ProcessorConstants.CONTEXT_GRAPH, graph);

    }

}