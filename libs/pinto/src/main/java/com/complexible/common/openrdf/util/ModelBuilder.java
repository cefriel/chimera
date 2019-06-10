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

package com.complexible.common.openrdf.util;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import com.complexible.common.openrdf.model.Models2;


/**
 * <p>Utility class for creating a set of statements using {@link ResourceBuilder ResourceBuilders}.</p>
 *
 * @author	Michael Grove
 * @since	0.1
 * @version 4.0
 */
public class ModelBuilder {
    private final Model mGraph;
    private final ValueFactory mValueFactory;

    public ModelBuilder() {
        mGraph = Models2.newModel();
        mValueFactory = SimpleValueFactory.getInstance();
    }

    public ModelBuilder(final ValueFactory theValueFactory) {
	    mGraph = Models2.newModel();
        mValueFactory = theValueFactory;
    }

	/**
	 * Return the Graph created via this builder
	 *
	 * @return  the graph
	 */
    public Model model() {
        return Models2.newModel(mGraph);
    }

	/**
	 * Clear the contents of the builder
	 */
    public void reset() {
        mGraph.clear();
    }

	/**
	 * Create a {@link ResourceBuilder builder} for the individual
	 * @param theURI    the individual
	 * @return          the {@link ResourceBuilder builder}
	 */
    public ResourceBuilder iri(IRI theURI) {
        return new ResourceBuilder(mGraph, getValueFactory(), getValueFactory().createIRI(theURI.toString()));
    }

	/**
	 * Create a {@link ResourceBuilder builder} for the individual
	 * @param theURI    the individual
	 * @return          the {@link ResourceBuilder builder}
	 */
    public ResourceBuilder iri(String theURI) {
        return instance(null, theURI);
    }

	/**
	 * Create a new anonymous instance of the given type
	 *
	 * @param theType   the type
	 *
	 * @return          a {@link ResourceBuilder builder} for the new individual
	 */
    public ResourceBuilder instance(IRI theType) {
        return instance(theType, (String) null);
    }

	/**
	 * Create an un-typed anonymous individual in the graph
	 *
	 * @return a ResourceBuilder wrapping the bnode
	 */
	public ResourceBuilder instance() {
		return instance(null, (String) null);
	}


	/**
	 * Create a {@link ResourceBuilder builder} for the given individual and add the type
	 *
	 * @param theType   the type
	 * @param theURI    the individual
	 *
	 * @return          a {@link ResourceBuilder builder} for the new individual
	 */
	public ResourceBuilder instance(IRI theType, java.net.URI theURI) {
		return instance(theType, theURI.toString());
	}

	/**
	 * Create a {@link ResourceBuilder builder} for the given individual and add the type
	 *
	 * @param theType   the type
	 * @param theRes    the individual
	 *
	 * @return          a {@link ResourceBuilder builder} for the new individual
	 */
	public ResourceBuilder instance(IRI theType, Resource theRes) {
		if (theType != null) {
			mGraph.add(theRes, RDF.TYPE, theType);
		}

		return new ResourceBuilder(mGraph, getValueFactory(), theRes);
	}
	/**
	 * Create a {@link ResourceBuilder builder} for the given individual and add the type
	 *
	 * @param theType   the type
	 * @param theURI    the individual
	 *
	 * @return          a {@link ResourceBuilder builder} for the new individual
	 */
    public ResourceBuilder instance(IRI theType, String theURI) {
        Resource aRes = theURI == null
                        ? getValueFactory().createBNode()
                        : getValueFactory().createIRI(theURI);

        if (theType != null) {
            mGraph.add(aRes, RDF.TYPE, theType);
        }

        return new ResourceBuilder(mGraph, getValueFactory(), aRes);
    }

	public ValueFactory getValueFactory() {
		return mValueFactory;
	}
}
