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

import com.google.common.collect.PeekingIterator;
import com.google.common.collect.UnmodifiableIterator;

/**
 * <p>Class which adapts an array of objects to the Iterator interface, particularly to allow sub-arrays to be iterated over.</p>
 *
 * @param <T> the type of elements that will be iterated over
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 5.0
 */
public final class ArrayIterator<T> extends UnmodifiableIterator<T> implements PeekingIterator<T> {
	private final T[] mArray;

	private final int mEnd;
	private int mPos = 0;

	/**
	 * Create a new ArrayIterator
	 * @param theArray the array to iterate over
	 */
	public ArrayIterator(final T[] theArray) {
		this(theArray, 0, theArray.length);
	}

	/**
	 * Create a new ArrayIterator
	 * @param theArray the array to iterate over
	 * @param theStart the starting position of the array
	 * @param theEnd the end position of the array
	 */
	public ArrayIterator(final T[] theArray, final int theStart, final int theEnd) {
		mArray = theArray;
		mEnd = theEnd;
		mPos = theStart;
	}

	@SafeVarargs
	public static <T> ArrayIterator<T> create(final T... theElements) {
		return new ArrayIterator<>(theElements);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return mPos < mEnd;
	}

	/**
	 * {@inheritDoc}
	 */
	public T next() {
		return mArray[mPos++];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public T peek() {
	    return mArray[mPos];
    }
}
