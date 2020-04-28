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

package com.cefriel.chimera.processor;

import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class BaseIRIProcessor implements Processor {

    private String baseIRI = ProcessorConstants.BASE_IRI_VALUE;

    public void process(Exchange exchange) throws Exception {
        String headerBaseIRI = exchange.getMessage().getHeader(ProcessorConstants.BASE_IRI, String.class);
        if (headerBaseIRI != null)
            baseIRI = headerBaseIRI;

        ProcessorConstants.BASE_IRI_VALUE = baseIRI;
    }

    public String getBaseIRI() {
        return baseIRI;
    }

    public void setBaseIRI(String baseIRI) {
        this.baseIRI = baseIRI;
    }
}
