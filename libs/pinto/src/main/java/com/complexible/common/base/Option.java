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
 * Represents a configuration option. A configuration option is used as a key to get and set values from a
 * {@link Options} structure. In addition, an option has a predefined default value.
 * 
 * @author Evren Sirin
 * 
 * @since 2.0
 * @version 2.0
 */
public final class Option<V> {
	private final String name;
	private V defaultValue;

	protected Option(String name, V defaultValue) {
		checkNotNull(name);

		this.name = name;
		this.defaultValue = defaultValue;
	}

	/**
	 * Creates an option with the given name and <code>null</code> default value.
	 */
	public static <T> Option<T> create(String name) {
		return new Option<T>(name, null);
	}

	/**
	 * Creates an option with the given name and default value.
	 */
	public static <T> Option<T> create(String name, T defaultValue) {
		return new Option<T>(name, defaultValue);
	}

	/**
	 * Returns the default value for this option.
	 */
	public V getDefaultValue() {
		return defaultValue;
	}
		
	/**
	 * Sets the default value for this option. This will have a global effect.
	 */
	public void setDefaultValue(V defaultValue) {
    	this.defaultValue = defaultValue;
    }

	@Override
	public int hashCode() {
		return 17 * name.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof Option<?>) && name.equals(((Option<?>) other).name);
	}

	@Override
	public String toString() {
		return name;
	}
}
