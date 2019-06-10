/*
 * Copyright (c) 2005-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.sql.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

/**
 * Immutable representation of date time.
 *
 * @author Evren Sirin
 */
public final class DateTime {
	public static DateTime now() {
		return new DateTime(System.currentTimeMillis());
	}

	private final long dateTime;

	public DateTime(long time) {
		this.dateTime = time;
	}

	public long getTime() {
	    return dateTime;
    }

	public Date toDate() {
		return new Date(dateTime);
	}

	public GregorianCalendar toCalendar() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(toDate());
		return calendar;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public int hashCode() {
	    return (int) (dateTime ^ (dateTime >>> 32));
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public boolean equals(Object obj) {
	    return (obj instanceof DateTime) && this.dateTime == ((DateTime) obj).dateTime;
    }

	/**
	 * String representation of date time in XML schema format.
	 */
    @Override
	public String toString() {
		return DatatypeConverter.printDateTime(toCalendar());
	}

	/**
	 * Creates a DateTime instance from XML schema serialization of xsd:dateTime.
	 */
	public static DateTime valueOf(String str) {
		return new DateTime(DatatypeConverter.parseDateTime(str).getTime().getTime());
	}
}
