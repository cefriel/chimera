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

package com.cefriel.operations;

import com.cefriel.graph.RDFGraph;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GraphAdd {

    private static final Logger LOG = LoggerFactory.getLogger(GraphAdd.class);

    public static void graphAdd(RDFGraph graph, Model model) throws IOException {
        Repository repo = graph.getRepository();
        try (RepositoryConnection con = repo.getConnection()) {
            con.add(model);
            for (Namespace ns : model.getNamespaces()) {
                con.setNamespace(ns.getPrefix(), ns.getName());
            }
        }
        LOG.info(model.size() + " triples added to the graph");
    }

}
