package it.cefriel.chimera.processor.rdf4j;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import eu.st4rt.converter.empire.annotation.InvalidRdfException;
import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.sesame.RepositoryDataSourceFactory;
import eu.st4rt.converter.empire.sesame.RepositoryFactoryKeys;
import eu.st4rt.converter.util.XMLUtil;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;


public class St4rtLoweringProcessor implements Processor{
    private static final Logger LOGGER = LoggerFactory.getLogger(St4rtLoweringProcessor.class);
    private String destinationPackage= null;
    
	public void process(Exchange exchange) throws Exception {
		Repository repo=null;
		Message in = exchange.getIn();
		Map<String, Object> aConfig = new HashMap<String, Object>();
		
		repo=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
		String msg_id=in.getHeader(ProcessorConstants.MSG_ID, String.class);

		aConfig.put("factory", "sesame");
		aConfig.put("name", "s4rt");
		aConfig.put(RepositoryFactoryKeys.REPO_HANDLE, repo);
		
		RepositoryDataSourceFactory empire_repo_factory = new RepositoryDataSourceFactory();
		DataSource empireDataSource = empire_repo_factory.create(aConfig);
		List<String> classNames = new FastClasspathScanner(destinationPackage)
                .scan()
                .getNamesOfAllClasses();
		for (String className: classNames) {
			Class<?> outputClazz = Class.forName(className);
			Object convertedObject = find(outputClazz, msg_id, empireDataSource);
			String result = XMLUtil.convertObjectToXML(convertedObject, outputClazz);
			if (result!=null)
				in.setBody(result);
			break;
		}
	}

    public static <O> Object find(Class<O> clazz, String rdfKey, DataSource dataSource) {
        //  return aManager.find(clazz, rdfKey.toString());
        O result = null;
        try {
            result = RdfGenerator.fromRdf(clazz, rdfKey, dataSource,Maps.newHashMap());
        } catch (DataSourceException | InvalidRdfException e) {
        	LOGGER.debug(e.getMessage());
        }
        return result;
    }

	public String getDestinationPackage() {
		return destinationPackage;
	}

	public void setDestinationPackage(String destinationPackage) {
		this.destinationPackage = destinationPackage;
	}

}
