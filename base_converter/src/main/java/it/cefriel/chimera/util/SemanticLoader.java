/*
 * Copyright 2018 Cefriel.
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

package it.cefriel.chimera.util;

import java.io.IOException;
import java.io.InputStream;
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
    public static Model load_data(String url) throws RDFParseException, RDFHandlerException, IOException {
    	Model model = new LinkedHashModel();
        
        RDFParser rdfParser = null;
        ValueFactory vf = SimpleValueFactory.getInstance();

        if (url.startsWith("classpath://")){
        	rdfParser=Rio.createParser(RDFFormat.TURTLE);
        	rdfParser.setRDFHandler(new StatementCollector(model));
        	url=url.replaceAll("classpath://", "");
        	rdfParser.parse(SemanticLoader.class.getClassLoader().getResourceAsStream(url),url);
        	return model;
        }
        
        if (url.startsWith("/")) {
        	url="file://"+url;
        }
        RDFFormat format = Rio.getParserFormatForFileName(url.toString()).orElse(RDFFormat.RDFXML);
        rdfParser=Rio.createParser(format);
        rdfParser.setRDFHandler(new ContextStatementCollector(model, vf, vf.createIRI(url)));
        java.net.URL documentUrl = new URL(url);
        InputStream inputStream = documentUrl.openStream();
        rdfParser.parse(inputStream,url);

        return model;
        
    }


}
