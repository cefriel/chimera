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

import java.io.StringWriter;

/**
 * 
 * @author Evren Sirin
 * @since 2.0
 * @version 2.0
 */
public class GlobalTimer {
	public static final Timers timers = new Timers();
	
	public static Timer get(String name) {
		return timers.createTimer(name);
	}
	
	public static Timer start(String name) {
		return timers.startTimer(name);
	}
	
	public static Timer stop(String name) {
		Timer timer = get(name);
		timer.stop();
		return timer;
	}
	
	public static String format(long ms) {
		return DurationFormat.MEDIUM.format(ms);
	}
	
	public static String info() {
		StringWriter sw = new StringWriter();
		timers.print(sw);
		return sw.toString();
	}
}
