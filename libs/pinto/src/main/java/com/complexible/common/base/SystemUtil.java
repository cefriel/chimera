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

package com.complexible.common.base;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * <p>Utility methods for working with {@link System}, particularly safe, default value supported lookups into {@link System#getProperty} and other OS/system level calls.</p>
 *
 * @author Michael Grove
 * @version 2.0 (0.6.5)
 * @since 2.0
 */
public final class SystemUtil {
	/**
	 * the logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemUtil.class);

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private SystemUtil() {
		throw new AssertionError();
	}

	public static boolean getPropertyAsBoolean(final String theProperty, final boolean theDefault) {
		String aValue = System.getProperty(theProperty);
		if (aValue == null) {
			return theDefault;
		}
		else {
			try {
				return Boolean.parseBoolean(aValue);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("Value for property {} is intended to be an boolean, but the provided value {} is not a valid boolean", theProperty, theDefault);
				return theDefault;
			}
		}
	}

	public static int getPropertyAsInt(final String theProperty, final int theDefault) {
		String aValue = System.getProperty(theProperty);
		if (aValue == null) {
			return theDefault;
		}
		else {
			try {
				return Integer.parseInt(aValue);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("Value for property {} is intended to be an integer, but the provided value {} is not a valid integer", theProperty, theDefault);
				return theDefault;
			}
		}
	}

	public static long getPropertyAsLong(final String theProperty, final long theDefault) {
		String aValue = System.getProperty(theProperty);
		if (aValue == null) {
			return theDefault;
		}
		else {
			try {
				return Long.parseLong(aValue);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("Value for property {} is intended to be an long, but the provided value {} is not a valid long", theProperty, theDefault);
				return theDefault;
			}
		}
	}

	public static float getPropertyAsFloat(final String theProperty, final float theDefault) {
		String aValue = System.getProperty(theProperty);
		if (aValue == null) {
			return theDefault;
		}
		else {
			try {
				return Float.parseFloat(aValue);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("Value for property {} is intended to be an float, but the provided value {} is not a valid float", theProperty, theDefault);
				return theDefault;
			}
		}
	}

	public static double getPropertyAsDouble(final String theProperty, final double theDefault) {
		String aValue = System.getProperty(theProperty);
		if (aValue == null) {
			return theDefault;
		}
		else {
			try {
				return Double.parseDouble(aValue);
			}
			catch (NumberFormatException e) {
				LOGGER.warn("Value for property {} is intended to be an double, but the provided value {} is not a valid double", theProperty, theDefault);
				return theDefault;
			}
		}
	}
	public static String getProperty(final String theProperty, final String theDefault) {
		String aValue = System.getProperty(theProperty);

		if (aValue != null) {
			return aValue;
		}
		else {
			return theDefault;
		}
	}
}
