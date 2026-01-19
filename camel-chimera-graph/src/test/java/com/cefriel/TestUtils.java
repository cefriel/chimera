package com.cefriel;

import com.cefriel.util.Utils;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.io.StringReader;

/**
 * Utility class for RDF testing operations.
 * Provides helper methods for parsing RDF strings and comparing RDF graphs.
 */
public final class TestUtils {

    private static final String DEFAULT_BASE_IRI = "";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private TestUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Parses an RDF string into a Model.
     *
     * @param rdf       the RDF string to parse (must not be null or empty)
     * @param rdfFormat the format of the RDF string (e.g., "turtle", "rdfxml")
     * @param baseIri   the base IRI for resolving relative IRIs (can be null, defaults to empty string)
     * @return the parsed RDF Model
     * @throws IllegalArgumentException if the RDF string is null/empty or format is unsupported
     * @throws IOException             if parsing fails
     */
    public static Model rdfToModel(String rdf, String rdfFormat, String baseIri) throws IOException {
        if (rdf == null || rdf.isEmpty()) {
            throw new IllegalArgumentException("RDF string cannot be null or empty");
        }
        if (rdfFormat == null || rdfFormat.isEmpty()) {
            throw new IllegalArgumentException("RDF format cannot be null or empty");
        }

        RDFFormat format = Utils.getRDFFormat(rdfFormat);
        String effectiveBaseIri = (baseIri != null) ? baseIri : DEFAULT_BASE_IRI;

        RDFParser parser = Rio.createParser(format);
        Model model = new TreeModel();
        parser.setRDFHandler(new StatementCollector(model));

        try (StringReader reader = new StringReader(rdf)) {
            parser.parse(reader, effectiveBaseIri);
        }
        return model;
    }

    /**
     * Parses an RDF string into a Model with empty base IRI.
     *
     * @param rdf       the RDF string to parse
     * @param rdfFormat the format of the RDF string
     * @return the parsed RDF Model
     * @throws IOException if parsing fails
     */
    public static Model rdfToModel(String rdf, String rdfFormat) throws IOException {
        return rdfToModel(rdf, rdfFormat, DEFAULT_BASE_IRI);
    }

    /**
     * Checks if two RDF graphs are isomorphic (structurally equivalent).
     * Two RDF graphs are isomorphic if they contain the same triples,
     * considering blank node equivalence.
     *
     * @param rdfString            the first RDF string
     * @param rdfStringFormat      the format of the first RDF string
     * @param otherRdfString       the second RDF string
     * @param otherRdfStringFormat the format of the second RDF string
     * @return true if the graphs are isomorphic, false otherwise
     * @throws IOException if parsing fails
     */
    public static boolean isIsomorphicGraph(String rdfString, String rdfStringFormat,
                                            String otherRdfString, String otherRdfStringFormat) throws IOException {
        Model firstModel = rdfToModel(rdfString, rdfStringFormat);
        Model secondModel = rdfToModel(otherRdfString, otherRdfStringFormat);
        return Models.isomorphic(firstModel, secondModel);
    }

    /**
     * Checks if two RDF Models are isomorphic (structurally equivalent).
     *
     * @param firstModel  the first RDF Model
     * @param secondModel the second RDF Model
     * @return true if the models are isomorphic, false otherwise
     */
    public static boolean isIsomorphicGraph(Model firstModel, Model secondModel) {
        if (firstModel == null || secondModel == null) {
            return firstModel == secondModel;
        }
        return Models.isomorphic(firstModel, secondModel);
    }
}

