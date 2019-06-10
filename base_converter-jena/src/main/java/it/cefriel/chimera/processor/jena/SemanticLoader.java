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

import java.io.InputStream;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

public class SemanticLoader implements Processor {
    protected Model load_data(String url) {
        Model model = ModelFactory.createDefaultModel() ;
        if (url.startsWith("/")) {
            model.read(url) ;    
        }
        else if (url.startsWith("classpath://")){
            model.read(this.getClass().getClassLoader().getResourceAsStream(url.replaceAll("classpath://", "")),null);
        }
        else if (url.startsWith("http")){
            RDFDataMgr.read(model, url) ;
        }

        return model;

    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message msg=exchange.getIn();
        InputStream is=msg.getBody(InputStream.class);
        Model model = ModelFactory.createDefaultModel() ;
        model.read(is, null, "TURTLE");
        msg.setBody(model);
    }
}
