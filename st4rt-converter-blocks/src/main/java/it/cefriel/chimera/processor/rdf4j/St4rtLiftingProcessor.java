package it.cefriel.chimera.processor.rdf4j;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.st4rt.converter.empire.annotation.InvalidRdfException;
import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.annotation.SupportsRdfId;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.sesame.RepositoryDataSourceFactory;
import eu.st4rt.converter.empire.sesame.RepositoryFactoryKeys;
import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;


public class St4rtLiftingProcessor extends SemanticLoader implements Processor{
    private static final Logger LOGGER = LoggerFactory.getLogger(St4rtLiftingProcessor.class);

	public void process(Exchange exchange) throws Exception {
		Repository repo=null;
		ValueFactory vf = SimpleValueFactory.getInstance();
		Message in = exchange.getIn();
		SupportsRdfId objectToConvert = in.getBody(SupportsRdfId.class);
		Map<String, Object> aConfig = new HashMap<String, Object>();
		
		repo=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
		
		aConfig.put("factory", "sesame");
		aConfig.put("name", "s4rt");
		aConfig.put(RepositoryFactoryKeys.REPO_HANDLE, repo);
		
		RepositoryDataSourceFactory empire_repo_factory = new RepositoryDataSourceFactory();
		DataSource empireDataSource = empire_repo_factory.create(aConfig);
		
		String id="http://start.eu#"+objectToConvert.hashCode();
        Model mainModel = insertData(objectToConvert, createRdfKey(id), empireDataSource);
		try (RepositoryConnection con = repo.getConnection()) {
			con.add(mainModel, vf.createIRI(id));
		}
		in.setHeader(ProcessorConstants.MSG_ID, id);
	}

    public SupportsRdfId.RdfKey createRdfKey(String id) {
        return new SupportsRdfId.URIKey(URI.create(id));
    }

    private Model insertData(SupportsRdfId data, SupportsRdfId.RdfKey rdfKey, DataSource aSource) {
    	Model output = null;
        data.setRdfId(rdfKey);

        try {
             
            output=RdfGenerator.asRdf(data, aSource);
        } catch (InvalidRdfException e) {
        	LOGGER.debug(e.getMessage());
        }

        return output;
    }


}
