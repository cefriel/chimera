/*
 * Copyright (c) 2009-2014 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.query;

import java.util.Map;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryEvaluationException;


/**
 * <p>Base class for creating a GraphQueryResult decorator which delegates all calls to the child result.</p>
 *
 * @author  Michael Grove
 * @since   0.5
 * @version 1.0
 */
public class DelegatingGraphQueryResult implements GraphQueryResult {

	/**
	 * The result
	 */
	private final GraphQueryResult mResult;

	/**
	 * Create a new DelegatingGraphQueryResult
	 * @param theResult the result to delegate to
	 */
	protected DelegatingGraphQueryResult(final GraphQueryResult theResult) {
		mResult = theResult;
	}

	/**
	 * Returns the delegate result
	 * @return the result
	 */
	protected GraphQueryResult getResult() {
		return mResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getNamespaces() throws QueryEvaluationException {
		return mResult.getNamespaces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws QueryEvaluationException {
		mResult.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return mResult.hasNext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statement next() throws QueryEvaluationException {
		return mResult.next();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove() throws QueryEvaluationException {
		mResult.remove();
	}
}
