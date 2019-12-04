// Copyright (c) 2006 - 2011 -- Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact inquiries@clarkparsia.com.
package com.complexible.common.base;

import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;

/**
 * @author Evren Sirin
 */
public final class OSUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(OSUtil.class);

	/**
	 * Constant instance with memory usages set to 0 that indicates no memory info was retrieved.
	 */
	public static final NativeMemoryUsage ERROR = new NativeMemoryUsage(0, 0);

	private static long pid = Long.MIN_VALUE;

    private OSUtil() {
        throw new AssertionError();
    }

    /**
	 * Native memory usage info collected from OS.
	 *
	 * @author Evren Sirin
	 */
	public static class NativeMemoryUsage {
		/**
		 * Virtual memory usage in bytes
		 */
		public final long mVirtualMem;
		/**
		 * Resident memory usage in bytes
		 */
		public final long mResidentMem;

		public NativeMemoryUsage(long mVirtualMem, long mResidentMem) {
	        this.mVirtualMem = mVirtualMem;
	        this.mResidentMem = mResidentMem;
        }

		@Override
        public String toString() {
	        return String.format("Virtual Mem: %s Resident Mem: %s", Memory.readable(mVirtualMem), Memory.readable(mResidentMem));
        }
	}

	/**
	 * Returns a string with information about native memory usage or {@link #ERROR} if an error occurs.
	 */
	public static NativeMemoryUsage getNativeMemory() {
		try {
			String[] strs = getNativeMemoryInfo().replaceAll("\\s+", " ").split(" ");
			return new NativeMemoryUsage(Long.parseLong(strs[4]) * Memory.KB, Long.parseLong(strs[5]) * Memory.KB);
		}
		catch (Exception e) {
			LOGGER.debug("Error getting native memory info", e);
			return ERROR;
		}
	}

	/**
	 * Returns a string with information about native memory usage of "N/A" if an error occurs.
	 */
	public static String getNativeMemoryInfo() {
		try {
			long pid = getPID();
			String[] cmd = { "ps", "-p", String.valueOf(pid), "-o", "pid,vsz,rss" };
			Process p = Runtime.getRuntime().exec(cmd);
			StringBuilder sb = new StringBuilder();
			CharStreams.copy(new InputStreamReader(p.getInputStream()), sb);
			return sb.toString().trim();
		}
		catch (Exception e) {
			LOGGER.debug("Error getting naitve memory info", e);
			return "N/A";
		}
	}

	/**
	 * Returns the process ID for the JVM or -1 if the process ID cannot be determined.
	 */
	public static long getPID() {
		if (pid == Long.MIN_VALUE) {
			try {
				String[] cmd = { "bash", "-c", "echo $PPID" };
				Process p = Runtime.getRuntime().exec(cmd);
				StringBuilder sb = new StringBuilder();
				CharStreams.copy(new InputStreamReader(p.getInputStream()), sb);
				pid = Long.parseLong(sb.toString().trim());
			}
			catch (Exception e) {
				LOGGER.debug("Error getting PID", e);
				pid = -1;
			}
		}
		return pid;
	}
}
