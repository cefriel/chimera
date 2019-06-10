/*
 * Copyright (c) 2009-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.util;

import java.util.Optional;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * <p>Utility methods for Aduna {@link info.aduna.iteration.Iteration Iterations} not already present in {@link Iterations}</p>
 *
 * @author  Michael Grove
 * @since   0.4
 * @version 4.0
 */
public final class AdunaIterations {
	/**
	 * the logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdunaIterations.class);

	/**
	 * Private constructor, no instances
	 */
	private AdunaIterations() {
        throw new AssertionError();
	}

	/**
	 * Quietly close the iteration
	 * @param theCloseableIteration the iteration to close
	 */
	public static void closeQuietly(final CloseableIteration<?,?> theCloseableIteration) {
		try {
			if (theCloseableIteration != null) {
				Iterations.closeCloseable(theCloseableIteration);
			}
		}
		catch (Exception e) {
			LOGGER.warn("Ignoring error while closing iteration.", e);
		}
	}

    /**
     * Return the first result of the iteration.  If the Iteration is empty or null, the Optional will be absent.
     *
     * The Iteration is closed whether or not there is a result.
     *
     * @param theIter   the iteration
     * @return          an Optional containing the first result of the iteration, if present.
     * @throws E
     */
    public static <T, E extends Exception> Optional<T> singleResult(final CloseableIteration<T, E> theIter) throws E {
        if (theIter == null) {
            return Optional.empty();
        }

        try {
            return theIter.hasNext() ? Optional.of(theIter.next()) : Optional.<T>empty();
        }
        finally {
            theIter.close();
        }
    }

    /**
     * Consume all of the results in the Iteration and then close it when iteration is complete.
     * @param theIter   the Iteration to consume
     * @throws E        if there is an error while consuming the results
     */
    public static <T, E extends Exception> void consume(final CloseableIteration<T, E> theIter) throws E {
        if (theIter == null) {
            return;
        }

        try {
            while (theIter.hasNext()) {
                theIter.next();
            }
        }
        finally {
            theIter.close();
        }
    }

    /**
     * Return the number of elements left in the Iteration.  The iteration is closed when complete.
     * @param theIter   the Iteration whose size should be computed
     * @return          the number of elements left
     * @throws E        if there is an error while iterating
     */
    public static <T, E extends Exception> long size(final CloseableIteration<T, E> theIter) throws E {
        if (theIter == null) {
            return 0;
        }

        try {
            long aCount = 0;
            while (theIter.hasNext()) {
                theIter.next();
                aCount++;
            }

            return aCount;
        }
        finally {
            theIter.close();
        }
    }
}
