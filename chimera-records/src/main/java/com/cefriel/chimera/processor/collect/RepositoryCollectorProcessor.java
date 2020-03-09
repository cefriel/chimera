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

import com.cefriel.chimera.graph.RDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.RecordCollector;
import com.cefriel.chimera.util.RecordProcessorConstants;
import com.cefriel.chimera.util.Utils;
import com.cefriel.utils.rdf.RDFReader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class RepositoryCollectorProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RepositoryCollectorProcessor.class);

    private String collectorId;

    @Override
    public void process(Exchange exchange) throws Exception {
        String collectorId = exchange.getMessage().getHeader(RecordProcessorConstants.COLLECTOR_ID, String.class);
        if (collectorId == null)
            collectorId = this.collectorId;
        if (collectorId == null) {
            logger.info("Collector ID not found. Attach it to header using as key " + RecordProcessorConstants.COLLECTOR_ID);
            return;
        }

        Repository repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
        IRI contextIRI = Utils.getContextIRI(exchange);
        RDFReader reader = new RDFReader(repo, contextIRI);
        List<Map<String,String>> results = reader.executeQueryStringValue("SELECT (COUNT(*) as ?num) \n" +
                "WHERE { ?s ?p ?o } ");
        String num = "Not available";
        if (results != null)
            if (!results.isEmpty())
                num = results.get(0).get("num");

        RecordCollector collector = exchange.getProperty(collectorId, RecordCollector.class);
        if (collector == null) {
            RecordCollectorProcessor rcp = new RecordCollectorProcessor();
            rcp.process(exchange);
        } else {
            String[] record = new String[3];
            String context = exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
            if (context != null)
                record[0] = context;
            else
                record[0] = exchange.getExchangeId();
            record[1] = "num_triples";
            record[2] = num;
            collector.addRecord(record);
        }
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }
}
