/*
 * Copyright (c) 2005-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.collect;

import java.util.Iterator;

/**
 * <p>Adapter class for using an Array as an Iterable, particularly, for allowing parts of an array to be the iterable.  Does not make a copy
 * of the array, so changes to the array will affect the Iterable and Iterators created from it.</p>
 *
 * @author Michael Grove
 * @since 2.0
 * @version 2.0
 */
public final class ArrayIterable<T> implements Iterable<T> {
	private final T[] mArray;
	private final int mStart;
	private final int mEnd;

	/**
	 * Create a new ArrayIterator
	 * @param theArray the array to iterate over
	 */
	public ArrayIterable(final T[] theArray) {
		this(theArray, 0, theArray.length);
	}

	/**
	 * Create a new ArrayIterator
	 * @param theArray the array to iterate over
	 * @param theStart the starting position of the array
	 * @param theEnd the end position of the array
	 */
	public ArrayIterable(final T[] theArray, final int theStart, final int theEnd) {
		mArray = theArray;
		mStart = theStart;
		mEnd = theEnd;
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<T> iterator() {
		return new ArrayIterator<T>(mArray, mStart, mEnd);
	}
}
