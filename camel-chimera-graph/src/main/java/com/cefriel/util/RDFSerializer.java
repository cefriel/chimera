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

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverters;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.helpers.TurtleWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public final class RDFSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(RDFSerializer.class);
    public static InputStream serialize(Model model, String rdfFormat) {
        RDFFormat format = Utils.getRDFFormat(rdfFormat);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        RDFWriter writer = Rio.createWriter(format, outstream);
        writer.getWriterConfig()
                .set(BasicWriterSettings.INLINE_BLANK_NODES, true);
        writer.getWriterConfig()
                .set(TurtleWriterSettings.ABBREVIATE_NUMBERS, false);
        Rio.write(model, writer);
        LOG.info("RDF serialisation succeeded [RDFFormat: " + rdfFormat + "]");
        return new ByteArrayInputStream(outstream.toByteArray());
    }

    public static Exchange serialize(Model model, String rdfFormat, Exchange e) {
        InputStream inputStream = RDFSerializer.serialize(model, rdfFormat);
        RDFFormat format = Utils.getRDFFormat(rdfFormat);

        e.getMessage().setBody(inputStream);
        e.getMessage().setHeader(Exchange.CONTENT_TYPE, format.getDefaultMIMEType());
        e.getMessage().setHeader(ChimeraConstants.FILE_EXTENSION, format.getDefaultFileExtension());
        return e;
    }

    public static InputStream serialize(Model model, Exchange exchange) throws IOException {
        RDFFormat rdfFormat = Utils.getExchangeRdfFormat(exchange, ChimeraConstants.ACCEPTFORMAT);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        RDFWriter writer = Rio.createWriter(rdfFormat, outstream);
        writer.getWriterConfig()
                .set(BasicWriterSettings.INLINE_BLANK_NODES, true);
        writer.getWriterConfig()
                .set(TurtleWriterSettings.ABBREVIATE_NUMBERS, false);
        Rio.write(model, writer);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, rdfFormat.getDefaultMIMEType());
        exchange.getMessage().setHeader(ChimeraConstants.FILE_EXTENSION, rdfFormat.getDefaultFileExtension());
        LOG.info("RDF serialisation succeeded [RDFFormat: " + rdfFormat + "]");

        return new ByteArrayInputStream(outstream.toByteArray());
    }
}
