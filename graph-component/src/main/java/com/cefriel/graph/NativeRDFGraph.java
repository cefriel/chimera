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
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;

public class NativeRDFGraph extends RDFGraph {

    private Sail data;
    public NativeRDFGraph(String pathDataDir) {
        File dataDir = new File(pathDataDir);
        this.data = new NativeStore(dataDir);
        this.repo = new SailRepository(data);
        this.repo.init();
    }
    public NativeRDFGraph(String pathDataDir, IRI namedGraph, IRI baseIRI) {
        this(pathDataDir);
        this.namedGraph = namedGraph;
        this.setNamedGraph(namedGraph);
        this.baseIRI = baseIRI;
    }

    public NativeRDFGraph(String pathDataDir, IRI baseIRI) {
        this(pathDataDir);
        this.baseIRI = baseIRI;
    }

    public NativeRDFGraph(String pathDataDir, String namedGraph, String baseIRI) {
        this(pathDataDir, Utils.stringToIRI(namedGraph), Utils.stringToIRI(baseIRI));
    }

    public NativeRDFGraph(String pathDataDir, String baseIRI) {
        this(pathDataDir, Utils.stringToIRI(baseIRI));
    }

    public Sail getData() {
        return data;
    }

    public void setData(Sail data) {
        this.data = data;
    }

}
