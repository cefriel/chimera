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
package com.cefriel.chimera.processor.rdf4j;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import com.cefriel.chimera.context.MemoryRDFGraph;
import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.SemanticLoader;

public class DataEnricher implements Processor{

	private List<String> masterDataUrls=null;

	public void process(Exchange exchange) throws Exception {
		Model current_dataset=null;
		Repository repo=null;
		ValueFactory vf = SimpleValueFactory.getInstance();
	
		if (masterDataUrls==null)
			masterDataUrls=exchange.getProperty(ProcessorConstants.MASTER_DATA, List.class);

		repo=exchange.getProperty(ProcessorConstants.CONTEXT_GRAPH, MemoryRDFGraph.class).getRepository();

		try (RepositoryConnection con = repo.getConnection()) {

			for (String url: masterDataUrls) {
				current_dataset=SemanticLoader.load_data(url);    
				con.add(current_dataset, vf.createIRI(url));
			}
		}
	}

	public List<String> getMasterDataUrls() {
		return masterDataUrls;
	}

	public void setMasterDataUrls(List<String> masterDataUrls) {
		this.masterDataUrls = masterDataUrls;
	}
}