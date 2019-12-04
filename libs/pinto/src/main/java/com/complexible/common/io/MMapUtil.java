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

import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Utility class which provides a method for attempting to directly unmap a {@link MappedByteBuffer} rather than
 * waiting for the JVM &amp; OS eventually unmap.</p>
 *
 * @author  Evren Sirin
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public final class MMapUtil {
    public static final Logger LOGGER = LoggerFactory.getLogger(MMapUtil.class);

    /**
     * No instances
     */
    private MMapUtil() {
        throw new AssertionError();
    }

    /**
     * http://svn.apache.org/repos/asf/lucene/dev/trunk/lucene/src/java/org/apache/lucene/store/MMapDirectory.java
     * <code>true</code>, if this platform supports unmapping mmapped files.
     */
    public static final boolean UNMAP_SUPPORTED;

    static {
        boolean v;
        try {
            Class.forName("sun.misc.Cleaner");
            Class.forName("java.nio.DirectByteBuffer").getMethod("cleaner");
            v = true;
        }
        catch (Exception e) {
            v = false;
        }
        UNMAP_SUPPORTED = v;

        if (!UNMAP_SUPPORTED) {
            LOGGER.warn("JVM does not support unmapping memory-mapped files.");
        }
    }

    /**
     * Try to unmap the given {@link java.nio.MappedByteBuffer}. This method enables the workaround for unmapping the
     * buffers from address space after closing {@link java.nio.MappedByteBuffer}, that is mentioned in the bug report.
     * This hack may fail on non-Sun JVMs. It forcefully unmaps the buffer on close by using an undocumented internal
     * cleanup functionality.
     *
     * @return <code>true</code> if unmap was successful, <code>false</code> if unmap is not supported by the JVM or if
     * there was an exception while trying to unmap.
     */
    public static final boolean unmap(final MappedByteBuffer theBuffer) {
        if (UNMAP_SUPPORTED) {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        final Method getCleanerMethod = theBuffer.getClass().getMethod("cleaner");
                        getCleanerMethod.setAccessible(true);
                        final Object cleaner = getCleanerMethod.invoke(theBuffer);
                        if (cleaner != null) {
                            cleaner.getClass().getMethod("clean").invoke(cleaner);
                        }
                        return null;
                    }
                });
                return true;
            }
            catch (PrivilegedActionException e) {
                LOGGER.warn("Cleaning memory mapped byte buffer failed", e);
            }
        }

        return false;
    }
}
