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

package com.cefriel.aggregationStrategy;

import com.cefriel.util.MaptTemplateConstants;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.io.InputStream;

public class KeyValueCSVAggregation implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
	if (oldExchange == null) {
            return newExchange;
        }
	
        InputStream newInputStream = newExchange.getMessage().getBody(InputStream.class);
        if (newInputStream != null)
            oldExchange.setProperty(MaptTemplateConstants.KEY_VALUE_CSV, newInputStream);
        return oldExchange;
    }
}
