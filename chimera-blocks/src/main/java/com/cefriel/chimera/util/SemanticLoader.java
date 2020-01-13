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
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.ContextStatementCollector;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

public class SemanticLoader {

    private static ValueFactory vf = SimpleValueFactory.getInstance();

    public static Model load_data(String url) throws RDFParseException, RDFHandlerException, IOException {
    	return load_data(url, null);
    }
    
    public static Model load_data(String url, String token) throws RDFParseException, RDFHandlerException, IOException {
    	Model model = new LinkedHashModel();
        if (checkClassPath(url,model))
            return model;
        
        if (url.startsWith("/"))
        	url = "file://" + url;
        RDFFormat format = Rio.getParserFormatForFileName(url).orElse(RDFFormat.RDFXML);
        RDFParser rdfParser = Rio.createParser(format);
        rdfParser.setRDFHandler(new ContextStatementCollector(model, vf, vf.createIRI(url)));
        java.net.URL documentUrl = new URL(url);

        if (token == null) {
            InputStream inputStream = documentUrl.openStream();
            rdfParser.parse(inputStream,url);
            return model;
        }

        HttpURLConnection con = (HttpURLConnection) documentUrl.openConnection();

        // Set up URL connection to get retrieve information back
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Accept", "application/x-turtle, application/rdf+xml");

        // Pull the information back from the URL
        InputStream inputStream = con.getInputStream();
        rdfParser.parse(inputStream, url);

        return model;
    }

    private static boolean checkClassPath(String url, Model model) throws IOException {
        if (url.startsWith("classpath://")){
            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
            rdfParser.setRDFHandler(new StatementCollector(model));
            url = url.replaceAll("classpath://", "");
            rdfParser.parse(SemanticLoader.class.getClassLoader().getResourceAsStream(url), url);
            return true;
        }
        return false;
    }
}
