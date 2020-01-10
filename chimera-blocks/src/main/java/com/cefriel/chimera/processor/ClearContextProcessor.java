package com.cefriel.chimera.processor;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearContextProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(ClearContextProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        String context = ProcessorConstants.BASE_CONVERSION_IRI
                + exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
        ValueFactory vf = SimpleValueFactory.getInstance();
        IRI contextIRI = vf.createIRI(context);

        try (RepositoryConnection con = repo.getConnection()) {
            con.clear(contextIRI);
        }
        logger.info("Cleared named graph " + context);
    }
}
