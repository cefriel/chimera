package it.cefriel.chimera.processor.jena;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.pinto.MappingOptions;
import com.complexible.pinto.RDFMapper;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import it.cefriel.chimera.util.JenaSesameUtils;
import it.cefriel.chimera.util.ProcessorConstants;

public class LoweringProcessor  implements Processor{
    private HashMap<String, List<String>> standards=null;
    private Logger log = LoggerFactory.getLogger(LoweringProcessor.class); 

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        String dest_standard=null;
        String obj_id=null;
        String namespace=null;
        Message msg=exchange.getIn();
        Object result=null;
        List<String> classNames=null;

        obj_id=(String) msg.getHeader(ProcessorConstants.OBJ_ID, obj_id);
        dest_standard=msg.getHeader(ProcessorConstants.DEST_STD, String.class);
        addDestinationStandard(dest_standard);    
        classNames=standards.get(msg.getHeader(ProcessorConstants.DEST_STD, String.class));
        namespace=msg.getHeader(ProcessorConstants.DEFAULT_NS, String.class);

        Model context_graph = msg.getBody(Model.class);

        List<String> transformation_targets=null;
        List<Object> outputs=new ArrayList<Object>();

        try {
            NodeIterator output_classes = context_graph.listObjectsOfProperty(context_graph.getResource(obj_id), RDF.type);
            while (output_classes.hasNext()) {
                RDFNode output_rdf_class=output_classes.next();
                String output_class=dest_standard+"."+(output_rdf_class.asResource().getURI()).replaceAll(namespace, "");
                log.info("[TARGET] "+output_class);
                Class c=Class.forName(output_class);

                org.eclipse.rdf4j.model.Model aGraph = JenaSesameUtils.asSesameGraph(context_graph);
                RDFMapper aMapper = RDFMapper.builder()
                        .namespace("", namespace)
                        .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                        .build();

                result =aMapper.readValue(aGraph, c, SimpleValueFactory.getInstance().createIRI(obj_id));
                log.info(ToStringBuilder.reflectionToString(result));

                outputs.add(result);
            }

            /*
            transformation_targets=new ArrayList<String>(classNames);
            for (String c: classNames) {
                //transformation_targets.removeAll(ReflectionUtils.getSuperClasses(Class.forName(c)));
                if (Modifier.isAbstract(this.getClass().getClassLoader().loadClass(c).getModifiers())) {
                    transformation_targets.remove(c);
                }
            }

            
            for (String output_class: transformation_targets) {
                try {
                    Object output=null;
                    Class c=Class.forName(output_class);

                    org.eclipse.rdf4j.model.Model aGraph = JenaSesameUtils.asSesameGraph(context_graph);
                    RDFMapper aMapper = RDFMapper.builder()
                            .namespace("", namespace)
                            .set(MappingOptions.SERIALIZE_COLLECTIONS_AS_LISTS, true)
                            .build();

                    result =aMapper.readValue(aGraph, c, SimpleValueFactory.getInstance().createIRI(obj_id));
                    if ((result!=null)&&(! deep_void(result))) {
                        log.debug("[SUCCESS] ID "+obj_id+" can be lowered as a "+output_class);
                        log.debug(ToStringBuilder.reflectionToString(result));
                        outputs.add(result);
                    }
                } catch (RDFMappingException re){
                    //log.info("ID "+obj_id+" cannot be lowered as a "+output_class);
                    continue;
                }
            }*/
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
        msg.setHeader(ProcessorConstants.OBJ_CLASS, result.getClass().getName());
    }


    private void addDestinationStandard(String destinationStandard) {
        if (standards==null) {
            standards=new HashMap<String, List<String>>();
        }
        standards.put(destinationStandard, new FastClasspathScanner(destinationStandard).scan().getNamesOfAllClasses());
    }

    private boolean deep_void1(Object o) throws IllegalAccessException {
        for (Field f : o.getClass().getDeclaredFields()) {
            if (! f.getType().isEnum() ) {
                f.setAccessible(true);
                if (f.get(this) != null)
                    return false;
            }
        }
        return true;            
    }

    private boolean deep_void(Object o) throws IllegalAccessException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Field[] field = o.getClass().getDeclaredFields();     

        for(int j=0 ; j<field.length ; j++){    
            String name = field[j].getName();                
            name = name.substring(0,1).toUpperCase()+name.substring(1); 
            try {
                Method m = o.getClass().getMethod("get"+name);
                if (m.invoke(o)!=null)
                    return false;
            } catch (NoSuchMethodException nse) {
                continue;
            }
        }
        return true;
    }
}