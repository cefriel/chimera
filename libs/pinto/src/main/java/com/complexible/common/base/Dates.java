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

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * <p>Utility methods for working with the Date object</p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 4.0
 */
public final class Dates {

	private Dates() {
	}

	/**
	 * Formats the given string as a java.util.Date object.
	 * @param theDate the date string
	 * @return the string as a java.util.Date object
	 */
	public static Date asDate(String theDate) {

		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("MM dd yyyy HH:mm:ss").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("MM/dd/yyyy hh:mm").parse(theDate);
		}
		catch (ParseException pe) {
		}
		try {
			return new SimpleDateFormat("MM/dd/yy hh:mm").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("MM/dd/yyyy").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("MM/dd/yy").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("yyyy-MM-dd").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("MM-dd-yyyy").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("yyyy/MM/dd").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new SimpleDateFormat("dd-MMM-yy").parse(theDate);
		}
		catch (ParseException pe) {
		}

		try {
			return new Date(Long.parseLong(theDate));
		}
		catch (Exception pe) {
		}

		throw new IllegalArgumentException("Invalid date format supplied: " + theDate);
	}

	public static String date(Date theDate) {
		return new SimpleDateFormat("yyyy-MM-dd").format(theDate);
	}

	public static String datetime(Date theDate) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(theDate);
	}

	public static String datetimeISO(Date theDate) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(theDate);
	}
}
