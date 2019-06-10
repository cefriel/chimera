/*
 * Copyright (c) 2009-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.sail;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.openrdf.util.AdunaIterations;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 2.0
 */
public final class SailConnections {
	/**
	 * the logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SailConnections.class);

	public SailConnections() {
		throw new AssertionError();
	}

	public static void clear(final SailConnection theConnection, final Resource... theContexts) throws SailException {
		try {
			theConnection.begin();
			theConnection.clear(theContexts);
			theConnection.commit();
		}
		catch (SailException e) {
			theConnection.rollback();
			throw e;
		}
	}

	public static void add(final SailConnection theConnection, final Graph theGraph) throws SailException {
		try {
			theConnection.begin();
			for (Statement aStmt : theGraph) {
				if (aStmt.getContext() != null) {
					theConnection.addStatement(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aStmt.getContext());
				}
				else {
					theConnection.addStatement(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject());
				}
			}
			theConnection.commit();
		}
		catch (SailException e) {
			theConnection.rollback();
			throw e;
		}
	}

	public static void remove(final SailConnection theConnection, final Graph theGraph) throws SailException {
		try {
			theConnection.begin();
			for (Statement aStmt : theGraph) {
				if (aStmt.getContext() != null) {
					theConnection.removeStatements(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject(), aStmt.getContext());
				}
				else {
					theConnection.removeStatements(aStmt.getSubject(), aStmt.getPredicate(), aStmt.getObject());
				}
			}
			theConnection.commit();
		}
		catch (SailException e) {
			theConnection.rollback();
			throw e;
		}
	}

	public static boolean contains(final SailConnection theConnection, final Statement theStmt) throws SailException {
		CloseableIteration<?,SailException> aIter = theStmt.getContext() == null
		       ? theConnection.getStatements(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), true)
		       : theConnection.getStatements(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), true, theStmt.getContext());

		try {
			return aIter.hasNext();
		}
		finally {
			AdunaIterations.closeQuietly(aIter);
		}
	}

	public static void closeQuietly(final SailConnection theConnection) {
		if (theConnection != null) {
			try {
				theConnection.close();
			}
			catch (SailException e) {
				LOGGER.warn("An error while closing a SailConnection was ignored", e);
			}
		}
	}
}
