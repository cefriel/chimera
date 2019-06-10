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
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;

/**
 * <p>Base class for creating a term factory for an ontology or schema.</p>
 *
 * @author  Michael Grove
 * @since   0.1
 * @version 4.0
 */
public class Vocabulary {
    protected final ValueFactory FACTORY;

    private String mURI;

    public Vocabulary(String theURI) {
		this(theURI, ValueFactoryImpl.getInstance());
	}

	public Vocabulary(String theURI, final ValueFactory theValueFactory) {
        mURI = theURI;
		FACTORY = theValueFactory;
    }

    public IRI term(String theName) {
        return FACTORY.createIRI(mURI + theName);
    }

    public java.net.URI uri() {
        return java.net.URI.create(mURI);
    }
}