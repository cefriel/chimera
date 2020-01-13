package com.cefriel.chimera.processor;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearContextProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(ClearContextProcessor.class);

    private boolean removeNamespaces;

    @Override
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        IRI contextIRI = Utils.getContextIRI(exchange);

        try (RepositoryConnection con = repo.getConnection()) {
            if (contextIRI != null)
                con.clear(contextIRI);
            if (removeNamespaces) {
                RepositoryResult<Namespace> namespaces = con.getNamespaces();
                while (namespaces.hasNext()) {
                    Namespace ns = namespaces.next();
                    con.removeNamespace(ns.getName());
                }
            }
        }
        logger.info("Cleared named graph " + contextIRI.stringValue());
    }

    public boolean isRemoveNamespaces() {
        return removeNamespaces;
    }

    public void setRemoveNamespaces(boolean removeNamespaces) {
        this.removeNamespaces = removeNamespaces;
    }
}
