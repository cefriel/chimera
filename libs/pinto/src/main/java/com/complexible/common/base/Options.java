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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

/**
 * @author Evren Sirin
 * 
 * @since 2.0
 * @version 2.2
 */
public class Options implements Iterable<Option<Object>>, Copyable<Options> {
	private static Options EMPTY = new Options(Collections.<Option<Object>, Object>emptyMap());
	
	private final Map<Option<Object>, Object> options;

	Options() {
		this(Maps.<Option<Object>, Object>newHashMap());
	}

	Options(Options theOptions) {
		this(theOptions.options);
	}
	
	private Options(Map<Option<Object>, Object> theMap) {
		this.options = theMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Option<Object>> iterator() {
		return options.keySet().iterator();
	}

	/**
	 * Creates a mutable copy of the options instance. Updating the copy will not affect the original options. The copy
	 * can be modified even if the original options instance may not be modifiable.
	 */
	public Options copy() {
		return new Options(Maps.newHashMap(options));
	}

	/**
	 * Creates a new empty options instance.
	 */
	public static Options create() {
		return new Options();
	}

	/**
	 * Creates a new options instance with the given single mapping.
	 */
	public static <V> Options of(Option<V> key, V value) {
		return create().set(key, value);
	}

	/**
	 * Creates a new options instance with the given two mappings.
	 */
	public static <V1, V2> Options of(Option<V1> key1, V1 value1, Option<V2> key2, V2 value2) {
		return create().set(key1, value1).set(key2, value2);
	}

	/**
	 * Creates a new options instance with the given three mappings.
	 */
	public static <V1, V2, V3> Options of(Option<V1> key1, V1 value1, Option<V2> key2, V2 value2, Option<V3> key3,
	                V3 value3) {
		return create().set(key1, value1).set(key2, value2).set(key3, value3);
	}
	
	/**
	 * Returns <tt>true</tt> if this collection contains a value for the specified option.
	 */
	public <V> boolean contains(Option<V> option) {
		return options.containsKey(option);
	}

	/**
	 * Returns the value associated with the given option or the default value of the option if there is no associated
	 * value. The default value for an option might be <code>null</code> so there are cases this function will return
	 * <code>null</code> values. Must be used with care in autoboxing. 
	 * 
	 * @see Options#is(Option)  
	 */
	@SuppressWarnings("unchecked")
	public <V> V get(Option<V> option) {
		Object value = options.get(option);
		return value != null ? (V) value : option.getDefaultValue();
	}

	/**
	 * Returns the value associated with the given boolean option or the default value of the option if there is no associated
	 * value and the default value is not <code>null</code> or <code>false</code> otherwise.
	 */
	public boolean is(Option<Boolean> option) {
		Boolean value = (Boolean) options.get(option);
		if (value == null)
			value = option.getDefaultValue();
		return value != null && value;
	}
	
	/**
	 * Associate the given value with the given option overriding any previous value.
	 */
	@SuppressWarnings("unchecked")
	public <V> Options set(Option<V> option, V value) {
		options.put((Option<Object>) option, value);
		return this;
	}

	/**
	 * Copies all of the option value mappings from the specified Options overriding any previous value.
	 * .
	 * @return a reference to this object to allow method chaining
	 */
	public Options setAll(Options theOptions) {
		options.putAll(theOptions.options);
		return this;
	}

	/**
	 * Removes any previous value associated with this option.
	 */
	@SuppressWarnings("unchecked")
	public <V> V remove(Option<V> option) {
		Object oldValue = options.remove(option);
		return oldValue != null ? (V) oldValue : option.getDefaultValue();
	}

	/**
	 * Inserts the given options into the existing set of options.  Options will only be added if they
	 * do not already have a value specified in the existing options.
	 * @param theOptions			the options to insert into
	 * @param theOptionsToInsert	the new options to insert
	 */
	@SuppressWarnings("unchecked")
	public static void insert(final Options theOptions, final Options theOptionsToInsert) {
		for (Option aOpt : theOptionsToInsert) {
			if (!theOptions.contains(aOpt)) {
				theOptions.set(aOpt, theOptionsToInsert.get(aOpt));
			}
		}
	}
	
	/**
	 * Combines the given multiple options instances into one options instance. If there are duplicate options in the given
	 * arguments, the value that appears in the last options instance override any previous value.
	 */
	public static Options combine(Options... theOptionsArray) {
		Options aResult = Options.create();
		for (Options aOptions : theOptionsArray) {
			aResult.setAll(aOptions);
		}
		return aResult;
	}

	/**
	 * Creates an unmodifiable, shallow copy of the options instance. The unmodifiable copy cannot be updated directly
	 * but since it is a shallow copy updating the copy. If this needs to be avoided
	 * <code>Options.unmodifiable(Options.copy())</code> can be used.
	 * 
	 * @return a new options instance that will throw exceptions on modification function
	 */
	public static Options unmodifiable(Options options) {
		return new Options(Collections.unmodifiableMap(options.options));
	}

	/**
	 * Creates a new empty <i>immutable</i> Options instance.
	 */
	public static Options empty() {
		return EMPTY;
	}

	/**
	 * Creates an <i>immutable</i> option instance with the given single mapping.
	 */
	public static <V> Options singleton(Option<V> key, V value) { 
		return new Options(Collections.singletonMap((Option<Object>) key, (Object) value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final Options aOptions = (Options) o;

		return Objects.equal(options, aOptions.options);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return options != null
			   ? options.hashCode()
			   : 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public String toString() {
	    return options.toString();
    }

	public int size() {
		return options.size();
	}
}
