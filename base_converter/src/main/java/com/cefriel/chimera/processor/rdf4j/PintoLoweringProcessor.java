package it.cefriel.chimera.processor.rdf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.complexible.pinto.MappingOptions;
import com.complexible.pinto.RDFMapper;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import it.cefriel.chimera.util.ProcessorConstants;

public class PintoLoweringProcessor  implements Processor{
    private HashMap<String, List<String>> standards=null;
    private Logger log = LoggerFactory.getLogger(PintoLoweringProcessor.class); 

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        String dest_standard=null;
        String obj_id=null;
        String namespace=null;
        Message msg=exchange.getIn();
        Object result=null;
        List<String> classNames=null;
        Repository repo=null;

        ValueFactory vf = SimpleValueFactory.getInstance();

        obj_id=(String) exchange.getProperty(ProcessorConstants.OBJ_ID, obj_id);
        dest_standard=exchange.getProperty(ProcessorConstants.DEST_STD, String.class);
        addDestinationStandard(dest_standard);    
        classNames=standards.get(exchange.getProperty(ProcessorConstants.DEST_STD, String.class));
        namespace=exchange.getProperty(ProcessorConstants.DEFAULT_NS, String.class);

        repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, Repository.class);

        List<Object> outputs=new ArrayList<Object>();

        try (RepositoryConnection con = repo.getConnection()) {
            Set<Value> output_classes = new HashSet<Value>(); 
           
            for (Statement s: QueryResults.asModel(con.getStatements(vf.createIRI(obj_id), RDF.TYPE, null))) {
            	output_classes.add(s.getObject());
            }
            
            for (Value output_rdf_class: output_classes) {
                String output_class=dest_standard+"."+(output_rdf_class.stringValue()).replaceAll(namespace, "");
                log.info("[TARGET] "+output_class);
                Class c=Class.forName(output_class);

                RDFMapper aMapper = RDFMapper.builder()
                        .namespace("", namespace)
                        .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                        .build();

                Model rdfList = Connections.getRDFCollection(con, SimpleValueFactory.getInstance().createIRI(obj_id), new LinkedHashModel());
                result =aMapper.readValue(rdfList, c, SimpleValueFactory.getInstance().createIRI(obj_id));
                log.info(ToStringBuilder.reflectionToString(result));

                outputs.add(result);
            }

            if (outputs.size()>1)
                System.err.println("Multiple outputs found!");
            if (outputs.size()>0) {
                result= outputs.get(0);
            }
        } catch (ClassNotFoundException e) {
            log.info("Class not found in destination standard package");
            //e.printStackTrace();
        }

        msg.setBody(result);
        exchange.getProperty(ProcessorConstants.OBJ_CLASS, result.getClass().getName());
    }


    private void addDestinationStandard(String destinationStandard) {
        if (standards==null) {
            standards=new HashMap<String, List<String>>();
        }
        standards.put(destinationStandard, new FastClasspathScanner(destinationStandard).scan().getNamesOfAllClasses());
    }

}