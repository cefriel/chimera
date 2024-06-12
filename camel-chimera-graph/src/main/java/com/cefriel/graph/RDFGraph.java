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

import java.util.Arrays;
import java.util.List;

public abstract class RDFGraph {
    Repository repo;
    List<IRI> namedGraphs;
    IRI baseIRI;
    public Repository getRepository() {
        return repo;
    }

    public List<IRI> getNamedGraphs() {
        return namedGraphs;
    }

    public IRI getBaseIRI() {
        return baseIRI;
    }

    public void setBaseIRI(IRI baseIRI) {
        this.baseIRI = baseIRI;
    }

    public void setNamedGraphs(String namedGraphs) {
        if (namedGraphs != null && !namedGraphs.equals("")) {
            List<IRI> graphs = Arrays.stream(namedGraphs.split(";")).map(Utils::stringToIRI).toList();
            setNamedGraphs(graphs);
        }
    }

    public void setNamedGraphs(List<IRI> namedGraphs) {
        if (!namedGraphs.isEmpty() && this.repo != null) {
            ContextAwareRepository cRep = new ContextAwareRepository(this.repo);
            cRep.setReadContexts(namedGraphs.toArray(new IRI[0]));
            cRep.setInsertContext(namedGraphs.get(0));
            this.repo = cRep;
            this.namedGraphs = namedGraphs;
        }
    }

    public void setNamedGraphs(IRI namedGraph) {
        this.setNamedGraphs(List.of(namedGraph));
    }
}
