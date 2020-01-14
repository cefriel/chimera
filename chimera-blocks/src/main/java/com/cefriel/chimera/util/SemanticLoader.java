/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class SemanticLoader {

    private static String baseIRI = ProcessorConstants.BASE_CONVERSION_IRI;

    public static Model load_data(String url) throws RDFParseException, RDFHandlerException, IOException {
    	return secure_load_data(url, null, null);
    }

    public static Model load_data(String url, String format) throws RDFParseException, RDFHandlerException, IOException {
        return secure_load_data(url, format, null);
    }

    public static Model secure_load_data(String url, String token) throws RDFParseException, RDFHandlerException, IOException {
        return secure_load_data(url, null, token);
    }
    
    public static Model secure_load_data(String url, String format, String token) throws RDFParseException, RDFHandlerException, IOException {
    	Model model = new LinkedHashModel();

        RDFFormat rdfFormat = Rio.getParserFormatForFileName(url).orElse(RDFFormat.RDFXML);
    	if (format != null)
    	    rdfFormat = Utils.getRDFFormat(format);
        RDFParser rdfParser = Rio.createParser(rdfFormat);
        rdfParser.setRDFHandler(new StatementCollector(model));

        InputStream inputStream = UniLoader.open(url, token);
        rdfParser.parse(inputStream, baseIRI);
        return model;
    }

    public static String getBaseIRI() {
        return baseIRI;
    }

    public static void setBaseIRI(String baseIRI) {
        SemanticLoader.baseIRI = baseIRI;
    }

}
