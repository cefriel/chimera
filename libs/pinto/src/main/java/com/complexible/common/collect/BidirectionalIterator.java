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

package com.complexible.common.collect;

import java.util.Iterator;

/**
 * <p>An Iterator which can iterate back to previous elements.</p>
 *
 * @author Pavel Klinov
 *
 * @since 2.0
 * @version 2.0
 * @param <T> the type of element in the iterator
 */
public interface BidirectionalIterator<T> extends Iterator<T> {

	/**
	 * Return whether or not there is a previous element
	 * @return true if there is a previous, false otherwise
	 */
	public boolean hasPrevious();

	/**
	 * Return the previous element
	 * @return the previous element
	 */
	public T previous();
}
