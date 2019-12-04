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
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.RDFSRuleReasonerFactory;
import org.apache.jena.vocabulary.ReasonerVocabulary;

import it.cefriel.chimera.reflect.Java2Shacl;
import it.cefriel.chimera.util.ProcessorConstants;

public class ModelEnricher extends SemanticLoader implements Processor{

    private Map<String ,Model> ontology_cache=null;
    
    public void process(Exchange exchange) throws Exception {
        MultiUnion ontograph=null;
        Message in = exchange.getIn();
        Object current_data = in.getBody(Model.class);

        InfModel out_model = null;
        Model data_model=null;
        Model current_ontology=null;
        Reasoner reasoner = null;
        Reasoner boundReasoner = null;
        List<String> ontology_urls=null;

        ontology_urls=in.getHeader(ProcessorConstants.ONTOLOGY_URLS, List.class);
        if (ontology_cache==null) {
            ontology_cache=new HashMap<String, Model>();
        }
        ontograph=new MultiUnion();
        for (String url: ontology_urls) {
            current_ontology=ontology_cache.get(url);
            if (current_ontology==null) {
                if (url.startsWith("package:")) {
                    String ns=in.getHeader(ProcessorConstants.DEFAULT_NS, String.class);
                    current_ontology=Java2Shacl.packageToShacle(url.replace("package:", "").trim(), ns);
                }
                else {
                    current_ontology=load_data(url);    
                }
                ontology_cache.put(url, current_ontology);
                ontograph.addGraph(current_ontology.getGraph());
            }
        }

        Model ontModel = ModelFactory.createModelForGraph(ontograph);

        if (InfModel.class.isAssignableFrom(current_data.getClass())) {
            out_model = in.getBody(InfModel.class);
            out_model.add(ontModel);
        }
        else if (Model.class.isAssignableFrom(current_data.getClass())) {
            data_model = in.getBody(Model.class);

            reasoner=RDFSRuleReasonerFactory.theInstance().create(null);
            reasoner.setParameter(ReasonerVocabulary.PROPsetRDFSLevel,ReasonerVocabulary.RDFS_SIMPLE);
            boundReasoner=reasoner.bindSchema(ontModel);
             
            out_model=ModelFactory.createInfModel(boundReasoner, data_model);
        }
        in.setBody(out_model);
    }

}
