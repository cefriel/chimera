package eu.st4rt.converter.org.it2rail.semanticgraphmanager;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Rdf4jResourceXMLAdapter extends XmlAdapter<String, Resource> {

	@Override
	public String marshal(Resource arg0) throws Exception {
		 
		return arg0.stringValue();
	}

	@Override
	public Resource unmarshal(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return SimpleValueFactory.getInstance().createIRI(arg0);
	}
	
	

}
