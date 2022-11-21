/*
 * Copyright (c) 2019-2022 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cefriel.util;

import org.apache.camel.*;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

@Converter(generateLoader = true)
public final class StreamParser implements TypeConverters{

    private static final Logger LOG = LoggerFactory.getLogger(StreamParser.class);

    // todo refactor param list, should not accept the whole exchange but just the data from the exchange that it needs
    public static Model parse(InputStream inputStream, String rdfFormat, Exchange exchange) throws IOException {
        Model model = new TreeModel();
        String headerBaseIRI;

        if (inputStream == null)
            return null;
        else {
            RDFFormat rf = Utils.getRDFFormat(rdfFormat);
            // rdfFormat = Utils.getExchangeRdfFormat(exchange, Exchange.CONTENT_TYPE);
            LOG.info("RDF Format: " + rf);

            RDFParser parser = Rio.createParser(rf);
            parser.setRDFHandler(new StatementCollector(model));
            headerBaseIRI = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
            if(headerBaseIRI == null){
                headerBaseIRI = ChimeraConstants.DEFAULT_BASE_IRI;
            }
            parser.parse(inputStream, headerBaseIRI);
            LOG.info("Model created");
            return model;
        }
    }

    @Converter
    public static Model parse(InputStream inputStream, Exchange exchange) throws IOException {

        Model model = new TreeModel();
        String headerBaseIRI;
        RDFFormat rdfFormat;

        if (inputStream == null)
            return null;
        else {
            rdfFormat = Utils.getExchangeRdfFormat(exchange, Exchange.CONTENT_TYPE);
            LOG.info("RDF Format: " + rdfFormat);

            RDFParser parser = Rio.createParser(rdfFormat);
            parser.setRDFHandler(new StatementCollector(model));
            headerBaseIRI = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
            if(headerBaseIRI == null){
                headerBaseIRI = ChimeraConstants.DEFAULT_BASE_IRI;
            }
            parser.parse(inputStream, headerBaseIRI);
            LOG.info("Model created");
            return model;
        }
    }
}
