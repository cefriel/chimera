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

import com.cefriel.util.ChimeraResourceBean;
import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * rml component which does bla bla.
 *
 * TODO: Update one line description above what the component does.
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "rml", title = "rml", syntax="rml:name",
             category = {Category.JAVA})
public class RmlEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true)
    private String name;
    @UriParam(defaultValue = "null")
    private String basePath;
    @UriParam(defaultValue = "null")
    private ChimeraResourceBean inputFile;
    @UriParam(defaultValue = "false")
    private boolean useMessage;
    @UriParam(defaultValue = "null")
    private ChimeraResourceBean mapping;
    @UriParam(defaultValue = "null")
    private ChimeraResourceBean functionFile;
    @UriParam(defaultValue = "0")
    private int batchSize = 0;
    @UriParam(defaultValue = "false")
    private boolean incrementalUpdate;
    @UriParam(defaultValue = "false")
    private boolean noCache;
    @UriParam(defaultValue = "false")
    private boolean ordered;
    @UriParam(defaultValue = "null")
    private String baseIri;
    @UriParam(defaultValue = "null")
    private String baseIriPrefix;
    @UriParam(defaultValue = "false")
    private boolean emptyStrings;
    @UriParam(defaultValue = "false")
    private boolean concurrentWrites;
    @UriParam(defaultValue = "false")
    private boolean concurrentRecords;
    @UriParam(defaultValue = "false")
    private boolean defaultRecordFactory;
    @UriParam(defaultValue = "0")
    private int numThreadsRecords = 0;
    @UriParam(defaultValue = "0")
    private int numThreadsWrites = 0;
    @UriParam(defaultValue = "null")
    private String concurrency;
    @UriParam(defaultValue = "false")
    private boolean singleRecordsFactory;
    @UriParam(defaultValue = "null")
    private String streamName;
    @UriParam(defaultValue = "null")
    private String prefixLogicalSource;
    @UriParam(defaultValue = "null")
    private String baseUrl;
    @UriParam(defaultValue = "null")
    private RmlBean rmlBaseConfig;

    public RmlEndpoint() {
    }

    public RmlEndpoint(String uri, RmlComponent component) {
        super(uri, component);
    }

    public Producer createProducer() throws Exception {
        return new RmlProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = new RmlConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public ChimeraResourceBean getInputFile() {
        return inputFile;
    }

    public void setInputFile(ChimeraResourceBean inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isUseMessage() {
        return useMessage;
    }

    public void setUseMessage(boolean useMessage) {
        this.useMessage = useMessage;
    }

    public ChimeraResourceBean getMapping() {
        return mapping;
    }

    public void setMapping(ChimeraResourceBean mapping) {
        this.mapping = mapping;
    }

    public ChimeraResourceBean getFunctionFile() {
        return functionFile;
    }

    public void setFunctionFile(ChimeraResourceBean functionFile) {
        this.functionFile = functionFile;
    }

    /**
     * Some description of this option, and what it does
     */
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isIncrementalUpdate() {
        return incrementalUpdate;
    }

    public void setIncrementalUpdate(boolean incrementalUpdate) {
        this.incrementalUpdate = incrementalUpdate;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getBaseIri() {
        return baseIri;
    }

    public void setBaseIri(String baseIri) {
        this.baseIri = baseIri;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getBaseIriPrefix() {
        return baseIriPrefix;
    }

    public void setBaseIriPrefix(String baseIriPrefix) {
        this.baseIriPrefix = baseIriPrefix;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isEmptyStrings() {
        return emptyStrings;
    }

    public void setEmptyStrings(boolean emptyStrings) {
        this.emptyStrings = emptyStrings;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isConcurrentWrites() {
        return concurrentWrites;
    }

    public void setConcurrentWrites(boolean concurrentWrites) {
        this.concurrentWrites = concurrentWrites;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isConcurrentRecords() {
        return concurrentRecords;
    }

    public void setConcurrentRecords(boolean concurrentRecords) {
        this.concurrentRecords = concurrentRecords;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isDefaultRecordFactory() {
        return defaultRecordFactory;
    }

    public void setDefaultRecordFactory(boolean defaultRecordFactory) {
        this.defaultRecordFactory = defaultRecordFactory;
    }

    /**
     * Some description of this option, and what it does
     */
    public int getNumThreadsRecords() {
        return numThreadsRecords;
    }

    public void setNumThreadsRecords(int numThreadsRecords) {
        this.numThreadsRecords = numThreadsRecords;
    }

    /**
     * Some description of this option, and what it does
     */
    public int getNumThreadsWrites() {
        return numThreadsWrites;
    }

    public void setNumThreadsWrites(int numThreadsWrites) {
        this.numThreadsWrites = numThreadsWrites;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(String concurrency) {
        this.concurrency = concurrency;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isSingleRecordsFactory() {
        return singleRecordsFactory;
    }

    public void setSingleRecordsFactory(boolean singleRecordsFactory) {
        this.singleRecordsFactory = singleRecordsFactory;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getPrefixLogicalSource() {
        return prefixLogicalSource;
    }

    public void setPrefixLogicalSource(String prefixLogicalSource) {
        this.prefixLogicalSource = prefixLogicalSource;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Some description of this option, and what it does
     */
    public RmlBean getRmlBaseConfig() {
        return rmlBaseConfig;
    }

    public void setRmlBaseConfig(RmlBean rmlBaseConfig) {
        this.rmlBaseConfig = rmlBaseConfig;
    }
}
