/*
 * Copyright (c) 2005-2016 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.io;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;

/**
 * <p>Basic rotation strategy for specifying a rotation policy based on time intervals.</p>
 *
 * @author  Michael Grove
 * @since   1.0.1
 * @version 1.0.1
 */
public final class TimeRotationStrategy implements FileRotationStrategy {
    /**
     * Duration of the interval in ms
     */
    private final long mInterval;

    /**
     * Create a new DateRotationStrategy with the specified interval given in milliseconds.
     */
    public TimeRotationStrategy(final long theMilliseconds) {
        mInterval = theMilliseconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needsRotation(final File theFile) {
        return needsRotation(theFile.lastModified(), System.currentTimeMillis());
    }

    public long getInterval() {
        return mInterval;
    }

    @VisibleForTesting
    public boolean needsRotation(final long theLastModifiedTime, final long theTime) {
        // check which interval the time belongs to rather than how much time has passed between two timepoints
        // so we rotate files at even times (e.g. beginning of the hour if the interval is one hour)
        final long now = theTime / mInterval;
        final long lastWrite = theLastModifiedTime / mInterval;
        return now > lastWrite;
    }

    /**
     * Creates a new {@link TimeRotationStrategy} with the specified interval.
     *
     * The interval value should be a positive integer followed by either letter 'd' (for days) or letter 'h' (for hours).
     * Examples intervals are '7d' for weekly logs, '1d' for daily logs, '12h' for two log files per day, and '1h' for
     * hourly logs.
     */
    public static TimeRotationStrategy create(final String theInterval) {
        return new TimeRotationStrategy(parseDH(theInterval));
    }

    /**
     * Parses day or hour duration
     */
    private static long parseDH(String theStr) {
        int aLastIndex = theStr.length() - 1;
        TimeUnit aUnit = getTimeUnit(theStr.charAt(aLastIndex));

        return parseDuration(theStr.substring(0, aLastIndex), aUnit);
    }

    private static TimeUnit getTimeUnit(char theCh) {
        switch (Character.toLowerCase(theCh)) {
            case 'd': return TimeUnit.DAYS;
            case 'h': return TimeUnit.HOURS;
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Parse interval represented in H:mm:ss format.
     */
    @SuppressWarnings("unused")
    private static long parseHMS(String theStr) {
        String[] theFields = theStr.split("\\:");
        if (theFields.length != 3) {
            throw new IllegalArgumentException();
        }

        return parseDuration(theFields[0], TimeUnit.HOURS)
               + parseDuration(theFields[1], TimeUnit.MINUTES)
               + parseDuration(theFields[2], TimeUnit.SECONDS);
    }

    private static long parseDuration(String theStr, TimeUnit theUnit) {
        int aVal = Integer.parseInt(theStr);
        if (aVal < 0) {
            throw new IllegalArgumentException();
        }
        return theUnit.toMillis(aVal);
    }
}
