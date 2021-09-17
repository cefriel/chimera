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
package com.cefriel.chimera.processor.template;

import com.cefriel.utils.LoweringUtils;

public class TemplateLowererOptions {

    private String templatePath;
    private String destFileName;
    private String keyValuePairsPath;
    private String keyValueCsvPath;
    private String format;
    private LoweringUtils utils;
    private String queryFile;
    private boolean trimTemplate;
    private boolean resourceTemplate;
    private boolean verboseQueries;

    // Default Constructor
    public TemplateLowererOptions() {}

    public TemplateLowererOptions(TemplateLowererOptions options) {
        templatePath = options.getTemplatePath();
        destFileName = options.getDestFileName();
        keyValuePairsPath = options.getKeyValuePairsPath();
        keyValueCsvPath = options.getKeyValueCsvPath();
        format = options.getFormat();
        utils = options.getUtils();
        queryFile = options.getQueryFile();
        trimTemplate = options.isTrimTemplate();
        resourceTemplate = options.isResourceTemplate();
        verboseQueries = options.isVerboseQueries();
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getDestFileName() {
        return destFileName;
    }

    public void setDestFileName(String destFileName) {
        this.destFileName = destFileName;
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

    public LoweringUtils getUtils() {
        return utils;
    }

    public void setUtils(LoweringUtils utils) {
        this.utils = utils;
    }

    public String getQueryFile() {
        return queryFile;
    }

    public void setQueryFile(String queryFile) {
        this.queryFile = queryFile;
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

    public boolean isResourceTemplate() {
        return resourceTemplate;
    }

    public void setResourceTemplate(boolean resourceTemplate) {
        this.resourceTemplate = resourceTemplate;
    }

}
