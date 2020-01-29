/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
