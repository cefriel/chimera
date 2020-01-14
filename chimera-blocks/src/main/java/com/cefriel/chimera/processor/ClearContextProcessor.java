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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class ClearContextProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(ClearContextProcessor.class);

    private List<String> removeNamespacesPaths;

    @Override
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        IRI contextIRI = Utils.getContextIRI(exchange);

        try (RepositoryConnection con = repo.getConnection()) {
            if (contextIRI != null)
                con.clear(contextIRI);
            logger.info("Cleared named graph " + contextIRI.stringValue());
            if (removeNamespacesPaths != null)
                for(String path : removeNamespacesPaths)  {
                    Model l = SemanticLoader.load_data(path);
                    Set<Namespace> namespaces = l.getNamespaces();
                    for(Namespace n : namespaces)
                        con.removeNamespace(n.getPrefix());
                    logger.info("Removed namespaces listed in file " + path);
                }
        }
    }

    public List<String> getRemoveNamespacesPaths() {
        return removeNamespacesPaths;
    }

    public void setRemoveNamespacesPaths(List<String> removeNamespacesPaths) {
        this.removeNamespacesPaths = removeNamespacesPaths;
    }


}
