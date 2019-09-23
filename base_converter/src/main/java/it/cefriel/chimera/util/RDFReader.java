package it.cefriel.chimera.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public class RDFReader {
	private Repository repository;
	
	public List<Map<String,Value>> executeQuery(String query) {
		try (RepositoryConnection con = this.repository.getConnection()) {
			   TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			   List<BindingSet> resultList;
			   List<Map<String,Value>> results = new ArrayList<Map<String,Value>>();
			   try (TupleQueryResult result = tupleQuery.evaluate()) {
				   resultList = QueryResults.asList(result);
			   }	
			   for (BindingSet bindingSet : resultList) {
				   Map<String,Value> result = new HashMap<String,Value>();				   
				   for (String bindingName : bindingSet.getBindingNames()) {
					   result.put(bindingName, bindingSet.getValue(bindingName));
				   }				   
				   results.add(result);
			   }

			   return results;
			}
	}
	
	public List<Map<String, String>> executeQueryStringValue(String query) {
        try (RepositoryConnection con = this.repository.getConnection()) {
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
            List<BindingSet> resultList;
            List<Map<String,String>> results = new ArrayList<>();
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                resultList = QueryResults.asList(result);
            }
            for (BindingSet bindingSet : resultList) {
                Map<String,String> result = new HashMap<>();
                for (String bindingName : bindingSet.getBindingNames()) {
                    Value v = bindingSet.getValue(bindingName);
                    String value = (v != null) ? v.stringValue() : null ;
                    result.put(bindingName, value);
                }
                results.add(result);
            }

            return results;
        }
    }

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
