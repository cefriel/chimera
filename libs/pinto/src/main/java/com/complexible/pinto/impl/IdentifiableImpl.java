/*
 * Copyright (c) 2015 Complexible Inc. <http://complexible.com>
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

package com.complexible.pinto.impl;

import org.eclipse.rdf4j.model.Resource;

import com.complexible.pinto.Identifiable;

/**
 * <p>Default implementation of {@link Identifiable}</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 2.0.1
 */
public final class IdentifiableImpl implements Identifiable {
	private Resource mId;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource id() {
		return mId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void id(final Resource theResource) {
		mId = theResource;
	}
}
