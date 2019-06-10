/*
 * Copyright (c) 2005-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * <p>A simple Tuple object.  Trades strict type safety for flexibility.  If you want a type-safe tuple you
 * have to define a specific Tuple class for a 1-tuple, for a 2-tuple, for a 3-tuple ... up to n-tuple.  This does
 * not really scale.  Most likely your uses will fall into a small enough range you could make a class for each, but
 * this gets most of the joy without having to write lots of classes for each size.</p>
 *
 * @author Michael Grove
 */
public class Tuple implements Iterable<Object> {

	/**
	 * The tuple data.
	 */
	private List<Object> mData = new ArrayList<Object>();

	/**
	 * Whether or not to squelch possible ClassCastExceptions thrown from using get() with the wrong type.
	 */
	private boolean mSafe = true;

	/**
	 * Create a new Tuple
	 * @param theData the tuple data
	 */
	public Tuple(List<?> theData) {
		mData.addAll(theData);
	}

	/**
	 * Create a new Tuple
	 * @param theData the tuple data
	 */
	public Tuple(Object... theData) {
		mData.addAll(Arrays.asList(theData));
	}

	/**
	 * Create a new Tuple
	 * @param theSafe true to enable safe mode, false otherwise
	 * @param theData the tuple data
	 */
	public Tuple(boolean theSafe, Object... theData) {
		mData.addAll(Arrays.asList(theData));
		mSafe = theSafe;
	}

	/**
	 * Return the Tuple value at the given index.  This will return the value as whatever it's being assigned to:
	 * {@code
	 *   Integer aInt = aTuple.get(2);
	 * }
	 *
	 * Or you can explicitly request the type:
	 *
	 * {@code
	 *   return aTuple.<Boolean>get(0);
	 * }
	 *
	 * This is done by default in "safe" mode.  Safe mode will catch the possible ClassCastException and return null
	 * instead.  You can disable safe mode to get the 
	 * @param theIndex the indexed value of the tuple to retrieve
	 * @param <T> the type the return value should be
	 * @return the value at the index, or null if it's not the right type and safe mode is enabled
	 * @throws ClassCastException thrown if you ask for a tuple element with a given type and it cannot be casted to
	 * that value (when safe mode is off).
	 * @throws IndexOutOfBoundsException if you ask for a tuple element that does not exist
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(int theIndex) {
		try {
			return (T) mData.get(theIndex);
		}
		catch (ClassCastException e) {
			if (mSafe) {
				return null;
			}
			else {
				throw e;
			}
		}
	}

	/**
	 * Return the length of this tuple.
	 * @return the length
	 */
	public int length() {
		return mData.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<Object> iterator() {
		return mData.iterator();
	}
}
