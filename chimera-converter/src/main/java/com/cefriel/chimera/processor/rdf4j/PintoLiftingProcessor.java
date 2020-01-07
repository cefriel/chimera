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

import java.io.FileOutputStream;
import java.util.List;

import com.complexible.pinto.MappingOptions;
import com.complexible.pinto.RDFMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cefriel.chimera.context.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;

public class PintoLiftingProcessor  implements Processor{
    private String defaultNS=null;
    
    private Logger log = LoggerFactory.getLogger(PintoLiftingProcessor.class); 

    public void process(Exchange exchange) throws Exception {
        int obj_suffix;
        String obj_id=null; 
        Repository repo=null;
        Model tmp_model=null;
        Model output = new LinkedHashModel();

        String namespace=null;
        Message msg=exchange.getIn();
        Object raw_input=msg.getBody();

        namespace=exchange.getProperty(ProcessorConstants.DEFAULT_NS, String.class);
        repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class).getRepository();

        if (namespace==null)
        	namespace=defaultNS;

        try (RepositoryConnection con = repo.getConnection()) {

        	log.debug("Conversion of "+raw_input.getClass().getTypeName());
        	if (raw_input instanceof List) {
        		List objects=(List) raw_input;
        		for (Object o: objects) {
        			obj_suffix=System.identityHashCode(raw_input);
        			obj_id=namespace+raw_input.getClass().getTypeName()+"_"+obj_suffix;

        			tmp_model=lift_data(obj_id, o, namespace);
        			con.add(tmp_model);
        		}
        	}
        	else {
        		obj_suffix=System.identityHashCode(raw_input);
        		obj_id=namespace+raw_input.getClass().getTypeName()+"_"+obj_suffix;
        		output=lift_data(obj_id, raw_input, namespace);
    			con.add(output);

        	}
        	msg.setHeader(ProcessorConstants.OBJ_ID, obj_id);

        	FileOutputStream of=new FileOutputStream("/tmp/"+raw_input.getClass().getTypeName()+"-instances.ttl");
        	Rio.write(output, of, RDFFormat.TURTLE);
        	of.close();
        }
    }


    public String getDefaultNS() {
        return defaultNS;
    }

    public void setDefaultNS(String defaultNS) {
        this.defaultNS = defaultNS;
    }

    private Model lift_data(String obj_id, Object raw_input, String namespace) {
        RDFMapper aMapper = RDFMapper.builder()
                .namespace("", namespace)
                .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                .build();
        ValueFactory factory = SimpleValueFactory.getInstance();
        Model persisted_obj = aMapper.writeValue(raw_input, factory.createIRI(obj_id));

        return persisted_obj;
    }

}
