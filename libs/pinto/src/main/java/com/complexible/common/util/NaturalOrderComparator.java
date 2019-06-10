/*
 * Copyright (c) 2005-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.util.Comparator;

/**
 * <p>Simple {@link Comparator} implementation which can be used for {@link Comparable} objects.</p>
 *
 * @author	Michael Grove
 * @since	2.3
 * @version	2.5
 */
public final class NaturalOrderComparator<T extends Comparable<T>> implements Comparator<T> {
    private NaturalOrderComparator() {
    }

    public static <T extends Comparable<T>> NaturalOrderComparator<T> create() {
        return new NaturalOrderComparator<T>();
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final T o1, final T o2) {
		return o1.compareTo(o2);
	}
}
