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

package com.cefriel.chimera.processor.collect;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountTriplesRepositoryCollectorProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(CountTriplesRepositoryCollectorProcessor.class);

    private String collectorId;

    @Override
    public void process(Exchange exchange) throws Exception {
        QueryResultCollectorProcessor qp = new QueryResultCollectorProcessor();
        qp.setCollectorId(collectorId);
        qp.setQuery("SELECT (COUNT(*) as ?num_triples) \n" +
                "WHERE { ?s ?p ?o } ");
        qp.setVariable("num_triples");
        qp.process(exchange);
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }
}
