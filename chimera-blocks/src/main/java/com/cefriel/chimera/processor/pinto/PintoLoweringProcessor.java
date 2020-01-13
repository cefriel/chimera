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
package com.cefriel.chimera.processor.pinto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.complexible.pinto.MappingOptions;
import com.complexible.pinto.RDFMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.util.Connections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import com.cefriel.chimera.util.ProcessorConstants;

public class PintoLoweringProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(PintoLoweringProcessor.class);

    private HashMap<String, List<String>> standards;

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        String dest_standard;
        String obj_id;
        String namespace;
        Message msg = exchange.getIn();
        Object result = null;
        List<String> classNames;
        Repository repo;

        ValueFactory vf = SimpleValueFactory.getInstance();

        obj_id = exchange.getProperty(ProcessorConstants.OBJ_ID, String.class);
        dest_standard = exchange.getProperty(ProcessorConstants.DEST_STD, String.class);
        addDestinationStandard(dest_standard);

        // TODO Use me!
        classNames = standards.get(exchange.getProperty(ProcessorConstants.DEST_STD, String.class));

        namespace = exchange.getProperty(ProcessorConstants.DEFAULT_NS, String.class);

        repo = exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, Repository.class);

        List<Object> outputs = new ArrayList<Object>();

        try (RepositoryConnection con = repo.getConnection()) {
            Set<Value> output_classes = new HashSet<Value>(); 
           
            for (Statement s : QueryResults.asModel(con.getStatements(vf.createIRI(obj_id), RDF.TYPE, null))) {
            	output_classes.add(s.getObject());
            }
            
            for (Value output_rdf_class: output_classes) {
                String output_class = dest_standard + "." + (output_rdf_class.stringValue()).replaceAll(namespace, "");
                logger.info("[TARGET] " + output_class);
                Class c = Class.forName(output_class);

                RDFMapper aMapper = RDFMapper.builder()
                        .namespace("", namespace)
                        .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                        .build();

                Model rdfList = Connections.getRDFCollection(con, SimpleValueFactory.getInstance().createIRI(obj_id), new LinkedHashModel());
                result = aMapper.readValue(rdfList, c, SimpleValueFactory.getInstance().createIRI(obj_id));
                logger.info(ToStringBuilder.reflectionToString(result));

                outputs.add(result);
            }

            if (outputs.size() > 1)
                System.err.println("Multiple outputs found!");
            if (outputs.size() > 0) {
                result= outputs.get(0);
            }
        } catch (ClassNotFoundException e) {
            logger.info("Class not found in destination standard package");
            //e.printStackTrace();
        }

        msg.setBody(result);
        exchange.getProperty(ProcessorConstants.OBJ_CLASS, result.getClass().getName());
    }


    private void addDestinationStandard(String destinationStandard) {
        if (standards == null) {
            standards = new HashMap<>();
        }
        standards.put(destinationStandard, new FastClasspathScanner(destinationStandard).scan().getNamesOfAllClasses());
    }

}