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

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.contextaware.ContextAwareRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Utils {

    public static Repository getContextAwareRepository(Repository repository, IRI context) {
        if (context != null) {
            ContextAwareRepository cRepo = new ContextAwareRepository(repository);
            cRepo.setInsertContext(context);
            cRepo.setReadContexts(context);
            return cRepo;
        }
        return repository;
    }

    public static String trailingSlash(String url) {
        return url.endsWith("/") ? url : url + "/";
    }

    public static RDFFormat getRDFFormat(String format) {
        switch (format.toLowerCase()) {
            case ProcessorConstants.RDF_FORMAT_BINARY:
                return RDFFormat.BINARY;
            case ProcessorConstants.RDF_FORMAT_JSONLD:
                return RDFFormat.JSONLD;
            case ProcessorConstants.RDF_FORMAT_N3:
                return RDFFormat.N3;
            case ProcessorConstants.RDF_FORMAT_NQUADS:
                return RDFFormat.NQUADS;
            case ProcessorConstants.RDF_FORMAT_NTRIPLES:
                return RDFFormat.NTRIPLES;
            case ProcessorConstants.RDF_FORMAT_RDFXML:
                return RDFFormat.RDFXML;
            case ProcessorConstants.RDF_FORMAT_TURTLE:
                return RDFFormat.TURTLE;
            case ProcessorConstants.RDF_FORMAT_RDFA:
                return RDFFormat.RDFA;
            default:
                return null;
        }
    }

    public static Repository getSchemaRepository(List<String> ontologyUrls, String ontologyRDFFormat, String token) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        Repository schema = new SailRepository(new MemoryStore());
        schema.init();
        try (RepositoryConnection con = schema.getConnection()) {
            for (String url: ontologyUrls)
                con.add(SemanticLoader.secure_load_data(url, ontologyRDFFormat, token), vf.createIRI(url));
        }
        return schema;
    }

    public static void addSchemaToRepository(Repository repo, String context, List<String> ontologyUrls, String ontologyRDFFormat, String token) throws IOException {
        ValueFactory vf = SimpleValueFactory.getInstance();
        try (RepositoryConnection con = repo.getConnection()) {
            for (String url: ontologyUrls) {
                if (context != null)
                    con.add(SemanticLoader.secure_load_data(url, ontologyRDFFormat, token), vf.createIRI(context));
                else
                    con.add(SemanticLoader.secure_load_data(url, ontologyRDFFormat, token));
            }
        }
    }
}
