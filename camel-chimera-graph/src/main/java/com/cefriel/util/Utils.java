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
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelCollector;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static IRI stringToIRI(String sIRI) {
        return SimpleValueFactory.getInstance().createIRI(sIRI);
    }

    public static String trailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    public static void populateRepository(Repository repo, ChimeraResourceBean resourceBean, Exchange exchange) throws Exception {
        Model model = StreamParser.parseResource(resourceBean, exchange);
        populateRepository(repo, model);
    }
    public static void populateRepository(Repository repo, InputStream inputStream, String format) throws IOException {
        Model model = StreamParser.parseTriples(inputStream, format, null);
        populateRepository(repo, model);
    }
    public static void populateRepository(Repository repo, Model model) {
        RepositoryConnection connection = repo.getConnection();
        // triggers stack overflow error due to a problem for only ContextAwareRepositories
        // add statements from model one by one to repository instead
        // connection.add(model);


        for (Statement st : model.getStatements(null,null,null))
        {
                connection.add(st);
        }

        for (Namespace ns : model.getNamespaces()) {
            connection.setNamespace(ns.getPrefix(), ns.getName());
        }
    }

    public static void populateRepository(Repository targetRepo, Repository sourceRepo) {
        try (RepositoryConnection sourceConn = sourceRepo.getConnection();
             RepositoryConnection targetConn = targetRepo.getConnection()) {

            Model model = sourceConn.getStatements(null, null, null).stream()
                    .collect(ModelCollector.toModel());

            for (Namespace ns : model.getNamespaces()) {
                targetConn.setNamespace(ns.getPrefix(), ns.getName());
            }

            for (Statement st : model.getStatements(null,null,null))
            {
                targetConn.add(st.getSubject(), st.getPredicate(), st.getObject());
            }
        }
    }
    public static Repository createSchemaRepository(ChimeraResourceBean resourcesBean, Exchange exchange) throws Exception {
        Repository schema = new SailRepository(new MemoryStore());
        schema.init();
        populateRepository(schema, resourcesBean, exchange);
        return schema;
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
            // Rio.getParserFormatForMIMEType(format).orElse(RDFFormat.TURTLE)
            default -> throw new IllegalArgumentException("RDF Format: " + format + " is not supported");
        };
    }
    // todo validate rdfFormat everywhere
    public static boolean isSupportedRDFFormat(String rdfFormat) {
        return ChimeraConstants.SUPPORTED_RDF_FORMATS.contains(rdfFormat);
    }

    public static String writeModelToDestination(Model model, String rdfFormat, String basePath, String fileName) throws IOException {
        InputStream inputStream = RDFSerializer.serialize(model, rdfFormat);
        String extension = getRDFFormat(rdfFormat).getDefaultFileExtension();

        Path directory = Paths.get(basePath);

        if (!Files.exists(directory))
            Files.createDirectories(directory);

        Path filePath = directory.resolve(fileName + "." + extension);
        // write to file
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
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
