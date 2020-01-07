/*
 * Copyright 2018 Cefriel.
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

package it.cefriel.chimera.processor.rdf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.RDF4JStore;
import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.UniLoader;

public class RMLProcessor implements Processor{
    private Logger log = LoggerFactory.getLogger(RMLProcessor.class); 

    private List<String> rmlMappings=null;
    private Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();          
    private RDF4JStore rmlStore = null;
    private String label=null;

    
    public void process(Exchange exchange) throws Exception {
        Repository repo=null;
        Message in = exchange.getIn();
        InputStream incoming_message = in.getBody(InputStream.class);
        String stream_label=null;
        
        // RML Processor configuration
        if (rmlMappings==null) 
        	loadMappings(exchange.getProperty(ProcessorConstants.RML_MAPPINGS, List.class));
        if (label==null) {
        	stream_label=exchange.getProperty(ProcessorConstants.RML_LABEL, String.class);
    	}
        else {
        	stream_label=label;
        }
        
        log.info("Incoming message: "+incoming_message);
        log.info("RML mappings: "+rmlMappings);
        log.info("RML label: "+stream_label);
        repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        streamsMap.put("stream://"+stream_label, incoming_message);
        
        // RML mappings execution
        try (RepositoryConnection con = repo.getConnection()) {
        	// Results
        	Model rml_results_model=new LinkedHashModel();
        	RDF4JStore rml_results=new RDF4JStore(rml_results_model);
        	
        	// Mappings execution
            Executor executor = new Executor(rmlStore, new RecordsFactory(streamsMap), null, rml_results, ProcessorConstants.BASE_CONVERSION_IRI);
            executor.execute(null);
            con.add(rml_results.getModel());
        	log.info("RML output: "+rml_results.toString());

        }
    }

	public List<String> getRmlMappings() {
		return rmlMappings;
	}

	public void setRmlMappings(List<String> rmlMappings) {
		this.rmlMappings = rmlMappings;
		try {
			loadMappings(rmlMappings);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    
    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	private void loadMappings(List<String> rmlMappings) throws MalformedURLException, IOException {
    	Model rml_model=new LinkedHashModel();
    	for (String resource: rmlMappings) {
            Model model = Rio.parse(UniLoader.open(resource), "", RDFFormat.TURTLE);
            rml_model.addAll(model);
    	}
    	log.debug(rml_model.toString());
    	rmlStore=new RDF4JStore(rml_model);
    }
    
}
