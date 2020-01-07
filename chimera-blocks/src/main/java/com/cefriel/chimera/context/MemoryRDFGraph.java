/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.context;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class MemoryRDFGraph implements RDFGraph {

	private Sail data;
	private Sail schema;
	private SailRepository repository;
	
	public MemoryRDFGraph() {
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

	@Override
	public Repository getRepository() {
		return repository;
	}

}
