/*
 * Copyright 2018 Cefriel.
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

package it.cefriel.chimera.processor.rdf4j;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import it.cefriel.chimera.context.RDFGraph;
import it.cefriel.chimera.util.ProcessorConstants;
import it.cefriel.chimera.util.SemanticLoader;

public class SecuredDataEnricher implements Processor{

	private List<String> masterDataUrls=null;

	public void process(Exchange exchange) throws Exception {
		Model current_dataset=null;
		Repository repo=null;
		ValueFactory vf = SimpleValueFactory.getInstance();
		Message in = exchange.getIn();
		
		if (masterDataUrls==null)
			masterDataUrls=in.getHeader(ProcessorConstants.MASTER_DATA, List.class);

		repo=in.getHeader(ProcessorConstants.CONTEXT_GRAPH, RDFGraph.class).getRepository();
		String token=in.getHeader(ProcessorConstants.JWT_TOKEN, String.class);

		try (RepositoryConnection con = repo.getConnection()) {

			for (String url: masterDataUrls) {
				current_dataset=SemanticLoader.load_data(url, token);    
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