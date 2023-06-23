/*
 * Copyright (c) 2019-2022 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cefriel.graph;

import com.cefriel.util.Utils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.repository.contextaware.ContextAwareRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;


public class SPARQLEndpointGraph extends RDFGraph {

    public SPARQLEndpointGraph(String endpoint) {
        repo = new SPARQLRepository(endpoint);
        repo.init();
        // TODO Federation with FedX, requires rdf4j 3.1.0
    }

    public SPARQLEndpointGraph(String endpoint, IRI namedGraph, IRI baseIRI) {
        this.baseIRI = baseIRI;
        this.namedGraph = namedGraph;
        ContextAwareRepository cRepo = new ContextAwareRepository(new SPARQLRepository(endpoint));
        cRepo.setReadContexts(namedGraph);
        cRepo.setInsertContext(namedGraph);
        cRepo.init();
        this.repo = cRepo;
    }

    public SPARQLEndpointGraph(String endpoint, IRI baseIRI) {
        this(endpoint);
        this.baseIRI = baseIRI;
    }

    public SPARQLEndpointGraph(String endpoint, String namedGraph, String baseIRI) {
        this(endpoint, Utils.stringToIRI(namedGraph), Utils.stringToIRI(baseIRI));
    }

    public SPARQLEndpointGraph(String endpoint, String baseIRI) {
        this(endpoint, Utils.stringToIRI(baseIRI));
    }

}
