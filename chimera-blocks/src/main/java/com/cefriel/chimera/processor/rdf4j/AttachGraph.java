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
package com.cefriel.chimera.processor.rdf4j;

import com.cefriel.chimera.context.HTTPRDFGraph;
import com.cefriel.chimera.context.RDFGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.cefriel.chimera.context.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AttachGraph implements Processor {

    private Logger log = LoggerFactory.getLogger(AttachGraph.class);

    private String DB_ADDRESS;
    private String REPOSITORY_ID;

    public void process(Exchange exchange) throws Exception {
        RDFGraph graph;
        if (DB_ADDRESS != null && REPOSITORY_ID != null)
            graph = new HTTPRDFGraph(DB_ADDRESS, REPOSITORY_ID);
        else
            graph = new MemoryRDFGraph();
    	exchange.setProperty(ProcessorConstants.CONTEXT_GRAPH, graph);
    }

    public String getDB_ADDRESS() {
        return DB_ADDRESS;
    }

    public void setDB_ADDRESS(String DB_ADDRESS) {
        this.DB_ADDRESS = DB_ADDRESS;
    }

    public String getREPOSITORY_ID() {
        return REPOSITORY_ID;
    }

    public void setREPOSITORY_ID(String REPOSITORY_ID) {
        this.REPOSITORY_ID = REPOSITORY_ID;
    }

}