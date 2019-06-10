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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import it.cefriel.chimera.util.ProcessorConstants;

public class DataEnricher  extends SemanticLoader implements Processor{

    private Map<String ,Model> dataset_cache=null;

    public void process(Exchange exchange) throws Exception {
        MultiUnion datagraph=null;
        Model current_dataset=null;
        List<String> master_data_urls=null;

        Message in = exchange.getIn();
        Model current_data = in.getBody(Model.class);
        master_data_urls=in.getHeader(ProcessorConstants.MASTER_DATA, List.class);


        if (dataset_cache==null) {
            dataset_cache=new HashMap<String, Model>();
        }
        datagraph = new MultiUnion();
        datagraph.setBaseGraph(current_data.getGraph());

        for (String url: master_data_urls) {
            current_dataset=dataset_cache.get(url);
            if (current_dataset==null) 
                current_dataset=load_data(url);    

            dataset_cache.put(url, current_dataset);
            datagraph.addGraph(current_dataset.getGraph());
        }

        Model dataModel = ModelFactory.createModelForGraph(datagraph);
        in.setBody(dataModel);

    }

}
