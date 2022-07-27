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

import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;


public class SPARQLEndpointGraph extends RDFGraph {

    public SPARQLEndpointGraph(String endpoint) {
        repo = new SPARQLRepository(endpoint);
        repo.init();
        // TODO Federation with FedX, requires rdf4j 3.1.0
    }

}
