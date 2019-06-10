/*
 * Copyright (c) 2005-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.hash;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.PrimitiveSink;

/**
 * <p>An implementation of {@link PrimitiveSink} which has a number of 'child' sinks to which each call to this Sink is
 * delegated.  Intended to make it easy to multiplex results to a list of sinks.</p>
 *
 * @author	Michael Grove
 * @since	2.3
 * @version	2.3
 */
public final class ConcatenatedPrimitiveSink<T extends PrimitiveSink> implements PrimitiveSink, Iterable<T> {
	private final ImmutableList<T> mSinks;

	public ConcatenatedPrimitiveSink(final T theSink, final T... theOtherSinks) {
		mSinks = ImmutableList.<T>builder()
			.add(theSink)
			.add(theOtherSinks)
			.build();
	}

	public ConcatenatedPrimitiveSink(final List<T> theSinks) {
		mSinks = ImmutableList.copyOf(theSinks);
	}

	/**
	 * Return the list of child sinks
	 *
	 * @return	the child sinks
	 */
	public ImmutableList<T> getSinks() {
		return mSinks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<T> iterator() {
		return mSinks.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putBoolean(final boolean theBool) {
		for (PrimitiveSink aSink : this) {
			aSink.putBoolean(theBool);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putByte(final byte theByte) {
		for (PrimitiveSink aSink : this) {
			aSink.putByte(theByte);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putBytes(final byte[] theBytes) {
		return putBytes(theBytes, 0, theBytes.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putBytes(final byte[] theBytes, final int theOffset, final int theLength) {
		for (int i = 0; i < theLength; i++) {
			putByte(theBytes[theOffset + i]);
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putShort(final short theShort) {
		for (PrimitiveSink aSink : this) {
			aSink.putShort(theShort);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putInt(final int theInt) {
		for (PrimitiveSink aSink : this) {
			aSink.putInt(theInt);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putLong(final long theLong) {
		for (PrimitiveSink aSink : this) {
			aSink.putLong(theLong);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putFloat(final float theFloat) {
		for (PrimitiveSink aSink : this) {
			aSink.putFloat(theFloat);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putDouble(final double theDouble) {
		for (PrimitiveSink aSink : this) {
			aSink.putDouble(theDouble);
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putChar(final char theChar) {
		for (PrimitiveSink aSink : this) {
			aSink.putChar(theChar);
		}
		return this;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public PrimitiveSink putUnencodedChars(final CharSequence theCharSequence) {
        return putString(theCharSequence, Charsets.UTF_8);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putString(final CharSequence theCharSequence, final Charset theCharset) {
		return putBytes(theCharSequence.toString().getBytes(theCharset));
	}
}
