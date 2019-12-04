/*
 * Copyright (c) 2009-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.ds;

import org.eclipse.rdf4j.query.BindingSet;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>Simple stub interface for a set of results to a select query.  This is considered to be an
 * {@link Iterator} set of {@link BindingSet} objects.  BindingSet objects are map-like structures where the keys
 * are the names elements of the projection of the query and the values are the bindings for that particular
 * query solution.</p>
 *
 * <p>Even though this implements Iterator, it is not expected for the remove method to have any result on the
 * result set, or on the DataSource the results came from, the most likely implementation of the method is a no-op</p>
 *
 * @author  Michael Grove
 * @since   0.1
 * @version 0.7
 */
public interface ResultSet extends Iterator<BindingSet>, AutoCloseable {

	/**
	 * Close this result set and release any resources it holds.
	 */
	@Override
	public void close();

	public default Stream<BindingSet> stream() {
		return StreamSupport.stream(Spliterators.spliterator(this, Spliterator.SIZED, Spliterator.IMMUTABLE | Spliterator.ORDERED), false);
	}
}
