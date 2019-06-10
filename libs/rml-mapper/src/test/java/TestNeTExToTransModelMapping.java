import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import be.ugent.rml.Executor;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.RDF4JStore;

public class TestNeTExToTransModelMapping {

	public static void main(String[] args) {
//		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		String basePath = "./src/test/resources/mapping/NeTEx/";
        String sourceFile = "authority.xml"; //path to the input file
        String mappingFile = "mapping.ttl"; //path to the mapping file
        String destinationFile = "output.ttl"; //path to output file
        String baseIRI = "http://foo.org/gtfs/"; //prefix of the subject
        
        
        try {
            InputStream mappingStream = new FileInputStream(basePath+mappingFile);
            Model model = Rio.parse(mappingStream, "", RDFFormat.TURTLE);
            RDF4JStore rmlStore = new RDF4JStore(model);

            Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();          
                        
            streamsMap.put("stream://"+sourceFile, new FileInputStream(basePath+sourceFile));            
            
            Executor executor = new Executor(rmlStore, new RecordsFactory(streamsMap), baseIRI);
            QuadStore result = executor.execute(null);            

//          System.out.println(result);
            
            FileOutputStream fout=new FileOutputStream(basePath+destinationFile);    
            fout.write(result.toString().getBytes());    
            fout.close();

        } catch (Exception e) {
//          System.out.println(e.getMessage());
        	e.printStackTrace();
        }

	}

}