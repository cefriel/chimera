/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.processor.template;

public class TemplateLowererOptions {

    private String templatePath;
    private String destFileName;
    private String keyValuePairsPath;
    private String keyValueCsvPath;
    private String format;
    private String utils;
    private String queryFile;
    private boolean trimTemplate;
    private boolean verboseQueries;
    private boolean attachmentToExchange;

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

    public String getUtils() {
        return utils;
    }

    public void setUtils(String utils) {
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

    public boolean isAttachmentToExchange() {
        return attachmentToExchange;
    }

    public void setAttachmentToExchange(boolean attachmentToExchange) {
        this.attachmentToExchange = attachmentToExchange;
    }

}
