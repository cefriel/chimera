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
 * <p>Term constants for the DC ontology</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
public class DC extends Vocabulary {
	private static final DC VOCAB = new DC();
	
    private DC() {
        super("http://purl.org/dc/elements/1.1/");
    }
	
    public static DC ontology() {
        return VOCAB;
    }
	
    public final IRI title = term("title");
    public final IRI creator = term("creator");
    public final IRI subject = term("subject");
    public final IRI description = term("description");
    public final IRI contributor = term("contributor");
    public final IRI date = term("date");
    public final IRI type = term("type");
    public final IRI format = term ("format");
    public final IRI identifier = term("identifier");
    public final IRI source = term("source");
    public final IRI language = term("language");
    public final IRI relation = term("relation");
    public final IRI coverage = term("coverage");
    public final IRI rights = term("rights");
	public final IRI publisher = term("publisher");
}