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

package com.cefriel.util;

import com.cefriel.component.GraphBean;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConverterConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ConverterConfiguration.class);

    private String converterId;
    private List<ConverterResource> liftingMappings;
    private List<ConverterResource> loweringMappings;
    private List<ConverterResource> ontologies;
    private List<ConverterResource> additionalDataSources;
    private List<ConverterResource> constructQuery;

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

    public List<ConverterResource> getConstructQuery() {
        return constructQuery;
    }

    public void setConstructQuery(List<ConverterResource> constructQuery) {
        this.constructQuery = constructQuery;
    }

    public static List<String> setConverterConfiguration(GraphBean configuration, Exchange exchange){

        List<String> urls = new ArrayList<>();
        ConverterConfiguration converterConfiguration =
                exchange.getMessage().getHeader(ChimeraConstants.CONVERTER_CONFIGURATION, ConverterConfiguration.class);
        if (converterConfiguration != null
                && converterConfiguration.getOntologies() != null
                && converterConfiguration.getOntologies().size() > 0) {
            LOG.info("Converter converterConfiguration found in the exchange, ontologies extracted");
            configuration.setOntologyFormat(converterConfiguration.getOntologies().get(0).getSerialization());
            urls = converterConfiguration.getOntologies().stream()
                    .map(ConverterResource::getUrl)
                    .collect(Collectors.toList());
        }
        return urls;
    }
}
