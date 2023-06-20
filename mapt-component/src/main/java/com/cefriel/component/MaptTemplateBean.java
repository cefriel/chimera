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

import com.cefriel.template.utils.TemplateFunctions;
import com.cefriel.util.ChimeraResourceBean;

public class MaptTemplateBean {
    private String basePath = "./";
    private ChimeraResourceBean template;
    private String filename;
    private ChimeraResourceBean keyValuePairs;
    private ChimeraResourceBean keyValuePairsCSV;
    private String format;
    private ChimeraResourceBean query;
    private boolean trimTemplate;
    private boolean verboseQueries;
    private boolean isStream;
    private TemplateFunctions templateFunctions;

    // Default Constructor
    public MaptTemplateBean() {}

    public MaptTemplateBean(MaptTemplateBean options) {
        this.basePath = options.getBasePath();
        this.template = options.getTemplate();
        this.filename = options.getFilename();
        this.keyValuePairs = options.getKeyValuePairs();
        this.keyValuePairsCSV = options.getKeyValuePairsCSV();
        this.format = options.getFormat();
        this.query = options.getQuery();
        this.trimTemplate = options.isTrimTemplate();
        this.verboseQueries = options.isVerboseQueries();
        this.isStream = options.isStream();
        this.templateFunctions = options.getTemplateFunctions();
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public ChimeraResourceBean getTemplate() {
        return template;
    }

    public void setTemplate(ChimeraResourceBean template) {
        this.template = template;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ChimeraResourceBean getKeyValuePairs() {
        return keyValuePairs;
    }

    public void setKeyValuePairs(ChimeraResourceBean keyValuePairs) {
        this.keyValuePairs = keyValuePairs;
    }

    public ChimeraResourceBean getKeyValuePairsCSV() {
        return keyValuePairsCSV;
    }

    public void setKeyValuePairsCSV(ChimeraResourceBean keyValuePairsCSV) {
        this.keyValuePairsCSV = keyValuePairsCSV;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public ChimeraResourceBean getQuery() {
        return query;
    }

    public void setQuery(ChimeraResourceBean query) {
        this.query = query;
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
        return isStream;
    }

    public void setStream(boolean stream) {
        this.isStream = stream;
    }

    public TemplateFunctions getTemplateFunctions() {
        return templateFunctions;
    }

    public void setTemplateFunctions(TemplateFunctions templateFunctions) {
        this.templateFunctions = templateFunctions;
    }

    public void setConfig(MaptTemplateEndpoint endpoint){

        if (endpoint.getBasePath()!=null){
            this.setBasePath(endpoint.getBasePath());
        }
        if(endpoint.getTemplate() != null) {
            this.setTemplate(endpoint.getTemplate());
        }
        if (endpoint.getFilename()!=null){
            this.setFilename(endpoint.getFilename());
        }
        if(endpoint.getKeyValuePairs()!=null){
            this.setKeyValuePairs(endpoint.getKeyValuePairs());
        }
        if (endpoint.getKeyValuePairsCSV()!=null){
            this.setKeyValuePairsCSV(endpoint.getKeyValuePairsCSV());
        }
        if (endpoint.getFormat()!=null) {
            this.setFormat(endpoint.getFormat());
        }
        if (endpoint.getQuery() != null) {
            this.setQuery(endpoint.getQuery());
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
        if(endpoint.getTemplate() != null) {
            this.setTemplateFunctions(endpoint.getTemplateFunctions());
        }
    }
}
