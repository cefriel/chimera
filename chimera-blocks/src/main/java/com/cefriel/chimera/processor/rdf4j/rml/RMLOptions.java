package com.cefriel.chimera.processor.rdf4j.rml;

import java.util.List;

public class RMLOptions {

    private List<String> mappings;
    private String functionFile;
    private int batchSize;
    private boolean incrementalUpdate;
    private boolean noCache;
    private boolean ordered;
    private String baseIRI;
    private String baseIRIPrefix;

    public List<String> getMappings() {
        return mappings;
    }

    public void setMappings(List<String> mappings) {
        this.mappings = mappings;
    }

    public String getFunctionFile() {
        return functionFile;
    }

    public void setFunctionFile(String functionFile) {
        this.functionFile = functionFile;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public boolean isIncrementalUpdate() {
        return incrementalUpdate;
    }

    public void setIncrementalUpdate(boolean incrementalUpdate) {
        this.incrementalUpdate = incrementalUpdate;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public String getBaseIRI() {
        return baseIRI;
    }

    public void setBaseIRI(String baseIRI) {
        this.baseIRI = baseIRI;
    }

    public String getBaseIRIPrefix() {
        return baseIRIPrefix;
    }

    public void setBaseIRIPrefix(String baseIRIPrefix) {
        this.baseIRIPrefix = baseIRIPrefix;
    }

}
