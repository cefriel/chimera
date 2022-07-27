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

package com.cefriel.component;

import com.apicatalog.jsonld.JsonLdOptions;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Graph component
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "graph", title = "graph", syntax="graph:name",
             category = {Category.JAVA})
public class GraphEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true)
    private String name;
    @UriParam(defaultValue = "null")
    private String basePath;
    @UriParam(defaultValue = "null")
    private String resources;
    @UriParam(defaultValue = "null")
    private String rdfFormat;
    @UriParam(defaultValue = "null")
    private String serverUrl;
    @UriParam(defaultValue = "null")
    private String repositoryID;
    @UriParam(defaultValue = "null")
    private String sparqlEndpoint;
    @UriParam(defaultValue = "null")
    private String pathDataDir;
    @UriParam(defaultValue = "true")
    private boolean allRules;
    @UriParam(defaultValue = "null")
    private String ontologyFormat;
    @UriParam(defaultValue = "null")
    private String namedGraph;
    @UriParam(defaultValue = "http://www.cefriel.com/data/")
    private String baseIri;
    @UriParam(defaultValue = "true")
    private boolean defaultGraph = true;
    @UriParam(defaultValue = "null")
    private String query;
    @UriParam(defaultValue = "null")
    private boolean newGraph;
    @UriParam(defaultValue = "null")
    private boolean clear;
    @UriParam(defaultValue = "true")
    private boolean repoOff = true;
    @UriParam(defaultValue = "null")
    private boolean routeOff;
    @UriParam(defaultValue = "null")
    private String dumpFormat;
    @UriParam(defaultValue = "null")
    private String filename;
    @UriParam(defaultValue = "null")
    private GraphBean baseConfig;
    @UriParam(defaultValue = "null")
    private JsonLdOptions jsonLdOptions;

    public GraphEndpoint() {
    }

    public GraphEndpoint(String uri, String remaining, GraphComponent component) {
        super(uri, component);
        setName(remaining);
    }

    public Producer createProducer() throws Exception {
        return new GraphProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = new GraphConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    /**
     * This parameter contains the name of the endpoint
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * This parameter contains the path of the triples you would like to add to your graph. It can be a file, a classpath or an url
     */
    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    /**
     * This parameter contains the rdfFormat you want to parse a file or an InputStream
     */
    public String getRdfFormat() {
        return rdfFormat;
    }

    public void setRdfFormat(String rdfFormat) {
        this.rdfFormat = rdfFormat;
    }

    /**
     * Parameter used to create a HTTPRDFGraph
     */
    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Parameter used to create a HTTPRDFGraph
     */
    public String getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

    /**
     * Parameter used to create a SPARQLEndpointGraph
     */
    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    /**
     * Parameter used to create a Native RDF Graph or an Inference RDF Graph
     */
    public String getPathDataDir() {
        return pathDataDir;
    }

    public void setPathDataDir(String pathDataDir) {
        this.pathDataDir = pathDataDir;
    }

    /**
     * Parameter used to set certain InferenceRDFGraph rules
     */
    public boolean isAllRules() {
        return allRules;
    }

    public void setAllRules(boolean allRules) {
        this.allRules = allRules;
    }

    /**
     * Parameter used to express the rdfFormat you want to parse to ontologies
     */
    public String getOntologyFormat() {
        return ontologyFormat;
    }

    public void setOntologyFormat(String ontologyFormat) {
        this.ontologyFormat = ontologyFormat;
    }

    /**
     * Parameter used if you want to set the context name of a graph
     */
    public String getNamedGraph() {
        return namedGraph;
    }

    public void setNamedGraph(String context) {
        this.namedGraph = context;
    }

    /**
     * Parameter used to set the IRI of the graph you want to create
     */
    public String getBaseIri() {
        return baseIri;
    }

    public void setBaseIri(String baseIri) {
        this.baseIri = baseIri;
    }

    /**
     * Parameter set to false if you don't want your graph to have a name
     */
    public boolean isDefaultGraph() {
        return defaultGraph;
    }

    public void setDefaultGraph(boolean defaultGraph) {
        this.defaultGraph = defaultGraph;
    }

    /**
     * This parameter contains a query that has to be applied to the graph
     */
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Parameter set to true if you want to create a new graph with the result of a construct
     */
    public boolean isNewGraph() {
        return newGraph;
    }

    public void setNewGraph(boolean newGraph) {
        this.newGraph = newGraph;
    }

    /**
     * Parameter set to true if you want to remove a graph or certain parts of it
     */
    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    /**
     * Parameter set to true if you want to shut down the repo
     */
    public boolean isRepoOff() {
        return repoOff;
    }

    public void setRepoOff(boolean repoOff) {
        this.repoOff = repoOff;
    }

    /**
     * Parameter set to true if you want to stop the route
     */
    public boolean isRouteOff() {
        return routeOff;
    }

    public void setRouteOff(boolean routeOff) {
        this.routeOff = routeOff;
    }

    /**
     * This parameter contains a list with the path of all the part of a graph you want to remove
     */
    public String getDumpFormat() {
        return dumpFormat;
    }

    public void setDumpFormat(String dumpFormat) {
        this.dumpFormat = dumpFormat;
    }

    /**
     * This parameter contains the path of a file where you want to dump your graph
     */
    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /**
     * This parameter contains the name of the file where you want to dump your graph
     */
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Parameter used to set the base configuration of this endpoint
     */
    public GraphBean getBaseConfig() {
        return baseConfig;
    }

    public void setBaseConfig(GraphBean baseConfig) {
        this.baseConfig = baseConfig;
    }

    /**
     * Parameter used to set the base configuration of this endpoint
     */
    public JsonLdOptions getJsonLdOptions() {
        return jsonLdOptions;
    }

    public void setJsonLdOptions(JsonLdOptions jsonLdOptions) {
        this.jsonLdOptions = jsonLdOptions;
    }
}
