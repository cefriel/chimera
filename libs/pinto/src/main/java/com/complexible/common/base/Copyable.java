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

/**
 * <p>Interface for an object that can be copied.</p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 2.0
 */
public interface Copyable<T extends Copyable<T>> {

	/**
	 * Create a deep copy of the object which does not share any references with the original.
	 * @return a copy of the object
	 */
	public T copy();
}
