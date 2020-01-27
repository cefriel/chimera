package com.cefriel.chimera.util;

import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

public class Utils {

    public static String getContext(Exchange exchange) {
        String contextId = exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
        if (contextId != null)
            return ProcessorConstants.BASE_IRI_VALUE + contextId;
        return null;
    }

    public static IRI getContextIRI(Exchange exchange) {
        String context = Utils.getContext(exchange);
        if (context != null) {
            ValueFactory vf = SimpleValueFactory.getInstance();
            IRI contextIRI = vf.createIRI(context);
            return contextIRI;
        }
        return null;
    }

    public static RDFFormat getRDFFormat(String format) {
        switch (format.toLowerCase()) {
            case "binary":
                return RDFFormat.BINARY;
            case "jsonld":
                return RDFFormat.JSONLD;
            case "n3":
                return RDFFormat.N3;
            case "nquads":
                return RDFFormat.NQUADS;
            case "ntriples":
                return RDFFormat.NTRIPLES;
            case "rdfxml":
                return RDFFormat.RDFXML;
            case "turtle":
                return RDFFormat.TURTLE;
            case "rdfa":
                return RDFFormat.RDFA;
            default:
                return null;
        }
    }
}
