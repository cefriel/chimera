/*
 * Copyright (c) 2019-2021 Cefriel.
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
package com.cefriel.chimera.graph;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;

public abstract class RDFGraph {

    Repository repo;
    IRI context;

    public Repository getRepository() {
        return repo;
    }

    public IRI getContext() {
        return context;
    }

    public void setContext(String context) {
        if (context != null) {
            ValueFactory vf = SimpleValueFactory.getInstance();
            this.context = vf.createIRI(context);
        }
        this.context = null;
    }

}
