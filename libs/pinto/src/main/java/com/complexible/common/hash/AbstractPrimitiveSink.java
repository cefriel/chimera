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

import com.google.common.base.Charsets;
import com.google.common.hash.PrimitiveSink;

/**
 * <p>Abstract implementation of a {@link PrimitiveSink} whose methods all throw UnsupportedOperationException.  Intended for cases where you
 * want to implement a PrimitiveSink that will only ever accept certain types of values.</p>
 *
 * @author Michael Grove
 * @since	2.3
 * @version	2.3
 */
public abstract class AbstractPrimitiveSink implements PrimitiveSink {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putByte(final byte b) {
		throw new UnsupportedOperationException();
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
	public PrimitiveSink putBytes(final byte[] theBytes, final int theStart, final int theLength) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putShort(final short i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putInt(final int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putLong(final long theLong) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putFloat(final float v) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putDouble(final double v) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putBoolean(final boolean b) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrimitiveSink putChar(final char c) {
		throw new UnsupportedOperationException();
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
