/*
 * Copyright (c) 2019-2021 Cefriel.
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
import com.cefriel.utils.rdf.RDFReader;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryResultCollectorProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(QueryResultCollectorProcessor.class);

    private String collectorId;
    private String query;
    private String variable;

    @Override
    public void process(Exchange exchange) throws Exception {
        String collectorId = exchange.getMessage().getHeader(RecordProcessorConstants.COLLECTOR_ID, String.class);
        if (collectorId == null)
            collectorId = this.collectorId;
        if (collectorId == null)
            logger.error("Collector ID not found. Attach it to header using as key " + RecordProcessorConstants.COLLECTOR_ID);

        RDFGraph graph = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class);
        if (graph == null)
            throw new RuntimeException("RDF Graph not attached");
        Repository repo = graph.getRepository();
        IRI contextIRI = graph.getContext();
        RDFReader reader = new RDFReader(repo, contextIRI);
        List<Map<String,String>> results = reader.executeQueryStringValue(query);
        String result = "No results!";
        RecordCollector collector = null;
        if (collectorId != null)
            collector = exchange.getProperty(collectorId, RecordCollector.class);
        if (collector == null && collectorId != null) {
            RecordCollectorProcessor rcp = new RecordCollectorProcessor();
            rcp.setCollectorId(collectorId);
            rcp.process(exchange);
        } else {
            if (results != null)
                if (!results.isEmpty()) {
                    String graphID = exchange.getProperty(ProcessorConstants.GRAPH_ID, String.class);
                    for (Map<String, String> row : results) {
                        String r = row.get(variable);
                        String[] record = new String[3];
                        if (graphID != null)
                            record[0] = graphID;
                        else
                            record[0] = exchange.getExchangeId();
                        record[1] = variable;
                        record[2] = r;
                        if (collector != null)
                            collector.addRecord(record);
                        else
                            logger.info(Arrays.toString(record));
                    }
                }
        }
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }
}
