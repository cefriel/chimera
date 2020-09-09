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

package com.cefriel.chimera.processor.rml;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamToMapProcessor implements Processor {

    private String label;

    @Override
    public void process(Exchange exchange) throws Exception {
        InputStream is = exchange.getIn().getBody(InputStream.class);
        String sourceLabel = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        if (sourceLabel == null)
            sourceLabel = label;
        if (is != null && sourceLabel != null) {
            Map<String, InputStream> map = new HashMap<>();
            map.put("is://" + sourceLabel, is);
            exchange.getMessage().setBody(map, Map.class);
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
