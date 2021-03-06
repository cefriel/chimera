/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.jwt;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import com.cefriel.chimera.util.ProcessorConstants;

public class JWTHeaderAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange original, Exchange resource) {
        AccessResponseToken resourceResponse = resource.getIn().getBody(AccessResponseToken.class);
        original.setProperty(ProcessorConstants.JWT_TOKEN, resourceResponse.getAccess());
        return original;
    }
    
}