/*
 * Copyright (c) 2009-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.query;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;


/**
 * <p>An immutable {@link BindingSet}</p>
 *
 * @author  Michael Grove
 * @since   1.1
 * @version 1.1
 */
public final class ImmutableBindingSet extends DelegatingBindingSet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5607633429367933757L;
	private final ImmutableSet<String> mBindingNames;

    public ImmutableBindingSet(final BindingSet theBindingSet) {
        super(theBindingSet);
        mBindingNames = ImmutableSet.copyOf(theBindingSet.getBindingNames());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Iterator<Binding> iterator() {
        return Iterators.unmodifiableIterator(super.iterator());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<String> getBindingNames() {
        return mBindingNames;
    }
}
