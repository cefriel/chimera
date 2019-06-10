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

package com.complexible.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * This class provides a customizable formatter implementation for <code>java.util.logging</code> (JUL). The
 * customization of log message is done via setting the style property for this class in the configuration file.
 * 
 * Example configuration looks like this:
 * <pre>
 *   java.util.logging.ConsoleHandler.formatter = com.clarkparsia.utils.CustomizableJULFormatter
 *   com.clarkparsia.utils.CustomizableJULFormatter.style = NONE
 * </pre>
 * 
 * Messages formatted with different style look like this:
 * <pre>
 *   This message is formatted with style NONE
 *   [FINE 02:24:20.792] This message is formatted with style SHORT
 *   [FINE FormatExample.print - 02:24:20.792] This message is formatted with style MEDIUM
 *   [FINE com.example.FormatExample.print - Mar 4, 2011 02:24:20.792] This message is formatted with style LONG
 * </pre>
 * 
 * @author Evren Sirin
 * @since 2.0
 * @version 2.0
 */
public final class CustomizableJULFormatter extends Formatter {
	private static enum Style {
		NONE, SHORT, MEDIUM, LONG;
	}
	
	private static enum ThreadStyle {
		ID, NAME;
	}

	private final static DateFormat SHORT_TIME_FORMAT = new SimpleDateFormat("hh:mm:ss.SSS");
	private final static DateFormat LONG_TIME_FORMAT = new SimpleDateFormat("MMM d, yyyy hh:mm:ss.SSS");

	private final static String NEW_LINE = System.getProperty("line.separator");

	private Style style = Style.SHORT;
	private ThreadStyle threadStyle = null;

	private CustomizableJULFormatter(Style style) {
		this.style = style;
	}

	public CustomizableJULFormatter() {
		String stylePropName = getClass().getName() + ".style";
		String style = LogManager.getLogManager().getProperty(stylePropName);
		try {
			this.style = Style.valueOf(style.toUpperCase());
		}
		catch (Exception e) {
			// invalid style; does it make sense to log inside log formatter?
		}
		
		String threadsPropName = getClass().getName() + ".threads";
		String threadStyle = LogManager.getLogManager().getProperty(threadsPropName);
		try {
			this.threadStyle = ThreadStyle.valueOf(threadStyle.toUpperCase());
		}
		catch (Exception e) {
			// invalid style; does it make sense to log inside log formatter?
		}
	}

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		if (style != Style.NONE) {
			sb.append("[");
			sb.append(record.getLevel());
			sb.append(" ");
			if (threadStyle != null) {				
				sb.append("(Thread: ");
				if (threadStyle == ThreadStyle.ID) {
					sb.append(record.getThreadID());
				}
				else {
					sb.append(Thread.currentThread().getName());
				}
				sb.append(") ");
			}
			if (style != Style.SHORT) {
				if (style != Style.LONG) {
					sb.append(simpleClassName(record.getSourceClassName()));
				}
				else {
					sb.append(record.getSourceClassName());
				}
				sb.append(".");
				sb.append(record.getSourceMethodName());
				sb.append(" - ");
			}
			if (style != Style.LONG) {
				sb.append(SHORT_TIME_FORMAT.format(record.getMillis()));
			}
			else {
				sb.append(LONG_TIME_FORMAT.format(record.getMillis()));
			}
			sb.append("] ");
		}

		sb.append(record.getMessage());

		if (record.getThrown() != null) {
			try {
				sb.append(NEW_LINE);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			}
			catch (Exception ex) {
			}
		}

		sb.append(NEW_LINE);

		return sb.toString();
	}

	private String simpleClassName(String s) {
		return s == null ? "?" : s.substring(s.lastIndexOf(".") + 1);
	}

	public static void main(String[] args) {
		for (Style style : Style.values()) {
			LogRecord record = new LogRecord(Level.FINE, "This message is formatted with style " + style);
			record.setSourceClassName("com.example.FormatExample");
			record.setSourceMethodName("print");
			CustomizableJULFormatter formatter = new CustomizableJULFormatter(style);
			System.out.print(formatter.format(record));
		}
	}
}
