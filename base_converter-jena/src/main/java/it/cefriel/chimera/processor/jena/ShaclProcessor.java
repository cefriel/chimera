package it.cefriel.chimera.processor.jena;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.jenax.progress.SimpleProgressMonitor;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.vocabulary.SH;

import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.UniLoader;

public class ShaclProcessor  implements Processor{
    private Logger log = LoggerFactory.getLogger(ShaclProcessor.class);
    private Model shaclModel = null;
    private String shacl_url= null;

    
    @SuppressWarnings("unchecked")
	public void process(Exchange exchange) throws Exception {
        List<String> shacl_urls=null;
        Message msg=exchange.getIn();
        Model main_model=null;
        Model output= null;
        Model temp_model = JenaUtil.createMemoryModel();

        shacl_urls=msg.getHeader(ProcessorConstants.SHACL_RULES, List.class);
        
        shaclModel=createIfNull(shaclModel);
        
        for (String url: shacl_urls) {
            read_shacl(url);            
        }
        /*
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
                dataset.getGraph(),
                ontologies.getGraph(),
                temp_model.getGraph()
        });*/
        main_model=msg.getBody(Model.class);
        log.info("Message body type: "+msg.getBody().getClass());
        //log.info("Shacl rules: "+shaclModel);
        
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
                main_model.getGraph(),
                temp_model.getGraph()
        });
        unionGraph.setBaseGraph(temp_model.getGraph());

        Model data = ModelFactory.createModelForGraph(unionGraph);
                

        output=RuleUtil.executeRules(data, shaclModel, temp_model, new SimpleProgressMonitor("converter"));
        
        main_model.add(output);
        msg.setBody(main_model);
        log.info("OUTPUT: "+output);

        log.info("TEMP: "+temp_model);

        FileOutputStream of=new FileOutputStream("/tmp/"+main_model.hashCode()+"-shacl_output.n3");
        output.write(of, "TURTLE");
        of.close();
        
    }
    private Model createIfNull(Model m) {
        if (m==null)
            return JenaUtil.createMemoryModel();
        else
            return m;
    }
    
    public String getShacl_url() {
        return shacl_url;
    }
    
    public void setShacl_url(String shacl_url) {
        this.shacl_url = shacl_url;
        read_shacl(shacl_url);
    }

    private void read_shacl(String shacl_url) {
        if (shaclModel==null) {
            shaclModel=JenaUtil.createDefaultModel();    
        }
        try {
            shaclModel.read(UniLoader.open(shacl_url), SH.BASE_URI, FileUtils.langTurtle);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
