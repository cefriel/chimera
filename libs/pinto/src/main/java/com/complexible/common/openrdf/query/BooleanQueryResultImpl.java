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

package com.complexible.common.openrdf.query;

import java.util.NoSuchElementException;

import org.eclipse.rdf4j.query.QueryEvaluationException;


/**
 * <p>Default implementation of a {@link BooleanQueryResult}</p>
 *
 * @author	Michael Grove
 * @since	0.8
 * @version 0.8
 */
public final class BooleanQueryResultImpl implements BooleanQueryResult {
	private final boolean mResult;

	private boolean mHasNext = true;

	public BooleanQueryResultImpl(final boolean theResult) {
		mResult = theResult;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void close() throws QueryEvaluationException {
		// no-op
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return mHasNext;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Boolean next() throws QueryEvaluationException {
		if (mHasNext) {
			mHasNext = false;
			return mResult;
		}
		else {
			throw new NoSuchElementException();
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove() throws QueryEvaluationException {
		throw new UnsupportedOperationException("Cannot remove a query result");
	}
}
