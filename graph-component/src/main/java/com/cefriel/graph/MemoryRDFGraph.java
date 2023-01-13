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
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class MemoryRDFGraph extends RDFGraph {

    private Sail data;

    public MemoryRDFGraph() {
        this.data = new MemoryStore();
        this.repo = new SailRepository(data);
        this.repo.init();
    }
    public MemoryRDFGraph(IRI namedGraph, IRI baseIRI) {
        this();
        this.namedGraph = namedGraph;
        this.setNamedGraph(namedGraph);
        this.baseIRI = baseIRI;
    }
    public MemoryRDFGraph(IRI baseIRI) {
        this();
        this.baseIRI = baseIRI;
    }

    public MemoryRDFGraph(String namedGraph, String baseIRI) {
        this(Utils.stringToIRI(namedGraph), Utils.stringToIRI(baseIRI));
    }
    public MemoryRDFGraph( String baseIRI) {
        this(Utils.stringToIRI(baseIRI));
    }
    public Sail getData() {
        return data;
    }

    public void setData(Sail data) {
        this.data = data;
    }

}
