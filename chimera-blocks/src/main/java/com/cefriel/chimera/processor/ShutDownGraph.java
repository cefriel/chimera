package com.cefriel.chimera.processor;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShutDownGraph implements Processor {
    private Logger logger = LoggerFactory.getLogger(StopProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        if (repo != null)
            repo.shutDown();
    }
}
