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

    public static String DEFAULT_REPOSITORY_ID = "chimera_repo";
    public static String DEFAULT_REPO_CONFIG_FILE = "file://./repo-default-config.ttl";

    public static final String BASE_IRI = "base_iri";
    public static final String PREFIX_BASE_IRI = "prefix_base_iri";

    public static final String CONTEXT_ID = "context_id";
    public static final String CONTEXT_GRAPH = "context_graph";

    public static final String SOURCE_STD = "source_standard";
    public static final String DEST_STD = "destination_standard";

    public static final String ENRICHMENT_FORMAT = "enrichment_format";
    public static final String DUMP_FORMAT = "dump_format";

    public static final String FILE_EXTENSION = "file_extension";

    // RML
    public static final String RML_CONFIG = "rml_config";
	public static final String RML_LABEL = "rml_label";

    public static final String SHACL_RULES = "shacl_rules";
    public static final String ADDITIONAL_SOURCES = "additional_sources";
    public static final String ONTOLOGY_URLS = "ontologies";

    // TEMPLATE
    public static final String TEMPLATE_CONFIG = "template_config";

    public static final String JWT_TOKEN = "JWT_TOKEN";

    public static String getBaseIriValue() {
        return BASE_IRI_VALUE;
    }

    public static void setBaseIriValue(String baseIriValue) {
        BASE_IRI_VALUE = baseIriValue;
    }

    public static String getDefaultRepositoryId() {
        return DEFAULT_REPOSITORY_ID;
    }

    public static void setDefaultRepositoryId(String defaultRepositoryId) {
        DEFAULT_REPOSITORY_ID = defaultRepositoryId;
    }

    public static String getDefaultRepoConfigFile() {
        return DEFAULT_REPO_CONFIG_FILE;
    }

    public static void setDefaultRepoConfigFile(String defaultRepoConfigFile) {
        DEFAULT_REPO_CONFIG_FILE = defaultRepoConfigFile;
    }

}
