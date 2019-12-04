/*
 * Copyright (c) 2005-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.util.NoSuchElementException;

import com.google.common.collect.PeekingIterator;

/**
 * Abstract implementation of {@link PeekingIterator} interface.
 * 
 * @author Evren Sirin
 */
public abstract class AbstractPeekingIterator<T> implements PeekingIterator<T> {
	private T mNext = null;

	protected abstract T fetchNext();
	
	protected void clearNext() {
		mNext = null;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public T next() {
		T aResult = peek();
		mNext = null;
		return aResult;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public T peek() {
		if (mNext == null) {
			mNext = fetchNext();
		}
		if (mNext == null) {
			throw new NoSuchElementException();
		}
		return mNext;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public boolean hasNext() {
		if (mNext == null) {
			mNext = fetchNext();
		}
		return mNext != null;
	}
}
