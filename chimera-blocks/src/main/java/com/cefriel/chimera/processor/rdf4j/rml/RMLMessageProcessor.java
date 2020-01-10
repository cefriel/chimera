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
package com.cefriel.chimera.processor.rdf4j.rml;

import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RMLMessageProcessor extends RMLProcessor {

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String label = exchange.getProperty(ProcessorConstants.RML_LABEL, String.class);
        Map<String, InputStream> streamsMap = new HashMap<>();
        streamsMap.put("is://" + label, in.getBody(InputStream.class));
        
        processRML(streamsMap, exchange);
    }
    
}
