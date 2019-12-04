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

package it.cefriel.chimera.processor.jena;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.jena.rdf.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.jenax.util.JenaUtil;

import com.complexible.pinto.MappingOptions;
import com.complexible.pinto.RDFMapper;

import it.cefriel.chimera.util.JenaSesameUtils;
import it.cefriel.chimera.util.ProcessorConstants;

public class LiftingProcessor  implements Processor{
    private String defaultNS=null;
    
    private Logger log = LoggerFactory.getLogger(LiftingProcessor.class); 

    public void process(Exchange exchange) throws Exception {
        int obj_suffix;
        String obj_id=null; 

        Model tmp_model=null;
        Model output=JenaUtil.createMemoryModel();
        String namespace=null;
        Message msg=exchange.getIn();
        Object raw_input=msg.getBody();
        namespace=msg.getHeader(ProcessorConstants.DEFAULT_NS, String.class);

        if (namespace==null)
            namespace=defaultNS;

        log.debug("Conversion of "+raw_input.getClass().getTypeName());
        if (raw_input instanceof List) {
            List objects=(List) raw_input;
            for (Object o: objects) {
                obj_suffix=System.identityHashCode(raw_input);
                obj_id=namespace+raw_input.getClass().getTypeName()+"_"+obj_suffix;

                tmp_model=lift_data(obj_id, o, namespace);
                output.add(tmp_model);
            }
        }
        else {
            obj_suffix=System.identityHashCode(raw_input);
            obj_id=namespace+raw_input.getClass().getTypeName()+"_"+obj_suffix;
            output=lift_data(obj_id, raw_input, namespace);
        }
        msg.setHeader(ProcessorConstants.OBJ_ID, obj_id);
        msg.setBody(output);

        FileOutputStream of=new FileOutputStream("/tmp/"+raw_input.getClass().getTypeName()+"-instances.n3");
        output.write(of, "TURTLE");
        of.close();

    }


    public String getDefaultNS() {
        return defaultNS;
    }

    public void setDefaultNS(String defaultNS) {
        this.defaultNS = defaultNS;
    }

    private Model lift_data(String obj_id, Object raw_input, String namespace) {
        Model output=null;

        RDFMapper aMapper = RDFMapper.builder()
                .namespace("", namespace)
                .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                .build();
        ValueFactory factory = SimpleValueFactory.getInstance();
        org.eclipse.rdf4j.model.Model persisted_obj = aMapper.writeValue(raw_input, factory.createIRI(obj_id));

        output=JenaSesameUtils.asJenaModel(persisted_obj);
        return output;
    }

}
