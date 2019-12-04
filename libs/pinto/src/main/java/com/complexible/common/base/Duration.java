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

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Immutable representation of a duration.
 *
 * @author Evren Sirin
 */
public final class Duration implements Serializable {
    private static final long serialVersionUID = 1L;

	private final long durationMS;

	public Duration(long time) {
		this.durationMS = time;
	}

	public Duration(long time, TimeUnit theUnit) {
		this(theUnit.toMillis(time));
	}

	public long getMillis() {
	    return durationMS;
    }

	public long toUnit(TimeUnit theUnit) {
		return theUnit.convert(durationMS, TimeUnit.MILLISECONDS);
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public int hashCode() {
	    return (int) (durationMS ^ (durationMS >>> 32));
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public boolean equals(Object obj) {
	    return (obj instanceof Duration) && this.durationMS == ((Duration) obj).durationMS;
    }

	/**
	 * String representation of duration
	 */
    @Override
	public String toString() {
		return Durations.readable(durationMS);
	}

	/**
	 * Creates a duration instance from the string.
	 */
	public static Duration valueOf(String str) {
		return new Duration(Durations.parse(str));
	}
}
