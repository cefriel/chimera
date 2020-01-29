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
package com.cefriel.chimera.processor.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamMapAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<String, InputStream> newBody = newExchange.getIn().getBody(Map.class);
        Map<String, InputStream> isMap;
        if (oldExchange == null) {
            isMap = new HashMap<>();
            isMap.putAll(newBody);
            newExchange.getIn().setBody(isMap);
            return newExchange;
        } else {
            isMap = oldExchange.getIn().getBody(Map.class);
            isMap.putAll(newBody);
            return oldExchange;
        }
    }

}
