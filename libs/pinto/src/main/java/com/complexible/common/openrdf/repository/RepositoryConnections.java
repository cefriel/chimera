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

package com.complexible.common.openrdf.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;

/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 4.0
 */
public final class RepositoryConnections {
	/**
	 * the logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConnections.class);

	public RepositoryConnections() {
		throw new AssertionError();
	}

	public static void clear(final RepositoryConnection theConnection, final Resource... theContexts) throws RepositoryException {
		try {
			theConnection.begin();
			theConnection.clear(theContexts);
			theConnection.commit();
		}
		catch (RepositoryException e) {
			theConnection.rollback();
			throw e;
		}
	}

	public static void add(final RepositoryConnection theConnection, final Graph theGraph) throws RepositoryException {
		try {
			theConnection.begin();
			theConnection.add(theGraph);
			theConnection.commit();
		}
		catch (RepositoryException e) {
			theConnection.rollback();
			throw e;
		}
	}

	public static void remove(final RepositoryConnection theConnection, final Graph theGraph) throws RepositoryException {
		try {
			theConnection.begin();
			theConnection.remove(theGraph);
			theConnection.commit();
		}
		catch (RepositoryException e) {
			theConnection.rollback();
			throw e;
		}
	}

	/**
	 * Quietly close the connection object
	 * @param theConn the connection to close
	 */
	public static void closeQuietly(final RepositoryConnection theConn) {
		if (theConn != null) {
			try {
				theConn.close();
			}
			catch (RepositoryException e) {
				LOGGER.error("There was an error while closing the RepositoryConnection.", e);
			}
		}
	}

	public static boolean contains(final RepositoryConnection theConnection, final Statement theStatement) throws RepositoryException {
		return theStatement.getContext() == null
		       ? theConnection.hasStatement(theStatement, true)
		       : theConnection.hasStatement(theStatement, true, theStatement.getContext());
	}

	public static void add(final RepositoryConnection theRepo, final File theFile) throws RDFParseException, IOException {
		add(theRepo, new FileInputStream(theFile), Rio.getParserFormatForFileName(theFile.getName()).orElse(RDFFormat.TURTLE));
	}

	public static void add(final RepositoryConnection theRepo, final InputStream theStream, final RDFFormat theFormat) throws RDFParseException, IOException {
		add(theRepo, new InputStreamReader(theStream, Charsets.UTF_8), theFormat);
	}

	public static void add(final RepositoryConnection theRepo, final Reader theStream, final RDFFormat theFormat) throws RDFParseException, IOException {
		add(theRepo, theStream, theFormat, null, null);
	}

	public static void add(final RepositoryConnection theRepo, final Reader theStream, final RDFFormat theFormat, final Resource theContext) throws IOException, RDFParseException {
		add(theRepo, theStream, theFormat, theContext, null);
	}

	public static void add(final RepositoryConnection theConn, Reader theStream, final RDFFormat theFormat, final Resource theContext, final String theBase) throws RDFParseException, IOException {
		RDFParser aParser = Rio.createParser(theFormat);

		aParser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		aParser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		aParser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);

		aParser.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);

		try {
			theConn.begin();

			RDFInserter aInserter = new RDFInserter(theConn);

			if (theContext != null) {
				aInserter.enforceContext(theContext);
			}

			aParser.setRDFHandler(aInserter);

			aParser.parse(theStream, theBase == null ? (theContext != null ? theContext.stringValue() : "http://openrdf.clarkparsia.com") : theBase);

			theConn.commit();
		}
		catch (Exception e) {
			throw new IOException(e);
		}
		finally {
			Closeables.close(theStream, false);
		}
	}
}
