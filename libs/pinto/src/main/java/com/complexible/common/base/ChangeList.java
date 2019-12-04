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

package com.complexible.common.base;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * <p>An ordered list of {@link Change changes} to some resource.</p>
 *
 * @author  Michael Grove
 * @since 	3.0
 * @version 3.0
 *
 * @param <T> the type of the change objects
 */
public final class ChangeList<E extends Enum & ChangeType, T> implements Iterable<Change<E, T>> {

    /**
     * The list of changes
     */
	private final List<Change<E, T>> mChanges = Lists.newArrayList();

    /**
     * Private constructor, use {@link #create()}
     */
	private ChangeList() {
	}

	/**
	 * Create a new list of Changes
	 * @param <T>	the type of payload in the changes
	 * @return		the new ChangeList
	 */
	public static <T, E extends Enum & ChangeType> ChangeList<E, T> create() {
		return new ChangeList<E, T>();
	}

	/**
	 * Clear all changes
	 */
	public void clear() {
		mChanges.clear();
	}

	/**
	 * Add a new Change to the current list of changes
	 *
	 * @param theChange	the change to add
     *
     * @return          this ChangeList
	 */
	public ChangeList<E, T> append(final Change<E, T> theChange) {
		mChanges.add(theChange);
        return this;
	}

    /**
     * Add all the Changes to the current list of changes
     *
     * @param theChange	the change to add
     *
     * @return          this ChangeList
     */
    public ChangeList<E, T> appendAll(final Iterable<Change<E, T>> theChange) {
        Iterables.addAll(mChanges, theChange);

        return this;
    }

    /**
     * Add a new Change to the current list of changes.
     *
     * @param theType   the type of change
     * @param theChange the change payload
     *
     * @return          this ChangeList
     */
    public ChangeList<E, T> append(final E theType, final T theChange) {
   		mChanges.add(Change.of(theType, theChange));
        return this;
   	}

	/**
	 * Return the number of changes in this change set
	 * @return	the number of changes
	 */
	public int size() {
		return mChanges.size();
	}

    /**
     * Return whether or not the ChangeList is empty
     * @return  true if there are no changes in this list, false otherwise
     */
    public boolean isEmpty() {
        return mChanges.isEmpty();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Change<E, T>> iterator() {
		return Iterators.unmodifiableIterator(mChanges.iterator());
	}
}
