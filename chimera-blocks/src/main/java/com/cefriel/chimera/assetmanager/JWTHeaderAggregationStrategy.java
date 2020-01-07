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
package com.cefriel.chimera.assetmanager;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.cefriel.chimera.util.ProcessorConstants;

public class JWTHeaderAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange original, Exchange resource) {
        Object originalBody = original.getIn().getBody();
        AccessResponseToken resourceResponse = resource.getIn().getBody(AccessResponseToken.class);
        
        if (original.getPattern().isOutCapable()) {
        	System.out.println("original out: "+original.getOut()+" - "+resource.getIn().getBody());
        	if (originalBody!=null)
        		original.getOut().setBody(originalBody);
        	original.setProperty(ProcessorConstants.JWT_TOKEN, resourceResponse.getAccess());
        } else {
        	original.setProperty(ProcessorConstants.JWT_TOKEN, resourceResponse.getAccess());
        	if (originalBody!=null)
        		original.getIn().setBody(originalBody);
        }
        return original;
    }
    
}