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

import com.cefriel.util.ChimeraResourcesBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBean {

    private String basePath = "./";
    private List<String> resources = new ArrayList<>();

    private ChimeraResourcesBean chimeraResources;

    public List<String> getQueryUrls() {
        return queryUrls;
    }

    public void setQueryUrls(List<String> queryUrls) {
        this.queryUrls = queryUrls;
    }

    private List<String> queryUrls = new ArrayList<>();
    private String rdfFormat;
    private boolean allRules;
    private String ontologyFormat;
    private String namedGraph;
    private String baseIri;
    private boolean defaultGraph = true;
    private String serverUrl;
    private String repositoryID;
    private String sparqlEndpoint;
    private String pathDataDir;
    private String query;
    private boolean newGraph;
    private boolean clear;
    private boolean repoOff = true;
    private boolean routeOff;
    private String dumpFormat;
    private String filename;

    public GraphBean() {
    }

    public GraphBean(String basePath, List<String> resources, String rdfFormat, boolean allRules,
                     String ontologyFormat, String namedGraph, String baseIri, boolean defaultGraph, String serverUrl,
                     String repositoryID, String sparqlEndpoint, String pathDataDir, String query,
                     boolean newGraph, boolean clear, boolean repoOff, boolean routeOff,
                     String dumpFormat, String filename) {
        this.basePath = basePath;
        this.resources = resources;
        this.rdfFormat = rdfFormat;
        this.allRules = allRules;
        this.ontologyFormat = ontologyFormat;
        this.namedGraph = namedGraph;
        this.baseIri = baseIri;
        this.defaultGraph = defaultGraph;
        this.serverUrl = serverUrl;
        this.repositoryID = repositoryID;
        this.sparqlEndpoint = sparqlEndpoint;
        this.pathDataDir = pathDataDir;
        this.query = query;
        this.newGraph = newGraph;
        this.clear = clear;
        this.repoOff = repoOff;
        this.routeOff = routeOff;
        this.dumpFormat = dumpFormat;
        this.filename = filename;
    }

    public GraphBean(GraphBean configuration){
        this.basePath = configuration.getBasePath();
        this.resources = configuration.getResources();
        this.rdfFormat = configuration.getRdfFormat();
        this.allRules = configuration.isAllRules();
        this.ontologyFormat = configuration.getOntologyFormat();
        this.namedGraph = configuration.getNamedGraph();
        this.baseIri = configuration.getBaseIri();
        this.defaultGraph = configuration.isDefaultGraph();
        this.serverUrl = configuration.getServerUrl();
        this.repositoryID = configuration.getRepositoryID();
        this.sparqlEndpoint = configuration.getSparqlEndpoint();
        this.pathDataDir = configuration.getPathDataDir();
        this.query = configuration.getQuery();
        this.newGraph = configuration.isNewGraph();
        this.clear = configuration.isClear();
        this.repoOff = configuration.isRepoOff();
        this.routeOff = configuration.isRouteOff();
        this.dumpFormat = configuration.getDumpFormat();
        this.filename = configuration.getFilename();
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getRdfFormat() {
        return rdfFormat;
    }

    public void setRdfFormat(String rdfFormat) {
        this.rdfFormat = rdfFormat;
    }

    public boolean isAllRules() {
        return allRules;
    }

    public void setAllRules(boolean allRules) {
        this.allRules = allRules;
    }

    public String getOntologyFormat() {
        return ontologyFormat;
    }

    public void setOntologyFormat(String ontologyFormat) {
        this.ontologyFormat = ontologyFormat;
    }

    public String getNamedGraph() {
        return namedGraph;
    }

    public void setNamedGraph(String namedGraph) {
        this.namedGraph = namedGraph;
    }

    public String getBaseIri() {
        return baseIri;
    }

    public void setBaseIri(String baseIri) {
        this.baseIri = baseIri;
    }

    public boolean isDefaultGraph() {
        return defaultGraph;
    }

    public void setDefaultGraph(boolean defaultGraph) {
        this.defaultGraph = defaultGraph;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRepositoryID() {
        return repositoryID;
    }

    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public String getPathDataDir() {
        return pathDataDir;
    }

    public void setPathDataDir(String pathDataDir) {
        this.pathDataDir = pathDataDir;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isNewGraph() {
        return newGraph;
    }

    public void setNewGraph(boolean newGraph) {
        this.newGraph = newGraph;
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public boolean isRepoOff() {
        return repoOff;
    }

    public void setRepoOff(boolean repoOff) {
        this.repoOff = repoOff;
    }

    public boolean isRouteOff() {
        return routeOff;
    }

    public void setRouteOff(boolean routeOff) {
        this.routeOff = routeOff;
    }

    public String getDumpFormat() {
        return dumpFormat;
    }

    public void setDumpFormat(String dumpFormat) {
        this.dumpFormat = dumpFormat;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ChimeraResourcesBean getChimeraResources() {
        return chimeraResources;
    }

    public void setChimeraResources(ChimeraResourcesBean chimeraResources) {
        this.chimeraResources = chimeraResources;
    }

    public void setEndpointParameters(GraphEndpoint endpoint){
        if(endpoint.getChimeraResources()!=null){
            this.setChimeraResources(endpoint.getChimeraResources());
        }
        if(endpoint.getNamedGraph()!=null){
            this.setNamedGraph(endpoint.getNamedGraph());
        }
        if(endpoint.getBaseIri()!=null){
            this.setBaseIri(endpoint.getBaseIri());
        }
        if(!endpoint.isDefaultGraph()){
            this.setDefaultGraph(endpoint.isDefaultGraph());
        }
        if(endpoint.getServerUrl()!=null){
            this.setServerUrl(endpoint.getServerUrl());
        }
        if(endpoint.getRepositoryID()!=null){
            this.setRepositoryID(endpoint.getRepositoryID());
        }
        if(endpoint.getSparqlEndpoint()!=null){
            this.setSparqlEndpoint(endpoint.getSparqlEndpoint());
        }
        if(endpoint.getResources()!=null){
            this.setResources(Arrays.asList(endpoint.getResources().split("\\|")));
        }
        if(endpoint.getQueryUrls()!=null){
            this.setQueryUrls(Arrays.asList(endpoint.getQueryUrls().split("\\|")));
        }
        if(endpoint.getOntologyFormat()!=null){
            this.setOntologyFormat(endpoint.getOntologyFormat());
        }
        if(endpoint.getPathDataDir()!=null){
            this.setPathDataDir(endpoint.getPathDataDir());
        }
        if(endpoint.isAllRules()){
            this.setAllRules(endpoint.isAllRules());
        }
        if(endpoint.getRdfFormat()!=null){
            this.setRdfFormat(endpoint.getRdfFormat());
        }
        if(endpoint.getQuery()!=null){
            this.setQuery(endpoint.getQuery());
        }
        if(endpoint.isNewGraph()){
            this.setNewGraph(endpoint.isNewGraph());
        }
        if(endpoint.isClear()){
            this.setClear(endpoint.isClear());
        }
        if(!endpoint.isRepoOff()){
            this.setRepoOff(endpoint.isRepoOff());
        }
        if(endpoint.isRouteOff()){
            this.setRouteOff(endpoint.isRouteOff());
        }
        if (endpoint.getDumpFormat()!=null){
            this.setDumpFormat(endpoint.getDumpFormat());
        }
        if(endpoint.getFilename()!=null){
            this.setFilename(endpoint.getFilename());
        }
        if(endpoint.getBasePath()!=null){
            this.setBasePath(endpoint.getBasePath());
        }
    }
}
