/*
 * Copyright (c) 2009-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.util.Set;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.Dataset;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ObjectArrays;

/**
 * <p>A {@link Dataset} implementation which is immutable</p>
 *
 * @author  Michael Grove
 * @since   1.1.1
 * @version 4.0
 */
public final class ImmutableDataset implements Dataset {
	private final ImmutableSet<IRI> mNamedGraphs;

	private final IRI mInsertURI;

	private final ImmutableSet<IRI> mRemoveGraphs;

	private final ImmutableSet<IRI> mDefaultGraphs;

	private ImmutableDataset(final ImmutableSet<IRI> theDefaultGraphs,
                             final ImmutableSet<IRI> theNamedGraphs,
                             final IRI theInsertURI,
                             final ImmutableSet<IRI> theRemoveURI) {
		mInsertURI = theInsertURI;
		mRemoveGraphs = theRemoveURI;
		mDefaultGraphs = theDefaultGraphs;
		mNamedGraphs = theNamedGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IRI> getDefaultRemoveGraphs() {
		return mRemoveGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRI getDefaultInsertGraph() {
		return mInsertURI;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IRI> getDefaultGraphs() {
		return mDefaultGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IRI> getNamedGraphs() {
		return mNamedGraphs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(mDefaultGraphs, mNamedGraphs, mInsertURI, mRemoveGraphs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object theObj) {
		if (theObj == null) {
			return false;
		}
		else if (theObj == this) {
			return true;
		}
		else if (theObj instanceof ImmutableDataset) {
			ImmutableDataset aDataset = (ImmutableDataset) theObj;
			return Objects.equal(mDefaultGraphs, aDataset.mDefaultGraphs)
				&& Objects.equal(mNamedGraphs, aDataset.mNamedGraphs)
				&& Objects.equal(mRemoveGraphs, aDataset.mRemoveGraphs)
				&& Objects.equal(mInsertURI, aDataset.mInsertURI);
		}
		else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final MoreObjects.ToStringHelper aStringHelper = MoreObjects.toStringHelper("Dataset")
		                                                            .add("defaultGraphs", mDefaultGraphs)
		                                                            .add("namedGraphs", mNamedGraphs);
		if (!mRemoveGraphs.isEmpty()) {
			aStringHelper.add("removeGraphs", mRemoveGraphs);
		}

		if (mInsertURI != null) {
			aStringHelper.add("insertURI", mInsertURI);
		}

		return aStringHelper.toString();
	}

	public static ImmutableDatasetBuilder builder() {
		return new ImmutableDatasetBuilder();
	}

	public static final class ImmutableDatasetBuilder {
		private Set<IRI> mNamedGraphs = ImmutableSet.of();

		private IRI mInsertURI = null;

		private Set<IRI> mRemoveGraphs = ImmutableSet.of();

		private Set<IRI> mDefaultGraphs = ImmutableSet.of();

		public ImmutableDataset build() {
			return new ImmutableDataset(ImmutableSet.copyOf(mDefaultGraphs),
		                                ImmutableSet.copyOf(mNamedGraphs),
		                                mInsertURI,
		                                ImmutableSet.copyOf(mRemoveGraphs));
		}

		public ImmutableDatasetBuilder insertGraph(final IRI theInsertGraph) {
			mInsertURI = theInsertGraph;
			return this;
		}

		public ImmutableDatasetBuilder defaultGraphs(final IRI theDefaultGraph, final IRI... theOtherGraphs) {
			defaultGraphs(ImmutableSet.copyOf(ObjectArrays.concat(theDefaultGraph, theOtherGraphs)));
			return this;
		}

		public ImmutableDatasetBuilder defaultGraphs(final Iterable<IRI> theDefaultGraphs) {
			mDefaultGraphs = ImmutableSet.copyOf(theDefaultGraphs);
			return this;
		}

		public ImmutableDatasetBuilder removeGraphs(final IRI theGraph, final IRI... theOtherGraphs) {
			removeGraphs(ImmutableSet.copyOf(ObjectArrays.concat(theGraph, theOtherGraphs)));
			return this;
		}

		public ImmutableDatasetBuilder removeGraphs(final Iterable<IRI> theRemoveGraphs) {
			mRemoveGraphs = ImmutableSet.copyOf(theRemoveGraphs);
			return this;
		}

		public ImmutableDatasetBuilder namedGraphs(final IRI theGraph, final IRI... theOtherGraphs) {
			namedGraphs(ImmutableSet.copyOf(ObjectArrays.concat(theGraph, theOtherGraphs)));
			return this;
		}

		public ImmutableDatasetBuilder namedGraphs(final Iterable<IRI> theNamedGraphs) {
			mNamedGraphs = ImmutableSet.copyOf(theNamedGraphs);
			return this;
		}
	}
}
