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

import java.util.List;
import java.util.Comparator;
import java.io.Serializable;

import com.google.common.primitives.Ints;

/**
 * <p>Implementatino of a Comparator for List's of Comparable objects</p>
 *
 * @author Michael Grove
 * @since 2.1
 * @version 2.1
 */
public final class ListComparator<T extends Comparable> implements Comparator<List<T>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 949991599751842924L;

	public static <T extends Comparable> ListComparator<T> create() {
		return new ListComparator<T>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final List<T> o1, final List<T> o2) {
		int aResult = Ints.compare(o1.size(), o2.size());

		for (int i = 0; i < o1.size() && aResult == 0; i++) {
			aResult = o1.get(i).compareTo(o2.get(i));
		}

		return aResult;
	}
}
