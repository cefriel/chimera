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

    public static boolean isIsomorphicGraph(String firstRdf, String firstRdfFormat, String secondRdf, String secondRdfFormat) {
        return isIsomorphicGraph(firstRdf, firstRdfFormat, null, secondRdf, secondRdfFormat, null);
    }

    public static boolean isIsomorphicGraph(String firstRdf, String firstRdfFormat, String firstBaseIri, String secondRdf, String secondRdfFormat, String secondBaseIri) {
        try {
            Model firstModel = rdfToModel(firstRdf, firstRdfFormat, firstBaseIri);
            Model secondModel = rdfToModel(secondRdf, secondRdfFormat, secondBaseIri);
            return Models.isomorphic(firstModel, secondModel);
        } catch (Exception e) {
            return false;
        }
    }
}

