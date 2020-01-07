package it.cefriel.chimera.context;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class RDFGraph {
	private Sail data=null;
	private Sail schema=null;
	private Repository repository=null;
	
	public RDFGraph() {
		data = new MemoryStore();
		repository = new SailRepository(data);
		repository.init();
	}

	public Sail getData() {
		return data;
	}

	public void setData(Sail data) {
		this.data = data;
	}

	public Sail getSchema() {
		return schema;
	}

	public void setSchema(Sail schema) {
		this.schema = schema;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}


	
}
