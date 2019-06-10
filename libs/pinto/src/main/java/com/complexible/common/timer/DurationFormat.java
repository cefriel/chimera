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

package com.complexible.common.timer;

/**
 * <p>A simple class to provide various formatting options for durations represented in
 * milliseconds. The durations
 * are displayed in terms of hours, minutes, seconds, and milliseconds.
 * </p>
 * 
 * @author Evren Sirin
 * @since 2.0
 * @version 2.0
 */
public enum DurationFormat {
	/**
	 * Format duration in full format. Example: 0 hour(s) 2 minute(s) 13 second(s) 572 milliseconds(s)
	 */
	FULL( "%d hour(s) %d minute(s) %d second(s) %d milliseconds(s)", true ),
	/**
	 * Format duration in long format. Example: 00:02:13.572
	 */
	LONG( "%02d:%02d:%02d.%03d", true ), 
	/**
	 * Format duration in medium format (no milliseconds). Example: 00:02:13
	 */
	MEDIUM( "%02d:%02d:%02d", true ),
	/**
	 * Format duration in short format (no hours or milliseconds). Example: 00:02
	 */
	SHORT( "%2$02d:%3$02d", false );
	
	private String formatString;
	private boolean hoursVisible;
	
	DurationFormat(String formatString, boolean hoursVisible) {
		this.formatString = formatString;
		this.hoursVisible = hoursVisible;
	}
	
	/**
	 * Format the given duration in milliseconds according to the style defined by this
	 * DurationFormat class. 
	 * 
	 * @param durationInMilliseconds duration represented in milliseconds
	 * @return duration formatted as a string
	 */
	public String format(long durationInMilliseconds) {
        long hours, minutes, seconds, milliseconds;
        
        if( hoursVisible ) {
	        hours = durationInMilliseconds / 3600000;	        
	        durationInMilliseconds = durationInMilliseconds - (hours * 3600000);
        }
        else {
        	hours = 0;
        }
        
        minutes = durationInMilliseconds / 60000;
        durationInMilliseconds = durationInMilliseconds - (minutes * 60000);
        
        seconds = durationInMilliseconds / 1000;
        milliseconds = durationInMilliseconds - (seconds * 1000);

        return String.format( formatString, hours, minutes, seconds, milliseconds);
	}
}
