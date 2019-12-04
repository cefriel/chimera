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

import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;



/**
 * <p>Base class for creating a TupleQueryResult decorator which delegates all calls to its child TupleQueryResult</p>
 *
 * @author  Michael Grove
 * @since   0.5
 * @version 1.0
 */
public class DelegatingTupleQueryResult implements TupleQueryResult {

	/**
	 * The inner result
	 */
	private final TupleQueryResult mResult;

	/**
	 * Create a new DelegatingTupleQueryResult
	 * @param theResult the TupleQueryResult to delegate all calls to
	 */
	protected DelegatingTupleQueryResult(final TupleQueryResult theResult) {
		mResult = theResult;
	}

	/**
	 * Return the TupleQueryResult this object wraps
	 * @return the result
	 */
	protected TupleQueryResult getResult() {
		return mResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getBindingNames() throws QueryEvaluationException {
		return mResult.getBindingNames();
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
	public BindingSet next() throws QueryEvaluationException {
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
