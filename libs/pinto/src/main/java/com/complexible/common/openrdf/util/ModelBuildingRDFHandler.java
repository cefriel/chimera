/*
 * Copyright (c) 2009-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

import com.complexible.common.openrdf.model.Models2;

/**
 * <p>Implementation of an RDFHandler which collects statements from the handler events and puts them into a Graph object.</p>
 *
 * @author  Michael Grove
 * @since   0.1
 * @version 4.0
 */
public final class ModelBuildingRDFHandler extends AbstractRDFHandler {

	/**
	 * The graph to collect statements in
	 */
	private final Model mGraph;

	/**
	 * Create a new GraphBuildingRDFHandler
	 */
	public ModelBuildingRDFHandler() {
		this(Models2.newModel());
	}

	/**
	 * Create a new GraphBuildingRDFHandler that will insert statements into the supplied Graph
	 * @param theGraph the graph to insert into
	 */
	public ModelBuildingRDFHandler(final Model theGraph) {
		mGraph = theGraph;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleStatement(final Statement theStatement) throws RDFHandlerException {
		mGraph.add(theStatement);
	}

	/**
	 * Return the graph built from events fired to this handler
	 * @return the graph
	 */
	public Model getModel() {
		return mGraph;
	}

	/**
	 * Clear the underlying graph of all collected statements
	 */
	public void clear() {
		mGraph.clear();
	}
}
