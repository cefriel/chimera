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

import com.cefriel.template.utils.TemplateUtils;
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
 * rdft component which does bla bla.
 *
 * TODO: Update one line description above what the component does.
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "rdft", title = "rdft", syntax="rdft:name",
             category = {Category.JAVA})
public class RdfTemplateEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true)
    private String name;
    @UriParam(defaultValue = "null")
    private String basePath;
    @UriParam(defaultValue = "null")
    private String templatePath;
    @UriParam(defaultValue = "null")
    private String filename;
    @UriParam(defaultValue = "null")
    private String keyValuePairsPath;
    @UriParam(defaultValue = "null")
    private String keyValueCsvPath;
    @UriParam(defaultValue = "null")
    private String format;
    @UriParam(defaultValue = "null")
    private TemplateUtils utils;
    @UriParam(defaultValue = "null")
    private String queryFilePath;
    @UriParam(defaultValue = "false")
    private boolean trimTemplate;
    @UriParam(defaultValue = "false")
    private boolean verboseQueries;
    @UriParam(defaultValue = "false")
    private boolean stream;
    @UriParam(defaultValue = "null")
    private RdfTemplateBean rdfBaseConfig;

    public RdfTemplateEndpoint() {
    }

    public RdfTemplateEndpoint(String uri, String remaining, RdfTemplateComponent component) {
        super(uri, component);
        setName(remaining);
    }

    public Producer createProducer() throws Exception {
        return new RdfTemplateProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = new RdfTemplateConsumer(this, processor);
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

    /**
     * Some description of this option, and what it does
     */
    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getKeyValuePairsPath() {
        return keyValuePairsPath;
    }

    public void setKeyValuePairsPath(String keyValuePairsPath) {
        this.keyValuePairsPath = keyValuePairsPath;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getKeyValueCsvPath() {
        return keyValueCsvPath;
    }

    public void setKeyValueCsvPath(String keyValueCsvPath) {
        this.keyValueCsvPath = keyValueCsvPath;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Some description of this option, and what it does
     */
    public TemplateUtils getUtils() {
        return utils;
    }

    public void setUtils(TemplateUtils utils) {
        this.utils = utils;
    }

    /**
     * Some description of this option, and what it does
     */
    public String getQueryFilePath() {
        return queryFilePath;
    }

    public void setQueryFilePath(String queryFilePath) {
        this.queryFilePath = queryFilePath;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isTrimTemplate() {
        return trimTemplate;
    }

    public void setTrimTemplate(boolean trimTemplate) {
        this.trimTemplate = trimTemplate;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isVerboseQueries() {
        return verboseQueries;
    }

    public void setVerboseQueries(boolean verboseQueries) {
        this.verboseQueries = verboseQueries;
    }

    /**
     * Some description of this option, and what it does
     */
    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    /**
     * Some description of this option, and what it does
     */
    public RdfTemplateBean getRdfBaseConfig() {
        return rdfBaseConfig;
    }

    public void setRdfBaseConfig(RdfTemplateBean rdfBaseConfig) {
        this.rdfBaseConfig = rdfBaseConfig;
    }

}
