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

package com.complexible.common.base;

import com.google.common.base.Objects;

/**
 * Tuple with 2 elements
 *
 * @author	Pedro Oliveira
 * @author	Michael Grove
 *
 * @since	2.0
 * @version	2.3
 */
public final class Pair<K1, K2> {

	public final K1 first;

	public final K2 second;

	private final int mHashCode;

	private Pair(final K1 a, final K2 b) {
		this.first = a;
		this.second = b;

		mHashCode = (first == null ? 37 : first.hashCode()) ^ (second == null ? 7 : second.hashCode());
	}

	public static <S, T> Pair<S, T> create(S a, T b) {
		return new Pair<S, T>(a, b);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		else if (o == null) {
			return false;
		}
		else if ((o instanceof Pair<?, ?>)) {
			Pair<?, ?> other = (Pair<?, ?>) o;

			return mHashCode == other.mHashCode
				&& Objects.equal(first, other.first)
				&& Objects.equal(second, other.second);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return mHashCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[" + first + " , " + second + "]";
	}
}
