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

import be.ugent.rml.access.AccessFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class RMLGenericProcessor implements Processor {

    private RMLOptions defaultRmlOptions;

    @Override
    public void process(Exchange exchange) throws Exception {
        RMLProcessor rmlProcessor = new RMLProcessor();

        if (defaultRmlOptions != null)
            rmlProcessor.setDefaultRmlOptions(defaultRmlOptions);

        String basePath = System.getProperty("user.dir");
        rmlProcessor.processRML(exchange, new AccessFactory(basePath));
    }

    public RMLOptions getDefaultRmlOptions() {
        return defaultRmlOptions;
    }

    public void setDefaultRmlOptions(RMLOptions defaultRmlOptions) {
        this.defaultRmlOptions = defaultRmlOptions;
    }
    
}
