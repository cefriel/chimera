package it.cefriel.chimera.processor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;


public class AttachGraph implements Processor {

	private RDFGraph graph=null;
	
    public void process(Exchange exchange) throws Exception {
    	Message msg=exchange.getIn();
        msg.setHeader(ProcessorConstants.CONTEXT_GRAPH, graph);

    }

	public RDFGraph getGraph() {
		return graph;
	}

	public void setGraph(RDFGraph graph) {
		this.graph = graph;
	}
    
    

}