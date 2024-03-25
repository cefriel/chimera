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
 * rdft component which does bla bla.
 *
 * TODO: Update one line description above what the component does.
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "mapt", title = "mapt", syntax="mapt:name",
             category = {Category.JAVA})
public class MaptTemplateEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true)
    private String name;
    @UriParam(defaultValue = "null")
    private String basePath;
    @UriParam(defaultValue = "null", description = "Template resource used in the mapping process")
    // compilation error if no description is provided
    private ChimeraResourceBean template;
    @UriParam(defaultValue = "null")
    private String filename;
    @UriParam(defaultValue = "null", description = "File resource containing kv pairs used to build the templateMap used in the mapping process")
    // compilation error if no description is provided
    private ChimeraResourceBean keyValuePairs;
    @UriParam(defaultValue = "null", description = "CSV file resource used to build the templateMap used in the mapping process")
    // compilation error if no description is provided
    private ChimeraResourceBean keyValuePairsCSV;
    @UriParam(defaultValue = "null")
    private String format;
    @UriParam(defaultValue = "null", description = "Query resource used in the mapping process")
    // compilation error if no description is provided
    private ChimeraResourceBean query;
    @UriParam(defaultValue = "false")
    private boolean trimTemplate;
    @UriParam(defaultValue = "false")
    private boolean verboseQueries;
    @UriParam(defaultValue = "false")
    private boolean stream;
    @UriParam(defaultValue = "null", description = "User defined class that extends the TemplateFunctions class. Use this parameter when the user defined class is loaded from outside the Java project. Used to define java functions that can be called in a template file. ")
    private ChimeraResourceBean resourceCustomFunctions;
    @UriParam(defaultValue = "null", description = "User defined class that extends the TemplateFunctions class. Use this parameter when the user defined class is included in the Java project. Used to define java functions that can be called in a template file. ")
    private TemplateFunctions customFunctions;
    
    @UriParam(defaultValue = "null")
    private MaptTemplateBean rdfBaseConfig;

    // todo add resource option if template file in resource folder

    public MaptTemplateEndpoint() {
    }

    public MaptTemplateEndpoint(String uri, String remaining, MaptTemplateComponent component) {
        super(uri, component);
        setName(remaining);
    }

    public Producer createProducer() throws Exception {
        return new MaptTemplateProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer consumer = new MaptTemplateConsumer(this, processor);
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

    public ChimeraResourceBean getTemplate() {
        return template;
    }

    public void setTemplate(ChimeraResourceBean template) {
        this.template = template;
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

    /**
     * Some description of this option, and what it does
     */
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

    public ChimeraResourceBean getResourceCustomFunctions() {
        return resourceCustomFunctions;
    }

    public void setResourceCustomFunctions(ChimeraResourceBean resourceCustomFunctions) {
        this.resourceCustomFunctions = resourceCustomFunctions;
    }

    public TemplateFunctions getCustomFunctions() {
        return customFunctions;
    }

    public void setCustomFunctions(TemplateFunctions customFunctions) {
        this.customFunctions = customFunctions;
    }

    /**
     * Some description of this option, and what it does
     */
    public MaptTemplateBean getRdfBaseConfig() {
        return rdfBaseConfig;
    }

    public void setRdfBaseConfig(MaptTemplateBean rdfBaseConfig) {
        this.rdfBaseConfig = rdfBaseConfig;
    }

}
