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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChimeraConstants {
    public static final String DEFAULT_BASE_IRI = "http://www.cefriel.com/data/";
    public static final String BASE_IRI = "base_iri";
    public static final String GRAPH_ID = "graph_id";
    public static final String CONTEXT_GRAPH = "context_graph";
    public static final String GRAPH = "graph";

    public static final String DUMP_FORMAT = "dump_format";
    public static final String FILENAME = "filename";
    public static final String FILE_EXTENSION = "file_extension";
    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String ACCEPTFORMAT = "AcceptFormat";
    public static final String RDF_FORMAT = "rdfFormat";
    public static final String RDF_FORMAT_BINARY = "binary";
    // TODO IS JSONLD FORMAT STILL SUPPORTED ?
    public static final String RDF_FORMAT_JSONLD = "jsonld";
    public static final String RDF_FORMAT_N3 = "n3";
    public static final String RDF_FORMAT_NQUADS = "nquads";
    public static final String RDF_FORMAT_NTRIPLES = "ntriples";
    public static final String RDF_FORMAT_RDFXML = "rdfxml";
    public static final String RDF_FORMAT_TURTLE = "turtle";
    public static final String RDF_FORMAT_RDFA = "rdfa";
    public static final Set<String> SUPPORTED_RDF_FORMATS = new HashSet<>(Arrays.asList(
            RDF_FORMAT_BINARY, RDF_FORMAT_JSONLD, RDF_FORMAT_N3,
            RDF_FORMAT_NQUADS, RDF_FORMAT_NTRIPLES, RDF_FORMAT_RDFXML,
            RDF_FORMAT_TURTLE, RDF_FORMAT_RDFA));

    public static final String CONFIGURATION = "configuration";
    public static final String BASE_CONFIGURATION = "baseConfiguration";

}