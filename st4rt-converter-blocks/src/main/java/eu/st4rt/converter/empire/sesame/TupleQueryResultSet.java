/*
 * Copyright (c) 2009-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.sesame;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import eu.st4rt.converter.empire.ds.impl.AbstractResultSet;

/**
 * <p>Simple extension of the {@link AbstractResultSet} to provide iteration over a Sesame 2.x result set and to
 * close the results when completed.</p>
 *
 * @author  Michael Grove
 * @since   0.6
 * @version 1.0
 */
public final class TupleQueryResultSet extends AbstractResultSet {
	private final TupleQueryResult mResults;

	public TupleQueryResultSet(final TupleQueryResult theResults) {
		super(Iterations.stream(theResults).iterator());

		mResults = theResults;
	}

	/**
	 * @inheritDoc
	 */
    @Override
	public void close() {
		try {
			mResults.close();
		}
		catch (QueryEvaluationException e) {
            // todo: better error handling than this
			e.printStackTrace();
		}
	}
}
