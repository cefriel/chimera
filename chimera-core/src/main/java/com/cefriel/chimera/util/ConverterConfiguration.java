/*
 * Copyright 2020 Cefriel.
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

package com.cefriel.chimera.util;

import java.util.ArrayList;
import java.util.List;

public class ConverterConfiguration {

    private String converterId;
    private List<ConverterResource> liftingMappings;
    private List<ConverterResource> loweringMappings;
    private List<ConverterResource> ontologies;
    private List<ConverterResource> additionalDataSources;

    public String getConverterId() {
        return converterId;
    }

    public void setConverterId(String converterId) {
        this.converterId = converterId;
    }

    public void addLiftingMapping(String url, String serialization) {
        if (liftingMappings == null)
            liftingMappings = new ArrayList<>();
        if (url != null && serialization != null)
            liftingMappings.add(new ConverterResource(url, serialization));
    }

    public void addLoweringMapping(String url, String serialization) {
        if (loweringMappings == null)
            loweringMappings = new ArrayList<>();
        if (url != null && serialization != null)
            loweringMappings.add(new ConverterResource(url, serialization));
    }

    public void addOntology(String url, String serialization) {
        if (ontologies == null)
            ontologies = new ArrayList<>();
        if (url != null && serialization != null)
            ontologies.add(new ConverterResource(url, serialization));
    }

    public void addAdditionalDataSource(String url, String serialization) {
        if (additionalDataSources == null)
            additionalDataSources = new ArrayList<>();
        if (url != null && serialization != null)
            additionalDataSources.add(new ConverterResource(url, serialization));
    }

    public List<ConverterResource> getLiftingMappings() {
        return liftingMappings;
    }

    public void setLiftingMappings(List<ConverterResource> liftingMappings) {
        this.liftingMappings = liftingMappings;
    }

    public List<ConverterResource> getLoweringMappings() {
        return loweringMappings;
    }

    public void setLoweringMappings(List<ConverterResource> loweringMappings) {
        this.loweringMappings = loweringMappings;
    }

    public List<ConverterResource> getOntologies() {
        return ontologies;
    }

    public void setOntologies(List<ConverterResource> ontologies) {
        this.ontologies = ontologies;
    }

    public List<ConverterResource> getAdditionalDataSources() {
        return additionalDataSources;
    }

    public void setAdditionalDataSources(List<ConverterResource> additionalDataSources) {
        this.additionalDataSources = additionalDataSources;
    }
}
