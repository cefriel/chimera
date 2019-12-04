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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Math-related utils
 * @since 2.0 (0.1)
 * @version 2.0
 */
public final class Math {

	private Math() {
		throw new AssertionError();
	}
	
	/**
	 * Returns the base 10 logarithm of a {@link BigInteger}.
	 */
	public static double log10(final BigInteger theValue) {
		/*
		 * log10(x) = (x / (10 ^ len(x))) + len(x)
		 * http://ubuntuforums.org/showthread.php?t=1461903
		 */
		final int length = theValue.toString().length();	
		BigDecimal div = BigDecimal.TEN.pow(length);
		BigDecimal res = new BigDecimal(theValue).divide(div);
		return java.lang.Math.log10(res.doubleValue()) + length;
	}

	public static double log2(final double theValue) {
		return java.lang.Math.log(theValue) /java.lang.Math.log(2);
	}
}
