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

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.inferencer.fc.SchemaCachingRDFSInferencer;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;

import java.io.File;

public class InferenceRDFGraph extends RDFGraph {

    public InferenceRDFGraph(Repository schema, boolean allRules) {
        this(schema, null, allRules);
    }

    public InferenceRDFGraph(Repository schema, String pathDataDir, boolean allRules) {
        SchemaCachingRDFSInferencer inferencer;
        if (pathDataDir == null)
            inferencer = new SchemaCachingRDFSInferencer(new MemoryStore(), schema, allRules);
        else
            inferencer = new SchemaCachingRDFSInferencer(new NativeStore(new File(pathDataDir)),
                    schema, allRules);
        this.repo = new SailRepository(inferencer);
        this.repo.init();
    }

}
