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
import com.google.common.hash.Funnel;
import com.google.common.hash.Hasher;

/**
 * <p>An abstract Hasher implementation derived from the Guava AbstractHasher, which is packaged private, while this is public
 * making it accessible for all implementors.</p>
 *
 * @author	Michael Grove
 * @since	2.3
 * @version	2.3
 */
public abstract class AbstractHasher implements Hasher {
	/**
	 * {@inheritDoc}
	 */

	@Override
	public Hasher putBoolean(boolean b) {
		return putByte(b ? (byte) 1 : (byte) 0);
	}
	/**
	 * {@inheritDoc}
	 */

	@Override
	public Hasher putDouble(double d) {
		return putLong(Double.doubleToRawLongBits(d));
	}
	/**
	 * {@inheritDoc}
	 */

	@Override
	public Hasher putFloat(float f) {
		return putInt(Float.floatToRawIntBits(f));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hasher putBytes(final byte[] theBytes) {
		return putBytes(theBytes, 0, theBytes.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hasher putString(final CharSequence theCharSequence, final Charset theCharset) {
		return putBytes(theCharSequence.toString().getBytes(theCharset));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Hasher putUnencodedChars(final CharSequence theCharSequence) {
		return putString(theCharSequence, Charsets.UTF_8);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Hasher putObject(final T theT, final Funnel<? super T> theFunnel) {
		theFunnel.funnel(theT, this);
		return this;
	}
}
