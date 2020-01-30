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
package com.cefriel.chimera.processor.rml;

import com.cefriel.chimera.util.RMLProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RMLMessageProcessor implements Processor {

    private RMLOptions defaultRmlOptions;
    private String label;

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String streamLabel = exchange.getMessage().getHeader(RMLProcessorConstants.RML_LABEL, String.class);
        Map<String, InputStream> streamsMap = new HashMap<>();
        if (streamLabel != null) {
            exchange.getMessage().removeHeader(RMLProcessorConstants.RML_LABEL);
            streamsMap.put("is://" + streamLabel, in.getBody(InputStream.class));
        } else {
            streamsMap.put("is://" + label, in.getBody(InputStream.class));
        }

        RMLProcessor rmlProcessor = new RMLProcessor();
        if (defaultRmlOptions != null)
            rmlProcessor.setDefaultRmlOptions(defaultRmlOptions);
        
        rmlProcessor.processRML(streamsMap, exchange);
    }

    public RMLOptions getDefaultRmlOptions() {
        return defaultRmlOptions;
    }

    public void setDefaultRmlOptions(RMLOptions defaultRmlOptions) {
        this.defaultRmlOptions = defaultRmlOptions;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
