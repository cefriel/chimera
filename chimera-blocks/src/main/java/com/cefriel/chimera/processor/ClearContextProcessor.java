package com.cefriel.chimera.processor;

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;
import com.cefriel.chimera.util.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class ClearContextProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(ClearContextProcessor.class);

    // TODO Use a List<String>
    private String removeNamespacesPath;

    @Override
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        IRI contextIRI = Utils.getContextIRI(exchange);

        try (RepositoryConnection con = repo.getConnection()) {
            if (contextIRI != null)
                con.clear(contextIRI);

            if (removeNamespacesPath != null) {
                Model l = SemanticLoader.load_data(removeNamespacesPath);
                Set<Namespace> namespaces = l.getNamespaces();
                for(Namespace n : namespaces)
                    con.removeNamespace(n.getPrefix());
            }
        }
        logger.info("Cleared named graph " + contextIRI.stringValue());
    }

    public String getRemoveNamespacesPath() {
        return removeNamespacesPath;
    }

    public void setRemoveNamespacesPath(String removeNamespacesPath) {
        this.removeNamespacesPath = removeNamespacesPath;
    }


}
