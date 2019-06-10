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

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * An object that maps keys to values in a type-safe manner. A key cannot be mapped to multiple values. Each key
 * specifies the Java Object allowed for the values of the key. Neither null keys nor null values are allowed.
 * 
 * @author Evren Sirin
 * 
 * @version 2.0
 * @since 2.0
 */
public class DataMap {
	private final Map<DataKey<Object>, Object> data = Maps.newHashMap();

	private DataMap() {
	}

	/**
	 * Create a new empty data map.
	 */
	public static DataMap of() {
		return new DataMap();
	}

	/**
	 * Create a new data map with the given single mapping.
	 */
	public static <V> DataMap of(DataKey<V> key, V value) {
		return of().set(key, value);
	}

	/**
	 * Create a new data map with the given two mapping.
	 */
	public static <V1, V2> DataMap of(DataKey<V1> key1, V1 value1, DataKey<V2> key2, V2 value2) {
		return of().set(key1, value1).set(key2, value2);
	}

	/**
	 * Create a new data map with the given three mappings.
	 */
	public static <V1, V2, V3> DataMap of(DataKey<V1> key1, V1 value1, DataKey<V2> key2, V2 value2, DataKey<V3> key3,
	                V3 value3) {
		return of().set(key1, value1).set(key2, value2).set(key3, value3);
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 * key.
	 * 
	 * @param <V>
	 *            Allowed types for the specified key
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 *         key
	 */
	@SuppressWarnings("unchecked")
	public <V> V get(DataKey<V> key) {
		return (V) data.get(key);
	}

	/**
	 * Associates the specified value with the specified key in this map. If the map previously contained a mapping for
	 * the key, the old value is replaced by the specified value.
	 * 
	 * @param <V>
	 *            allowed types for this key
	 * @param key
	 *            the key to identify the data value
	 * @param value
	 *            the new data value to associate with the key
	 * @return this data map for method chaining
	 */
	@SuppressWarnings("unchecked")
	public <V> DataMap set(DataKey<V> key, V value) {
		data.put((DataKey<Object>) key, value);
		return this;
	}

	public <V> boolean contains(DataKey<V> key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	public <V> V remove(DataKey<V> key) {
		return (V) data.remove(key);
	}
}
