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
// Repository creation procedure based on https://henrietteharmse.com/2018/04/09/creating-a-remote-repository-for-graphdb-with-rdf4j-programmatically/
package com.cefriel.chimera.graph;

import com.cefriel.chimera.util.ProcessorConstants;
import com.cefriel.chimera.util.UniLoader;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.config.RepositoryConfigSchema;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

import java.io.IOException;
import java.io.InputStream;

public class HTTPRDFGraph implements RDFGraph {

    private Repository repo;
    private String DB_ADDRESS;
    private String REPOSITORY_ID;

    private String repoConfigPath;

    public HTTPRDFGraph(String address, String repoId) throws IOException {
        DB_ADDRESS = address;
        if (repoId == null) {
            REPOSITORY_ID = ProcessorConstants.DEFAULT_REPOSITORY_ID;
            RepositoryManager rm = RepositoryProvider.getRepositoryManager(DB_ADDRESS);
            rm.init();

            // Read repository configuration file
            TreeModel graph = new TreeModel();
            InputStream config;
            if(repoConfigPath != null)
                config = UniLoader.open(repoConfigPath);
            else
                config = UniLoader.open(ProcessorConstants.DEFAULT_REPO_CONFIG_FILE);
            RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
            rdfParser.setRDFHandler(new StatementCollector(graph));
            rdfParser.parse(config, RepositoryConfigSchema.NAMESPACE);
            config.close();

            // Retrieve the repository node as a resource
            Resource repositoryNode =  Models.subject(graph
                    .filter(null, RDF.TYPE, RepositoryConfigSchema.REPOSITORY))
                    .orElseThrow(() -> new RuntimeException(
                            "No <http://www.openrdf.org/config/repository#> subject found!"));

            // Create a repository configuration object and add it to the repositoryManager
            RepositoryConfig repositoryConfig = RepositoryConfig.create(graph, repositoryNode);
            rm.addRepositoryConfig(repositoryConfig);

            // Get the repository from repository manager, note the repository id is set in configuration .ttl file
            repo = rm.getRepository(REPOSITORY_ID);
        } else {
            REPOSITORY_ID = repoId;
            repo = new HTTPRepository(address, repoId);
            repo.init();
        }
    }

    @Override
    public Repository getRepository() {
        return repo;
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    public String getRepoConfigPath() {
        return repoConfigPath;
    }

    public void setRepoConfigPath(String repoConfigPath) {
        this.repoConfigPath = repoConfigPath;
    }

}