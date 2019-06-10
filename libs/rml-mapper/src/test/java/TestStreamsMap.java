import java.io.FileInputStream;
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

public class TestStreamsMap {

	public static void main(String[] args) {
		//String cwd = "/home/rml"; //path to default directory for local files
        String mappingFile = "C:\\Users\\fiano\\Documents\\CEFRIEL\\Progetti\\Sprint\\Software\\RMLTC0001a-CSV Stream\\mapping.ttl"; //path to the mapping file that needs to be executed
        
        try {
            InputStream mappingStream = new FileInputStream(mappingFile);
            Model model = Rio.parse(mappingStream, "", RDFFormat.TURTLE);
            RDF4JStore rmlStore = new RDF4JStore(model);

            Map<String, InputStream> streamsMap = new HashMap<String, InputStream>();
            
            
            streamsMap.put("stream://student.csv", new FileInputStream("C:\\Users\\fiano\\Documents\\CEFRIEL\\Progetti\\Sprint\\Software\\RMLTC0001a-CSV Stream\\student.csv"));
            
            Executor executor = new Executor(rmlStore, new RecordsFactory(streamsMap), "http://example.org#");
            QuadStore result = executor.execute(null);            
            
            System.out.println(result);
            
//            for (Statement statement: model) {
//            	System.out.println(statement); 
//            } 
        
        } catch (Exception e) {
        	e.printStackTrace();
//            System.out.println(e.getMessage());
        }

	}

}