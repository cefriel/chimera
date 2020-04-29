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
package com.cefriel.chimera.processor.rml;

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
    private int corePoolSize = 4;
    private int maximumPoolSize = 5;
    private int keepAliveMinutes = 10;

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

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getKeepAliveMinutes() {
        return keepAliveMinutes;
    }

    public void setKeepAliveMinutes(int keepAliveMinutes) {
        this.keepAliveMinutes = keepAliveMinutes;
    }

}
