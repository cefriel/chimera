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

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.MalformedURLException;
import java.util.*;

import be.ugent.rml.Utils;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.RDF4JStore;
import com.cefriel.chimera.context.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;

public class RMLProcessor implements Processor{
    private Logger log = LoggerFactory.getLogger(RMLProcessor.class); 

    private List<String> rmlMappings;
    private Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();          
    private RDF4JStore rmlStore;
    private String label;
    
    public void process(Exchange exchange) throws Exception {
        Repository repo;
        Message in = exchange.getIn();
        InputStream incoming_message = in.getBody(InputStream.class);
        String stream_label=null;
        
        // RML Processor configuration
        if (rmlMappings==null) {
            List<String> mappingsFiles = exchange.getProperty(ProcessorConstants.RML_MAPPINGS, List.class);
            List<InputStream> mappings =new ArrayList<>();
            for(String m : mappingsFiles)
                mappings.add(IOUtils.toInputStream(m, "UTF-8"));
            InputStream is = new SequenceInputStream(Collections.enumeration(mappings));
            rmlStore = new RDF4JStore();
            rmlStore.read(is, null, RDFFormat.TURTLE);
        }

        if (label==null)
        	stream_label = exchange.getProperty(ProcessorConstants.RML_LABEL, String.class);
        else
        	stream_label = label;
        
        log.info("Incoming message: "+incoming_message);
        log.info("RML mappings: " + rmlMappings);
        log.info("RML label: "+stream_label);
        repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class).getRepository();

        streamsMap.put("stream://"+stream_label, incoming_message);
        
        // RML mappings execution
        try (RepositoryConnection con = repo.getConnection()) {
        	// Results
        	RDF4JStore rml_results=new RDF4JStore();
        	
        	// Mappings execution
            Executor executor = new Executor(rmlStore, new RecordsFactory(null, streamsMap), null, rml_results, ProcessorConstants.BASE_CONVERSION_IRI);
            executor.execute(null);
            con.add(rml_results.getModel());
        	log.info("RML output: "+rml_results.toString());

        }
    }

	public List<String> getRmlMappings() {
		return rmlMappings;
	}
	    
    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
    
}