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

import org.eclipse.rdf4j.query.QueryResult;

/**
 * <p>An analog to {@link org.openrdf.query.TupleQueryResult} and {@link org.openrdf.query.GraphQueryResult},
 * this represents the result to a Boolean/Ask query.  Normally this has been represented as a single primitive
 * boolean result value.  Using a result based object here while inconvenient in some cases is preferable in
 * the cases where you want to treat all result types as the same, that is, now all queries return something
 * that is a {@link QueryResult}.  Further, since it's no longer a primitive value, it opens up the door for
 * letting you set a timeout and/or kill a query via the returned result object, something you cannot do when
 * its *just* a boolean primitive.</p>
 *
 * <p>This object will return the result of a boolean query.  There will always be a single result;
 * {@link #hasNext} is guaranteed to return a <code>true</code> value the first time.  This single value
 * will be the boolean result of the query.  So you can always call {@link #next} on one of these results
 * without checking {@link #hasNext} if its the first call to next.</p>
 *
 * @author	Michael Grove
 * @since	0.8
 * @version	0.8
 */
public interface BooleanQueryResult extends QueryResult<Boolean> {

}
