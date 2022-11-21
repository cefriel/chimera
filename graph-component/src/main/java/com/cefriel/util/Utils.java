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

import com.cefriel.component.GraphBean;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.contextaware.ContextAwareRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static Repository getContextAwareRepository(Repository repository, IRI context) {
        if (context != null) {
            ContextAwareRepository cRepo = new ContextAwareRepository(repository);
            cRepo.setInsertContext(context);
            cRepo.setReadContexts(context);
            return cRepo;
        }
        return repository;
    }
    public static IRI stringToIRI(String sIRI) {
        return SimpleValueFactory.getInstance().createIRI(sIRI);
    }

    public static String trailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    public static Repository getSchemaRepository(GraphBean configuration, Exchange exchange) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        Repository schema = new SailRepository(new MemoryStore());
        schema.init();
        try (RepositoryConnection con = schema.getConnection()) {
            for (String url: configuration.getResources())
                con.add(StreamParser.parse(UniLoader.open(url,
                        exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class)), exchange), vf.createIRI(url));
        }
        return schema;
    }
    public static Repository getSchemaRepository(List<String> ontologyUrls, String jwtToken, String rdfFormat, Exchange exchange) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        Repository schema = new SailRepository(new MemoryStore());
        schema.init();
        try (RepositoryConnection con = schema.getConnection()) {
            for (String url: ontologyUrls)
                con.add(StreamParser.parse(UniLoader.open(url, jwtToken), rdfFormat, exchange), vf.createIRI(url));
        }
        return schema;
    }

    public static void addSchemaToRepository(Repository repo, GraphBean configuration, Exchange exchange) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        try (RepositoryConnection con = repo.getConnection()) {
            for (String url: configuration.getResources()) {
                if (configuration.getNamedGraph() != null)
                    con.add(StreamParser.parse(UniLoader.open(url,
                            exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class)), exchange) , vf.createIRI(configuration.getNamedGraph()));
                else
                    con.add(StreamParser.parse(UniLoader.open(url, exchange.getMessage().getHeader(ChimeraConstants.JWT_TOKEN, String.class)), exchange));
            }
        }
    }

    // todo check if everywhere this logic is superseded by header and param merge handling and validating (probably)
    public static void setConfigurationRDFHeader(Exchange exchange, String format){
        if(exchange.getMessage().getHeader(ChimeraConstants.RDF_FORMAT) == null){
            exchange.getMessage().setHeader(ChimeraConstants.RDF_FORMAT, format);
        } else
            logger.info("There is a RDFFormat in header");
    }

    public static RDFFormat getExchangeRdfFormat(Exchange exchange, String headerName) {
        RDFFormat rdfFormat;
        if (exchange.getMessage().getHeader(headerName, String.class) != null) {
            String headerRdfFormat = exchange.getMessage().getHeader(headerName, String.class);
            rdfFormat = Rio.getParserFormatForMIMEType(headerRdfFormat).orElse(RDFFormat.TURTLE);
            exchange.getMessage().removeHeader(headerName);
        } else {
            String format = exchange.getMessage().getHeader(ChimeraConstants.RDF_FORMAT, String.class);
            if (format != null)
                rdfFormat = Utils.getRDFFormat(format);
            else
                rdfFormat = RDFFormat.TURTLE;
        }
        return rdfFormat;
    }

    public static RDFFormat getRDFFormat(String format) {
        return switch (format.toLowerCase()) {
            case ChimeraConstants.RDF_FORMAT_BINARY -> RDFFormat.BINARY;
            case ChimeraConstants.RDF_FORMAT_JSONLD -> RDFFormat.JSONLD;
            case ChimeraConstants.RDF_FORMAT_N3 -> RDFFormat.N3;
            case ChimeraConstants.RDF_FORMAT_NQUADS -> RDFFormat.NQUADS;
            case ChimeraConstants.RDF_FORMAT_NTRIPLES -> RDFFormat.NTRIPLES;
            case ChimeraConstants.RDF_FORMAT_RDFXML -> RDFFormat.RDFXML;
            case ChimeraConstants.RDF_FORMAT_TURTLE -> RDFFormat.TURTLE;
            case ChimeraConstants.RDF_FORMAT_RDFA -> RDFFormat.RDFA;
            default -> null;
        };
    }

    public static String writeModelToDestination(Exchange exchange, Model model, String defaultName) throws IOException {
        GraphBean configuration = exchange.getMessage().getHeader(ChimeraConstants.CONFIGURATION, GraphBean.class);
        InputStream inputStream = RDFSerializer.serialize(model, exchange);

        String graphID = exchange.getMessage().getHeader(ChimeraConstants.GRAPH_ID, String.class);
        String localDestPath = configuration.getBasePath();
        String fn = exchange.getMessage().getHeader(ChimeraConstants.FILENAME, String.class);
        if (fn == null)
            fn = configuration.getFilename();
        if (!localDestPath.endsWith("/"))
            localDestPath += '/';
        new File(localDestPath).mkdirs();
        if (fn != null) {
            localDestPath += fn;
        } else {
            localDestPath += defaultName;
            if (graphID != null)
                localDestPath = localDestPath + "-" + graphID;
            RDFFormat rdfFormat = Utils.getExchangeRdfFormat(exchange, ChimeraConstants.ACCEPTFORMAT);
            localDestPath = localDestPath + "." + rdfFormat.getDefaultFileExtension();
        }
        File file = new File(localDestPath);
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return localDestPath;
    }
}
