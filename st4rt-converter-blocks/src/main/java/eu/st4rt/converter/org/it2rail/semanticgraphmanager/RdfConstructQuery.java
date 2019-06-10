package eu.st4rt.converter.org.it2rail.semanticgraphmanager;

import st4rt.convertor.empire.annotation.NamedGraph;

import com.complexible.common.openrdf.model.Models2;
import com.complexible.common.openrdf.model.Statements;

import eu.st4rt.converter.empire.QueryFactory;
import eu.st4rt.converter.empire.annotation.InvalidRdfException;
import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.impl.RdfQuery;
import eu.st4rt.converter.empire.util.BeanReflectUtil;
import eu.st4rt.converter.org.it2rail.empire.rdf4jdatasouce.InMemoryRepositoryDataSource;
import eu.st4rt.converter.org.it2rail.empire.rdf4jdatasouce.InMemoryRepositoryDataSourceFactory;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;


import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class RdfConstructQuery {
	
	private static RdfConstructQuery instance = null;
	
	private static InMemoryRepositoryDataSource scratchDs = null;
	
	private RdfConstructQuery(QueryFactory queryFactory) throws DataSourceException, ConnectException {
		scratchDs = InMemoryRepositoryDataSourceFactory.create(queryFactory);
		scratchDs.connect();
		 
	}
	
	public static RdfConstructQuery getInstance(QueryFactory queryFactory) throws DataSourceException, ConnectException {
		if(instance == null)
			instance = new RdfConstructQuery(queryFactory);
		return instance;
	}
	
	public static <T> List<T> getConstructQueryResults(IRI filterProperty, RdfQuery query ) throws DataSourceException, InvalidRdfException {
		Class<T> aClass = query.getBeanClass();
		if(aClass == null)
			throw new InvalidRdfException("RdfConstructQuery results: No bean defined for RdfQuery");
		else return getConstructQueryResults(aClass, filterProperty,query);
	}
	
	public static <T> List<T>  getConstructQueryResults(Class<T> theClass, IRI filterProperty, RdfQuery query) throws DataSourceException, InvalidRdfException {
				
		if(filterProperty == null)
			throw new InvalidRdfException("No unique filter property specified for class "+theClass.getSimpleName());
		List<T> _return = new ArrayList<T>();
		
		NamedGraph namedGraph = BeanReflectUtil.getAnnotation(theClass, NamedGraph.class);
		 
				
		Iterator<LinkedHashModel> iter = query.getResultList().iterator();
		while(iter.hasNext()) {
			LinkedHashModel aModel = iter.next();
			List<Resource> ids = new ArrayList<Resource>();
			
			ids = aModel.stream().filter(Statements.predicateIs(filterProperty)).map(Statement::getSubject).collect(Collectors.toList());
			 
			Model theModel = Models2.newModel(aModel.stream().iterator());
			if(namedGraph!= null)
				scratchDs.add(URI.create(namedGraph.value()), theModel);
			else 
				scratchDs.add(theModel);
			for(Resource id : ids) {
				_return.add((T) RdfGenerator.fromRdf(theClass,id.stringValue(), scratchDs));
			}
			scratchDs.remove(theModel);
		}
		
		return _return;
		
	}
	
	public static void close() {
		scratchDs.disconnect();
	}
	

}
