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

package com.complexible.common.base;

import com.google.common.base.MoreObjects;
import java.util.Objects;

/**
 * <p>Class which represents a logical change to a resource.  It has an associated {@link ChangeType type}
 * as well as the {@link #getChange data that changed}.</p>
 *
 * @author  Michael Grove
 * @since   3.0
 * @version 3.0
 *
 * @see ChangeList
 * @see ChangeType
 */
public final class Change<E extends Enum & ChangeType, T> {
    /**
     * The type of the change
     */    
    private final E mChangeType;

    /**
     * The data associated with the change
     */
    private final T mChange;

    /**
     * Create a new Change
     * @param theChangeType the type of change
     * @param theChange     the change data
     */
    private Change(final E theChangeType, final T theChange) {
        mChangeType = theChangeType;
        mChange = theChange;
    }

    /**
     * Return the type of change
     * @return  the change type
     */
    public E getChangeType() {
        return mChangeType;
    }

    /**
     * Return whether or not the type of this Change is of the specified type.
     *
     * A convenience method for doing {@code aChange.getType() == SomeChangeType.AType}
     *
     * @param theType   the change type
     * @param <C>       the type of change to test
     * @return          true if this change is of the specified type, false otherwise
     */
    public <C extends Enum & ChangeType> boolean is(final C theType) {
        return mChangeType == theType;
    }

    /**
     * Return the change data
     * @return  the data
     */
    public T getChange() {
        return mChange;
    }

    /**
     * Create a new Change
     * @param theType   the type of the change
     * @param theChange the change data
     * @param <T>       the change data type
     * @param <E>       the change type
     * @return          A new change
     */
    public static <E extends Enum & ChangeType, T> Change<E, T> of(final E theType, final T theChange) {
        return new Change<E, T>(theType, theChange);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object theObj) {
        if (theObj == this) {
            return true;
        }
        else if (theObj == null) {
            return false;
        }
        else if (theObj instanceof Change) {
            Change aChange = (Change) theObj;

            return mChangeType == aChange.getChangeType()
                   && Objects.equals(mChange, aChange.getChange());
        }
        else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(mChange, mChangeType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Change")
                          .add("type", mChangeType)
                          .add("data", mChange)
                          .toString();
    }
}
