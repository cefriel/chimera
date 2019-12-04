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

package com.complexible.common.base;

import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * <p>Utility class for memory related information.</p>
 *
 * @author  Evren Sirin
 * @author  Michael Grove
 *
 * @since 2.0
 * @version 3.1.1
 */
public final class Memory {
	private static final Logger LOGGER = LoggerFactory.getLogger(Memory.class);

	private static final MemoryMXBean MEMORY = ManagementFactory.getMemoryMXBean();
	private static final Runtime RUNTIME = Runtime.getRuntime();

	private static final DecimalFormat ONE_FRACTION_DIGIT = new DecimalFormat("0.0");
	private static final DecimalFormat NO_FRACTION_DIGIT = new DecimalFormat("0");
	private static final String BYTE_COUNT_SUFFIX = "BKMGT";

	/**
	 * The number of bytes in a kilobyte (2^10).
	 */
	public static final long KB = (1 << 10);

	/**
	 * The number of bytes in a kilobyte (2^20).
	 */
	public static final long MB = (1 << 20);

	/**
	 * The number of bytes in a gigabyte (2^30).
	 */
	public static final long GB = (1 << 30);

	private Memory() {
		throw new AssertionError();
	}

	/**
	 * Returns a human-readable representation of bytes similar to how "ls -h" works in Unix systems. The resulting is
	 * guaranteed to be 4 characters or less unless the given value is greater than 999TB. The last character in the
	 * returned string is one of 'B', 'K', 'M', 'G', or 'T' representing bytes, kilobytes, megabytes, gigabytes or
	 * terabytes. This function uses the binary unit system where 1K = 1024B.
	 *
	 * <p>
	 * Examples:
	 * </p>
	 *
	 * <pre>
	 *         482 = 482B
	 *        1245 = 1.2K
	 *      126976 = 124K
	 *     4089471 = 3.9M
	 *    43316209 =  41M
	 *  1987357695 = 1.9G
	 * </pre>
	 */
	public static String readable(long bytes) {
		double result = bytes;
		int i = 0;
		while (result >= 1000 && i < BYTE_COUNT_SUFFIX.length() - 1) {
			result /= 1024.0;
			i++;
		}
		DecimalFormat formatter = (result < 10 && i > 0) ? ONE_FRACTION_DIGIT : NO_FRACTION_DIGIT;
		return formatter.format(result) + BYTE_COUNT_SUFFIX.charAt(i);
	}

	/**
	 * Take a representation from {@link #readable(long)} and turn it back into bytes.  For example, if the
	 * input is "2K" the output would be 2048.
	 *
	 * @param theBytes  the byte string
	 * @return          the number of bytes
	 */
	public static long fromReadable(final String theBytes) {
		final char aSuffix = theBytes.toUpperCase().charAt(theBytes.length()-1);
		final double aBytes = Double.parseDouble(theBytes.substring(0, theBytes.length()-1));
		switch (aSuffix) {
			case 'B':
				return (long) aBytes;
			case 'K':
				return (long) (aBytes * KB);
			case 'M':
				return (long) (aBytes * MB);
			case 'G':
				return (long) (aBytes * GB);
			case 'T':
				return (long) (aBytes * (1L << 40));
			default:
				throw new IllegalArgumentException("Unknown byte count suffix");
		}
	}

	/**
	 * Returns the used memory in bytes. The result is an estimate and based on when the GC runs this value might change
	 * significantly.
	 */
	public static long used() {
		return RUNTIME.totalMemory() - RUNTIME.freeMemory();
	}

	/**
	 * Returns the total memory available to JVM in bytes. This value may change over time.
	 */
	public static long total() {
		return RUNTIME.totalMemory();
	}

	/**
	 * Returns the maximum amount of memory that the JVM will attempt to use. It is possible that
	 */
	public static long max() {
		return RUNTIME.maxMemory();
	}

	/**
	 * Returns the current amount of memory available on the heap.  Shorthand for: {@code Memory.max() - Memory.used()}
	 *
	 * @return  available heap memory
	 */
	public static long available() {
		return max() - used();
	}

	/**
	 * Return the total memory available to the system, ie how much RAM the computer has.
	 * @return  the system memory.
	 */
	public static long system() {
		return ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean())
			.getTotalPhysicalMemorySize();
	}

	/**
	 * <b>Intended for Debugging Only</b> Makes a best effort to run the Garbage Collector by calling the Runtime.gc()
	 * repeatedly and checking until the used memory reported decreases.
	 */
	public static void gc() {
		try {
			// It helps to call Runtime.gc()
			// using several method calls:
			for (int r = 0; r < 4; ++r)
				_runGC();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void _runGC() throws Exception {
		long usedMem1 = used(), usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++i) {
			RUNTIME.runFinalization();
			RUNTIME.gc();
			Thread.yield();

			usedMem2 = usedMem1;
			usedMem1 = used();
		}
	}

	public static float usedRatio() {
		return 1 - ((float) RUNTIME.freeMemory() / RUNTIME.totalMemory());
	}

	public static long usedHeap() {
		return MEMORY.getHeapMemoryUsage().getUsed();
	}

	public static long usedNonHeap() {
		return MEMORY.getNonHeapMemoryUsage().getUsed();
	}

	public static String heapUsage() {
		MemoryUsage usage = MEMORY.getHeapMemoryUsage();
		StringBuilder sb = new StringBuilder();
		sb.append("init = ").append(readable(usage.getInit()));
		sb.append(" used = ").append(readable(usage.getUsed()));
		sb.append(" committed = ").append(readable(usage.getCommitted()));
		sb.append(" max = ").append(readable(usage.getMax()));
		return sb.toString();
	}

	/**
	 * Detailed memory information logged only at TRACE level.
	 */
	public static String detailedUsage() {
		StringBuilder sb = new StringBuilder();
		try {
			Formatter formatter = new Formatter(sb);
			formatter.format("%nDETAILED MEMORY INFO%n");
			// Read MemoryMXBean
			formatter.format("Heap Memory Usage: %s%n", MEMORY.getHeapMemoryUsage());
			formatter.format("Non-Heap Memory Usage: %s%n", MEMORY.getNonHeapMemoryUsage());

			// Read Garbage Collection information
			List<GarbageCollectorMXBean> gcmBeans = ManagementFactory.getGarbageCollectorMXBeans();
			for (GarbageCollectorMXBean gcmBean : gcmBeans) {
				formatter.format("Name: %s%n", gcmBean.getName());
				formatter.format("\tCollection count: %s%n", gcmBean.getCollectionCount());
				formatter.format("\tCollection time: %s%n", gcmBean.getCollectionTime());
				formatter.format("\tMemory Pools: %s%n", Arrays.toString(gcmBean.getMemoryPoolNames()));
			}

			// Read Memory Pool Information
			formatter.format("Memory Pools Info");
			List<MemoryPoolMXBean> mpBeans = ManagementFactory.getMemoryPoolMXBeans();
			for (MemoryPoolMXBean mpBean : mpBeans) {
				formatter.format("Name: %s%n", mpBean.getName());
				formatter.format("\tUsage: %s%n", mpBean.getUsage());
				formatter.format("\tCollection Usage: %s%n", mpBean.getCollectionUsage());
				formatter.format("\tPeak Usage: %s%n", mpBean.getPeakUsage());
				formatter.format("\tType: %s%n", mpBean.getType());
				formatter.format("\tMemory Manager Names: %s%n", Arrays.toString(mpBean.getMemoryManagerNames()));
			}

		}
		catch (Exception e) {
			LOGGER.warn("Cannot get memory info", e);
		}
		return sb.toString();
	}

	/**
	 * Returns the size of this object (with its references).
	 */
	public static long sizeOf(Object obj) {
		return sizeOf(obj, Sets.newIdentityHashSet());
	}

	private static long sizeOf(Object obj, final Set<Object> visited) {
		Instrumentation instrumentation = Instrumentor.instrumentation();

		if (instrumentation == null)
			return -1;

		if (obj == null)
			return 0;

		final ArrayDeque<Object> queue = new ArrayDeque<Object>();

		visited.add(obj);
		queue.add(obj);

		int result = 0;

		while (!queue.isEmpty()) {
			obj = queue.removeFirst();

			result += instrumentation.getObjectSize(obj);

			Class<?> objClass = obj.getClass();

			if (objClass.isArray() && !objClass.getComponentType().isPrimitive()) {
				final int arrayLength = Array.getLength(obj);
				for (int i = 0; i < arrayLength; ++i) {
					final Object ref = Array.get(obj, i);

					if ((ref != null) && visited.add(ref)) {
						queue.addFirst(ref);
					}
				}
			}

			while (objClass != null) {
				for (Field fld : objClass.getDeclaredFields()) {
					int mod = fld.getModifiers();

					if (((mod & 0x8) == 0)) {
						Class fieldClass = fld.getType();
						if (!fieldClass.isPrimitive()) {
							if (!fld.isAccessible()) {
								fld.setAccessible(true);
							}

							try {
								Object ref = fld.get(obj);
								if ((ref != null) && visited.add(ref)) {
									queue.addFirst(ref);
								}
							}
							catch (IllegalAccessException illAcc) {
								throw new InternalError("Couldn't read " + fld);
							}
						}
					}
				}
				objClass = objClass.getSuperclass();
			}
		}

		return result;
	}
}
