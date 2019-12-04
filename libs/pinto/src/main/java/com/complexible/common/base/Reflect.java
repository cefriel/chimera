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

import java.lang.reflect.Constructor;

import com.google.common.base.Optional;

/**
 * <p>A series of basic Reflect based utility functions.  In most cases, exceptions thrown by the Reflect API methods
 * are caught and ignored choosing to return an {@link com.google.common.base.Optional} with no value instead.</p>
 *
 * @author  Michael Grove
 * @since   2.2.1
 * @version 2.2.1
 */
public final class Reflect {

	/**
	 * No instances
	 */
	private Reflect() {
		throw new AssertionError();
	}

	/**
	 * Returns the class with the given name.  A ClassNotFoundException will not be thrown if
	 * the class does not exist, an {@link com.google.common.base.Optional} with an absent value will be returned instead.
	 * Similarly, if a ClassCastException is thrown, because the class name is valid, but is
	 * not of the correct type of class requested by the user, an Optional without a value will be returned..
	 *
	 * @param theClassName	the class name
	 * @return				the class, or null if it does not exist
	 */
	public static <T> Optional<Class<T>> getClass(final String theClassName) {
		try {
			return Optional.of((Class<T>) Class.forName(theClassName));
		}
		catch (ClassNotFoundException e) {
			return Optional.absent();
		}
		catch (ClassCastException e) {
			return Optional.absent();
		}
	}

	/**
	 * Get the constructor of the class with the given arguments.  NoSuchMethodException is not
	 * thrown if a suitable constructor does not exist, an {@link Optional} without a value is
	 * returned.
	 *
	 * @param theClass		the class to get the constructor of
	 * @param theArgTypes	the arguments to the constructor
	 * @param <T>			the class type
	 * @return				the constructor, an Optional with an absent value if the constructor
	 * 						does not exist
	 */
	public static <T> Optional<Constructor<T>> getConstructor(final Class<T> theClass, final Class<?>... theArgTypes) {
		try {
			return Optional.of(theClass.getConstructor(theArgTypes));
		}
		catch (NoSuchMethodException e) {
			return Optional.absent();
		}
	}
}
