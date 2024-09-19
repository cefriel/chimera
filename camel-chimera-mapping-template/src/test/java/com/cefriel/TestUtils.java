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

    public static boolean isIsomorphicGraph(String rdfString, String rdfStringFormat,
                                             String otherRdfString, String otherRdfStringFormat) throws Exception {
        Model a = parseRDFString(rdfString, rdfStringFormat);
        Model b = parseRDFString(otherRdfString, otherRdfStringFormat);
        return Models.isomorphic(a,b);
    }

    public static Model parseRDFString(String rdfString, String rdfFormat) throws Exception {
        RDFFormat format = Utils.getRDFFormat(rdfFormat);
        RDFParser rdfParser = Rio.createParser(format);

        Model model = new TreeModel();
        rdfParser.setRDFHandler(new StatementCollector(model));
        // rdfParser.parse(new StringReader(rdfString), "http://example.com/base/");
        return model;
    }
}
