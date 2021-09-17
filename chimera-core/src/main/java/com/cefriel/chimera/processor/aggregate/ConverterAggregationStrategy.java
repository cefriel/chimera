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

package com.cefriel.chimera.processor.aggregate;

import com.cefriel.chimera.util.ConverterConfiguration;
import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

public class ConverterAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange original, Exchange resource) {
        // Extract configuration
        ConverterConfiguration configuration = resource.getMessage().getBody(ConverterConfiguration.class);
        if (configuration == null)
            throw new IllegalArgumentException("Converter Configuration not found!");
        original.getMessage().setHeader(ProcessorConstants.CONVERTER_CONFIGURATION, configuration);

        // Extract token
        String token = resource.getMessage().getHeader(ProcessorConstants.JWT_TOKEN, String.class);
        if (token != null)
            original.setProperty(ProcessorConstants.JWT_TOKEN, token);

        // Check if cache should be invalidated
        String cacheInvalidation = resource.getMessage().getHeader(ProcessorConstants.CACHE_INVALIDATION, String.class);
        if (cacheInvalidation != null)
            if (cacheInvalidation.toLowerCase().equals("true"))
                original.getMessage().setHeader(ProcessorConstants.CACHE_INVALIDATION, cacheInvalidation);

        return original;
    }
}
