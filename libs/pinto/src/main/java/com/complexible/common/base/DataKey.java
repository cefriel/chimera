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

package com.complexible.common.base;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Evren Sirin
 *
 * @since 2.0
 * @version 2.0
 */
public class DataKey<E> {
	private final String name;

	protected DataKey(String name) {
		checkNotNull(name);
		
		this.name = name;
	}
	
	public static <T> DataKey<T> create(String name) {
		return new DataKey<T>(name);
	}

	@Override
	public int hashCode() {
		return 13 * name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof DataKey<?>) && name.equals(((DataKey<?>) other).name);
	}

	@Override
	public String toString() {
		return name;
	}

}
