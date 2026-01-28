package com.cefriel;

import com.cefriel.util.Utils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.StringReader;

public class TestUtils {

    public static Model rdfToModel(String rdf, String rdfFormat, String baseIri) throws Exception {
        if (rdf == null || rdf.isEmpty()) {
            throw new IllegalArgumentException("RDF string cannot be null or empty");
        }
        RDFFormat format = Utils.getRDFFormat(rdfFormat);
        if (format == null) {
            throw new IllegalArgumentException("Unsupported RDF format: " + rdfFormat);
        }
        if (baseIri == null) {
            baseIri = "";
        }
        RDFParser parser = Rio.createParser(format);
        Model model = new TreeModel();
        parser.setRDFHandler(new StatementCollector(model));

        try (StringReader reader = new StringReader(rdf)) {
            parser.parse(reader, baseIri);
        }
        return model;
    }
}

