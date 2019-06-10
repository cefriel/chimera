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

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.RDF4JStore;
import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.UniLoader;

public class RMLProcessor  extends SemanticLoader implements Processor{
    
    private List<String> rmlMappings=null;
    private Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();          
    private RDF4JStore rmlStore = null;
    private String label=null;

    
    public void process(Exchange exchange) throws Exception {
        Repository repo=null;
        Message in = exchange.getIn();
        String incoming_message = in.getBody(String.class);
        
        // RML Processor configuration
        if (rmlMappings==null) 
        	setRmlMappings(in.getHeader(ProcessorConstants.RML_MAPPINGS, List.class));
        if (label==null)
        	setLabel(in.getHeader(ProcessorConstants.RML_LABEL, String.class));
        repo=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();

        streamsMap.put("stream://"+label, new ByteArrayInputStream(incoming_message.getBytes()));
        
        // RML mappings execution
        try (RepositoryConnection con = repo.getConnection()) {
        	Model mapping_results=new LinkedHashModel();
        	RDF4JStore rml_results=new RDF4JStore(mapping_results);

            Executor executor = new Executor(rmlStore, new RecordsFactory(streamsMap), null, rml_results, ProcessorConstants.BASE_CONVERSION_IRI);
            executor.execute(null);
            con.add(rml_results.getModel());
        }
    }

	public List<String> getRmlMappings() {
		return rmlMappings;
	}

	public void setRmlMappings(List<String> rmlMappings) {
		this.rmlMappings = rmlMappings;
		try {
			loadMappings();
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

	private void loadMappings() throws MalformedURLException, IOException {
    	Model rml_model=new LinkedHashModel();
    	for (String resource: rmlMappings) {
            Model model = Rio.parse(UniLoader.open(resource), "", RDFFormat.TURTLE);
            rml_model.addAll(model);
    	}
    	rmlStore=new RDF4JStore(rml_model);
    }
    
}
