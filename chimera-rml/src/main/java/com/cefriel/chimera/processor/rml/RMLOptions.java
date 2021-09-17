/*
 * Copyright (c) 2019-2021 Cefriel.
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
package com.cefriel.chimera.processor.rml;

import com.cefriel.chimera.util.RMLProcessorConstants;

import java.util.ArrayList;
import java.util.List;

public class RMLOptions {

    private List<String> mappings;
    private List<String> functionFiles;
    private int batchSize;
    private boolean incrementalUpdate;
    private boolean noCache;
    private boolean ordered;
    private String baseIRI;
    private String baseIRIPrefix;
    private boolean emptyStrings;
    private boolean concurrentWrites;
    private boolean concurrentRecords;
    private boolean defaultRecordFactory;
    int numThreadsRecords = RMLProcessorConstants.DEFAULT_NUM_THREADS;
    int numThreadsWrites = RMLProcessorConstants.DEFAULT_NUM_THREADS;

    public RMLOptions() {
        mappings = new ArrayList<>();
        functionFiles = new ArrayList<>();
    }
    public RMLOptions(RMLOptions options) {
        this();
        if(options != null) {
            if (options.getMappings() != null)
                mappings = new ArrayList<>(options.getMappings());
            if (options.getFunctionFiles() != null)
                functionFiles = new ArrayList<>(options.getFunctionFiles());
            batchSize = options.getBatchSize();
            incrementalUpdate = options.isIncrementalUpdate();
            noCache = options.isNoCache();
            ordered = options.isOrdered();
            baseIRI = options.getBaseIRI();
            baseIRIPrefix = options.getBaseIRIPrefix();
            emptyStrings = options.isEmptyStrings();
            concurrentWrites = options.isConcurrentWrites();
            concurrentRecords = options.isConcurrentRecords();
            defaultRecordFactory = options.isDefaultRecordFactory();
            numThreadsRecords = options.getNumThreadsRecords();
            numThreadsWrites = options.getNumThreadsWrites();
        }
    }

    public List<String> getMappings() {
        return mappings;
    }

    public void setMappings(List<String> mappings) {
        this.mappings = mappings;
    }

    public List<String> getFunctionFiles() {
        return functionFiles;
    }

    public void setFunctionFiles(List<String> functionFiles) {
        this.functionFiles = functionFiles;
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

    public boolean isEmptyStrings() {
        return emptyStrings;
    }

    public void setEmptyStrings(boolean emptyStrings) {
        this.emptyStrings = emptyStrings;
    }

    public boolean isConcurrentWrites() {
        return concurrentWrites;
    }

    public void setConcurrentWrites(boolean concurrentWrites) {
        this.concurrentWrites = concurrentWrites;
    }

    public boolean isConcurrentRecords() {
        return concurrentRecords;
    }

    public void setConcurrentRecords(boolean concurrentRecords) {
        this.concurrentRecords = concurrentRecords;
    }

    public boolean isDefaultRecordFactory() {
        return defaultRecordFactory;
    }

    public void setDefaultRecordFactory(boolean defaultRecordFactory) {
        this.defaultRecordFactory = defaultRecordFactory;
    }

    public int getNumThreadsRecords() {
        return numThreadsRecords;
    }

    public void setNumThreadsRecords(int numThreadsRecords) {
        this.numThreadsRecords = numThreadsRecords;
    }

    public int getNumThreadsWrites() {
        return numThreadsWrites;
    }

    public void setNumThreadsWrites(int numThreadsWrites) {
        this.numThreadsWrites = numThreadsWrites;
    }

}
