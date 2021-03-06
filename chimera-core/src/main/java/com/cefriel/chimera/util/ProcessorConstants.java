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

public class ProcessorConstants {

    public static String BASE_IRI_VALUE = "http://sprint-transport.eu/data/";

    public static String QUERY_CLASS = "http://sprint-transport.eu/terms#Query";
    public static String CONSTRUCT_PROPERTY = "http://sprint-transport.eu/terms#construct";

    public static final String BASE_IRI = "base_iri";

    public static final String GRAPH_ID = "graph_id";
    public static final String CONTEXT_GRAPH = "context_graph";

    public static final String ENRICHMENT_FORMAT = "enrichment_format";
    public static final String QUERIES_ID = "queries_id";

    public static final String DUMP_FORMAT = "dump_format";
    public static final String DUMP_FILENAME = "dump_filename";

    public static final String FILE_EXTENSION = "file_extension";

    public static final String SHACL_RULES = "shacl_rules";
    public static final String ADDITIONAL_SOURCE = "additional_source";

    public static final String JWT_TOKEN = "JWT_TOKEN";

    public static final String CACHE_INVALIDATION = "cache_invalidation";

    public static final String RDF_FORMAT_BINARY = "binary";
    public static final String RDF_FORMAT_JSONLD = "jsonld";
    public static final String RDF_FORMAT_N3 = "n3";
    public static final String RDF_FORMAT_NQUADS = "nquads";
    public static final String RDF_FORMAT_NTRIPLES = "ntriples";
    public static final String RDF_FORMAT_RDFXML = "rdfxml";
    public static final String RDF_FORMAT_TURTLE = "turtle";
    public static final String RDF_FORMAT_RDFA = "rdfa";

    public static final String RML_SERIALIZATION_KEY = "rml";
    public static final String RDF_LOWERER_SERIALIZATION_KEY = "rdf_lowerer";

    public static final String CONVERTER_CONFIGURATION = "converter_configuration";

    public static String getBaseIriValue() {
        return BASE_IRI_VALUE;
    }

    public static void setBaseIriValue(String baseIriValue) {
        BASE_IRI_VALUE = baseIriValue;
    }

}
