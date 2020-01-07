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
package com.cefriel.chimera.context;

import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;

public class HTTPRDFGraph implements RDFGraph {

    private HTTPRepository repo;
    private final String DB_ADDRESS;
    private final String REPOSITORY_ID;

    public HTTPRDFGraph(String address, String repoId) {
        DB_ADDRESS = address;
        REPOSITORY_ID = repoId;
        repo = new HTTPRepository(address, repoId);
        repo.init();
    }

    @Override
    public Repository getRepository() {
        return repo;
    }

    public String getAddress() {
        return DB_ADDRESS;
    }

    public String getRepositoryID() {
        return REPOSITORY_ID;
    }
}
