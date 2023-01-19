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
import com.cefriel.util.ChimeraRmlConstants;

public class RmlBean {

    private String basePath;
    private ChimeraResourcesBean inputFiles; // todo chimera resources
    private boolean useMessage;
    private ChimeraResourcesBean mappings; // paths to mapping files .rml // todo chimera resources
    private ChimeraResourcesBean functionFiles; // todo chimera resources
    private int batchSize; //triples writing strategy parameters
    private boolean incrementalUpdate; //scrivo tutto alla fine o a mano a mano
    private boolean noCache; // rename to noCache record
    private boolean ordered; // ordered execution of triple maps by logical source
    private String baseIri;
    private String baseIriPrefix;
    private boolean emptyStrings; // what to do when i have empty strings in the source
    private boolean concurrentWrites;
    private boolean concurrentRecords;
    private boolean defaultRecordFactory; // decide weather or not to handle inputs as just inputstream
    private int numThreadsRecords = ChimeraRmlConstants.DEFAULT_NUM_THREADS;
    private int numThreadsWrites = ChimeraRmlConstants.DEFAULT_NUM_THREADS;
    private String concurrency; // 3 possible values, type of concurrency
    private boolean singleRecordsFactory; // in caso di concorrenza usa sempre la stessa recordFactory
    private String streamName; // boh, controllare
    private String baseUrl; // boh, controllare
    private String prefixLogicalSource; // for mappings applied to streams only, prefix that is specified in the mapping file (default is://)

    public RmlBean() {
        mappings = new ChimeraResourcesBean();
        functionFiles = new ChimeraResourcesBean();
        inputFiles = new ChimeraResourcesBean();
    }

    public RmlBean(RmlBean options) {
        this();
        if(options != null) {
            if (options.getInputFiles() != null)
                inputFiles = options.getInputFiles();
            if (options.getMappings() != null)
                mappings = options.getMappings();
            if (options.getFunctionFiles() != null)
                functionFiles = options.getFunctionFiles();
            basePath = options.getBasePath();
            useMessage = options.isUseMessage();
            batchSize = options.getBatchSize();
            incrementalUpdate = options.isIncrementalUpdate();
            noCache = options.isNoCache();
            ordered = options.isOrdered();
            baseIri = options.getBaseIri();
            baseIriPrefix = options.getBaseIriPrefix();
            emptyStrings = options.isEmptyStrings();
            concurrentWrites = options.isConcurrentWrites();
            concurrentRecords = options.isConcurrentRecords();
            defaultRecordFactory = options.isDefaultRecordFactory();
            numThreadsRecords = options.getNumThreadsRecords();
            numThreadsWrites = options.getNumThreadsWrites();
            concurrency = options.getConcurrency();
            singleRecordsFactory = options.isSingleRecordsFactory();
            streamName = options.getStreamName();
            baseUrl = options.getBaseUrl();
            prefixLogicalSource = options.getPrefixLogicalSource();
        }
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public ChimeraResourcesBean getInputFiles() {
        return inputFiles;
    }

    public void setInputFiles(ChimeraResourcesBean inputFiles) {
        this.inputFiles = inputFiles;
    }

    public boolean isUseMessage() {
        return useMessage;
    }

    public void setUseMessage(boolean useMessage) {
        this.useMessage = useMessage;
    }

    public ChimeraResourcesBean getMappings() {
        return mappings;
    }

    public void setMappings(ChimeraResourcesBean mappings) {
        this.mappings = mappings;
    }

    public ChimeraResourcesBean getFunctionFiles() {
        return functionFiles;
    }

    public void setFunctionFiles(ChimeraResourcesBean functionFiles) {
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

    public String getBaseIri() {
        return baseIri;
    }

    public void setBaseIri(String baseIri) {
        this.baseIri = baseIri;
    }

    public String getBaseIriPrefix() {
        return baseIriPrefix;
    }

    public void setBaseIriPrefix(String baseIriPrefix) {
        this.baseIriPrefix = baseIriPrefix;
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

    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    public boolean isSingleRecordsFactory() {
        return singleRecordsFactory;
    }

    public void setSingleRecordsFactory(boolean singleRecordsFactory) {
        this.singleRecordsFactory = singleRecordsFactory;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public String getPrefixLogicalSource() {
        return prefixLogicalSource;
    }

    public void setPrefixLogicalSource(String prefixLogicalSource) {
        this.prefixLogicalSource = prefixLogicalSource;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setConfig(RmlEndpoint endpoint){

        if (endpoint.getBasePath()!=null) {
            this.setBasePath(endpoint.getBasePath());
        }
        if (endpoint.getInputFiles()!=null) {
            this.setInputFiles(endpoint.getInputFiles());
        }
        if (endpoint.isUseMessage()){
            this.setUseMessage(endpoint.isUseMessage());
        }
        if (endpoint.getMappings()!=null) {
            this.setMappings(endpoint.getMappings());
        }
        if (endpoint.getFunctionFiles()!=null) {
            this.setFunctionFiles(endpoint.getFunctionFiles());
        }
        if (endpoint.getBatchSize()!=0) {
            this.setBatchSize(endpoint.getBatchSize());
        }
        if (endpoint.isIncrementalUpdate()) {
            this.setIncrementalUpdate(endpoint.isIncrementalUpdate());
        }
        if (endpoint.isNoCache()) {
            this.setNoCache(endpoint.isNoCache());
        }
        if (endpoint.isOrdered()) {
            this.setOrdered(endpoint.isOrdered());
        }
        if (endpoint.getBaseIri()!=null) {
            this.setBaseIri(endpoint.getBaseIri());
        }
        if (endpoint.getBaseIriPrefix()!=null) {
            this.setBaseIriPrefix(endpoint.getBaseIriPrefix());
        }
        if (endpoint.isEmptyStrings()) {
            this.setEmptyStrings(endpoint.isEmptyStrings());
        }
        if (endpoint.isConcurrentRecords()) {
            this.setConcurrentRecords(endpoint.isConcurrentRecords());
        }
        if (endpoint.isConcurrentWrites()) {
            this.setConcurrentWrites(endpoint.isConcurrentWrites());
        }
        if (endpoint.isDefaultRecordFactory()) {
            this.setDefaultRecordFactory(endpoint.isDefaultRecordFactory());
        }
        if (endpoint.getNumThreadsRecords()!=0) {
            this.setNumThreadsRecords(endpoint.getNumThreadsRecords());
        }
        if (endpoint.getNumThreadsWrites()!=0) {
            this.setNumThreadsWrites(endpoint.getNumThreadsWrites());
        }
        if (endpoint.getConcurrency()!=null) {
            this.setConcurrency(endpoint.getConcurrency());
        }
        if (endpoint.isSingleRecordsFactory()) {
            this.setSingleRecordsFactory(endpoint.isSingleRecordsFactory());
        }
        if (endpoint.getStreamName()!=null) {
            this.setStreamName(endpoint.getStreamName());
        }
        if (endpoint.getPrefixLogicalSource()!=null) {
            this.setPrefixLogicalSource(endpoint.getPrefixLogicalSource());
        }
        if (endpoint.getBaseUrl()!=null) {
            this.setBaseUrl(endpoint.getBaseUrl());
        }
    }
}
