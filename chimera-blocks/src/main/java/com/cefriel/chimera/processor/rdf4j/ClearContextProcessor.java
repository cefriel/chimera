package com.cefriel.chimera.processor.rdf4j;

import com.cefriel.chimera.context.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class ClearContextProcessor implements Processor {

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
    }
}
