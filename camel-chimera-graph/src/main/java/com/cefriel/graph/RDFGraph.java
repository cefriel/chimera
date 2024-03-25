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
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.contextaware.ContextAwareRepository;

public abstract class RDFGraph {
    Repository repo;
    IRI namedGraph;
    IRI baseIRI;
    public Repository getRepository() {
        return repo;
    }

    public IRI getNamedGraph() {
        return namedGraph;
    }

    public IRI getBaseIRI() {
        return baseIRI;
    }

    public void setBaseIRI(IRI baseIRI) {
        this.baseIRI = baseIRI;
    }

    public void setNamedGraph(String namedGraph) {
        if (namedGraph != null && !namedGraph.equals("")) {
            setNamedGraph(Utils.stringToIRI(namedGraph));
        }
    }

    public void setNamedGraph(IRI namedGraph) {
        this.namedGraph = namedGraph;
        if (this.repo != null) {
            ContextAwareRepository cRep = new ContextAwareRepository(this.repo);
            cRep.setReadContexts(namedGraph);
            cRep.setInsertContext(namedGraph);
            this.repo = cRep;
        }
    }
}
