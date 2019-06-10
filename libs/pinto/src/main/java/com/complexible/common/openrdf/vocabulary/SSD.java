/*
 * Copyright (c) 2009-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.complexible.common.openrdf.vocabulary;

import org.eclipse.rdf4j.model.IRI;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   1.1
 * @version 1.1
 */
public final class SSD extends Vocabulary {
    public SSD() {
        super("http://www.w3.org/ns/sparql-service-description#");
    }

    private static final SSD INSTANCE = new SSD();
    public static SSD ontology() {
        return INSTANCE;
    }

    public final IRI Service = term("Service");

    public final IRI Language = term("Language");
    public final IRI SPARQL11Query = term("SPARQL11Query");
    public final IRI SPARQL11Update = term("SPARQL11Update");
    public final IRI SPARQL10Query = term("SPARQL10Query");

    public final IRI Feature = term("Feature");
    public final IRI DereferencesIRIs = term("DereferencesIRIs");
    public final IRI UnionDefaultGraph = term("UnionDefaultGraph");
    public final IRI RequiresDataset = term("RequiresDataset");
    public final IRI EmptyGraphs = term("EmptyGraphs");
    public final IRI BasicFederatedQuery = term("BasicFederatedQuery");

    public final IRI EntailmentProfile = term("EntailmentProfile");
    public final IRI EntailmentRegime = term("EntailmentRegime");
    public final IRI Dataset = term("Dataset");
    public final IRI Graph = term("Graph");
    public final IRI NamedGraph = term("NamedGraph");

    public final IRI Function = term("Function");
    public final IRI Aggregate = term("Aggregate");

    public final IRI endpoint = term("endpoint");
    public final IRI feature = term("feature");
    public final IRI resultFormat = term("resultFormat");
    public final IRI defaultEntailmentRegime = term("defaultEntailmentRegime");
    public final IRI entailmentRegime = term("entailmentRegime");
    public final IRI defaultSupportedEntailmentProfile = term("defaultSupportedEntailmentProfile");
    public final IRI supportedEntailmentProfile = term("supportedEntailmentProfile");
    public final IRI extensionFunction = term("extensionFunction");
    public final IRI extensionAggregate = term("extensionAggregate");
    public final IRI languageExtension = term("languageExtension");
    public final IRI supportedLanguage = term("supportedLanguage");
    public final IRI propertyFeature = term("propertyFeature");
    public final IRI defaultDataset = term("defaultDataset");
    public final IRI availableGraphs = term("availableGraphs");
    public final IRI inputFormat = term("inputFormat");
    public final IRI defaultGraph = term("defaultGraph");
    public final IRI namedGraph = term("namedGraph");
    public final IRI name = term("name");
    public final IRI graph = term("graph");

}
