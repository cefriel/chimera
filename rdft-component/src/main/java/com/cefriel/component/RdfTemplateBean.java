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

public class RdfTemplateBean {

    private String basePath = "./";
    private String templatePath;
    private String filename;
    private String keyValuePairsPath;
    private String keyValueCsvPath;
    private String format;
    private TemplateUtils utils;
    private String queryFilePath;
    private boolean trimTemplate;
    private boolean verboseQueries;
    private boolean stream;

    // Default Constructor
    public RdfTemplateBean() {}

    public RdfTemplateBean(RdfTemplateBean options) {
        basePath = options.getBasePath();
        templatePath = options.getTemplatePath();
        filename = options.getFilename();
        keyValuePairsPath = options.getKeyValuePairsPath();
        keyValueCsvPath = options.getKeyValueCsvPath();
        format = options.getFormat();
        utils = options.getUtils();
        queryFilePath = options.getQueryFilePath();
        trimTemplate = options.isTrimTemplate();
        verboseQueries = options.isVerboseQueries();
        stream = options.isStream();
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKeyValuePairsPath() {
        return keyValuePairsPath;
    }

    public void setKeyValuePairsPath(String keyValuePairsPath) {
        this.keyValuePairsPath = keyValuePairsPath;
    }

    public String getKeyValueCsvPath() {
        return keyValueCsvPath;
    }

    public void setKeyValueCsvPath(String keyValueCsvPath) {
        this.keyValueCsvPath = keyValueCsvPath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public TemplateUtils getUtils() {
        return utils;
    }

    public void setUtils(TemplateUtils utils) {
        this.utils = utils;
    }

    public String getQueryFilePath() {
        return queryFilePath;
    }

    public void setQueryFilePath(String queryFilePath) {
        this.queryFilePath = queryFilePath;
    }

    public boolean isTrimTemplate() {
        return trimTemplate;
    }

    public void setTrimTemplate(boolean trimTemplate) {
        this.trimTemplate = trimTemplate;
    }

    public boolean isVerboseQueries() {
        return verboseQueries;
    }

    public void setVerboseQueries(boolean verboseQueries) {
        this.verboseQueries = verboseQueries;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public void setConfig(RdfTemplateEndpoint endpoint){

        if (endpoint.getBasePath()!=null){
            this.setBasePath(endpoint.getBasePath());
        }
        if (endpoint.getTemplatePath()!=null) {
            this.setTemplatePath(endpoint.getTemplatePath());
        }
        if (endpoint.getFilename()!=null){
            this.setFilename(endpoint.getFilename());
        }
        if (endpoint.getKeyValuePairsPath()!=null) {
            this.setKeyValuePairsPath(endpoint.getKeyValuePairsPath());
        }
        if (endpoint.getKeyValueCsvPath()!=null) {
            this.setKeyValueCsvPath(endpoint.getKeyValueCsvPath());
        }
        if (endpoint.getFormat()!=null) {
            this.setFormat(endpoint.getFormat());
        }
        if (endpoint.getUtils()!=null) {
            this.setUtils(endpoint.getUtils());
        }
        if (endpoint.getQueryFilePath()!=null) {
            this.setQueryFilePath(endpoint.getQueryFilePath());
        }
        if (endpoint.isTrimTemplate()) {
            this.setTrimTemplate(endpoint.isTrimTemplate());
        }
        if (endpoint.isVerboseQueries()) {
            this.setVerboseQueries(endpoint.isVerboseQueries());
        }
        if (endpoint.isStream()) {
            this.setStream(endpoint.isStream());
        }
    }
}
