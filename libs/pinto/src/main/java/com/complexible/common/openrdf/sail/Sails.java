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
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailConnection;
import org.eclipse.rdf4j.sail.SailException;

import com.complexible.common.openrdf.util.AdunaIterations;


/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 2.0
 */
public final class Sails {

	public Sails() {
		throw new AssertionError();
	}

	public static void clear(final Sail theSail) throws SailException {
		SailConnection aConn = theSail.getConnection();
		try {
			SailConnections.clear(aConn);
		}
		finally {
			aConn.close();
		}
	}

	public static void add(final Sail theSail, final Graph theGraph) throws SailException {
		SailConnection aConn = theSail.getConnection();
		try {
			SailConnections.add(aConn, theGraph);
		}
		finally {
			aConn.close();
		}
	}

	public static void remove(final Sail theSail, final Graph theGraph) throws SailException {
		SailConnection aConn = theSail.getConnection();
		try {
			SailConnections.remove(aConn, theGraph);
		}
		finally {
			aConn.close();
		}
	}

	public static boolean contains(final Sail theSail, final Statement theStmt) throws SailException {
		SailConnection aConn = theSail.getConnection();
		CloseableIteration<?,?> aIter = null;
		try {
			aIter =
				theStmt.getContext() != null
			       ? aConn.getStatements(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), true, theStmt.getContext())
			       : aConn.getStatements(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), true);

			return aIter.hasNext();
		}
		catch (Exception e) {
			throw new SailException(e);
		}
		finally {
			AdunaIterations.closeQuietly(aIter);
			aConn.close();
		}
	}
}
