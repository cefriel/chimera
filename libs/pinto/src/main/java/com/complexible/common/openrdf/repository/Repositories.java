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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.eclipse.rdf4j.common.iteration.CloseableIteration;
import org.eclipse.rdf4j.common.iteration.EmptyIteration;
import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
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
public final class Repositories {
	/**
	 * the logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Repositories.class);

	public Repositories() {
		throw new AssertionError();
	}

	/**
	 * Return the number of statements in this repository
	 *
	 * @return the size of the repo
	 *
	 * @throws RepositoryException if there is an error while retrieving the size.
	 */
	public static long size(final Repository theRepository) throws RepositoryException {
		RepositoryConnection aConn = null;

		try {
			aConn = theRepository.getConnection();

			return aConn.size();
		}
		finally {
			RepositoryConnections.closeQuietly(aConn);
		}
	}

	public static void clear(final Repository theRepository) throws RepositoryException {
		try (RepositoryConnection aConn = theRepository.getConnection()) {
			RepositoryConnections.clear(aConn);
		}
	}

	public static void add(final Repository theRepository, final Graph theGraph) throws RepositoryException {
		try (RepositoryConnection aConn = theRepository.getConnection()) {
			RepositoryConnections.add(aConn, theGraph);
		}
	}

	public static void remove(final Repository theRepository, final Graph theGraph) throws RepositoryException {
		try (RepositoryConnection aConn = theRepository.getConnection()) {
			RepositoryConnections.remove(aConn, theGraph);
		}
	}

	public static boolean contains(final Repository theRepository, final Statement theStmt) throws RepositoryException {
		try (RepositoryConnection aConn = theRepository.getConnection()) {
			return theStmt.getContext() != null
			       ? aConn.hasStatement(theStmt, true, theStmt.getContext())
			       : aConn.hasStatement(theStmt, true);
		}
	}

	public static void add(final Repository theRepo, final File theFile) throws RDFParseException, IOException {
		add(theRepo, new FileInputStream(theFile), Rio.getParserFormatForFileName(theFile.getName()).orElse(RDFFormat.TURTLE));
	}

	public static void add(final Repository theRepo, final InputStream theStream, final RDFFormat theFormat) throws RDFParseException, IOException {
		add(theRepo, new InputStreamReader(theStream, Charsets.UTF_8), theFormat);
	}

	public static void add(final Repository theRepo, final Reader theStream, final RDFFormat theFormat) throws RDFParseException, IOException {
		add(theRepo, theStream, theFormat, null, null);
	}

	public static void add(final Repository theRepo, final Reader theStream, final RDFFormat theFormat, final Resource theContext) throws IOException, RDFParseException {
		add(theRepo, theStream, theFormat, theContext, null);
	}

	public static void add(final Repository theRepo, final Reader theStream, final RDFFormat theFormat, final Resource theContext, final String theBase) throws RDFParseException, IOException {
		RepositoryConnection aConn = null;

		try {
			aConn = theRepo.getConnection();

			RepositoryConnections.add(aConn, theStream, theFormat, theContext, theBase);
		}
		catch (Exception e) {
			throw new IOException(e);
		}
		finally {
			RepositoryConnections.closeQuietly(aConn);
			Closeables.close(theStream, false);
		}
	}

	/**
	 * Write the contents of the repository to the given file in the specified format
	 * @param theRepo the repository to write
	 * @param theFile the file to write to
	 * @param theFormat the format to write the RDF in
	 * @throws RepositoryException if there is an error getting the data from the repository
	 * @throws IOException if there is an error writing to the file
	 */
	public static void writeRepository(final Repository theRepo, File theFile, final RDFFormat theFormat) throws RepositoryException, IOException {
		writeRepository(theRepo, Rio.createWriter(theFormat, new OutputStreamWriter(new FileOutputStream(theFile), Charsets.UTF_8)));
	}

	/**
	 * Write the contents of the repository to the given stream in the specified format
	 *
	 * @param theRepo   the repository to write
	 * @param theStream the stream to write to
	 * @param theFormat the format to write the RDF in
	 *
	 * @throws RepositoryException if there is an error getting the data from the repository
	 * @throws IOException if there is an error writing to the stream
	 */
	public static void writeRepository(final Repository theRepo, final OutputStream theStream, final RDFFormat theFormat) throws RepositoryException, IOException {
		writeRepository(theRepo, Rio.createWriter(theFormat, new OutputStreamWriter(theStream, Charsets.UTF_8)));
	}

	/**
	 * Write the contents of the repository to the given writer in the specified format
	 *
	 * @param theRepo   the repository to write
	 * @param theWriter the writer to write to
	 * @param theFormat the format to write the RDF in
	 *
	 * @throws RepositoryException if there is an error getting the data from the repository
	 * @throws IOException if there is an error writing to the writer
	 */
	public static void writeRepository(final Repository theRepo, final Writer theWriter, final RDFFormat theFormat) throws RepositoryException, IOException {
		writeRepository(theRepo, Rio.createWriter(theFormat, theWriter));
	}

	private static void writeRepository(final Repository theRepo, final RDFWriter theWriter) throws IOException, RepositoryException {
		RepositoryConnection aConn = null;
		try {
			aConn = theRepo.getConnection();

			aConn.exportStatements(null, null, null, true, theWriter);
		}
		catch (RDFHandlerException e) {
			throw new IOException(e);
		}
		finally {
			RepositoryConnections.closeQuietly(aConn);
		}
	}

	/**
	 * Execute a select query.
	 *
	 * @param theLang   the query language
	 * @param theQuery  the query to execute
	 * @return          the result set
	 *
	 * @throws RepositoryException      if there is an error while querying
	 * @throws MalformedQueryException  if the query cannot be parsed
	 * @throws QueryEvaluationException if there is an error while querying
	 *
	 */
	public static TupleQueryResult selectQuery(final Repository theRepo, final QueryLanguage theLang, final String theQuery) throws RepositoryException, MalformedQueryException,
	                                                                                                                    QueryEvaluationException {
		RepositoryConnection aConn = null;
		try {
			aConn = theRepo.getConnection();

			return new ConnectionClosingTupleQueryResult(aConn, aConn.prepareTupleQuery(theLang, theQuery).evaluate());
		}
		catch (RepositoryException e) {
			RepositoryConnections.closeQuietly(aConn);
			throw e;
		}
	}

	/**
	 * Execute a construct query.
	 *
	 * @param theLang   the query language
	 * @param theQuery  the query string
	 * @return          the results of the construct query
	 *
	 * @throws RepositoryException      if there is an error while querying
	 * @throws MalformedQueryException  if the query cannot be parsed
	 * @throws QueryEvaluationException if there is an error while querying
	 */
	public static GraphQueryResult constructQuery(final Repository theRepo, QueryLanguage theLang, String theQuery) throws RepositoryException,
	                                                                                                                       MalformedQueryException, QueryEvaluationException {
		RepositoryConnection aConn = null;

		try {
			aConn = theRepo.getConnection();

			return new ConnectionClosingGraphQueryResult(aConn, aConn.prepareGraphQuery(theLang, theQuery).evaluate());
		}
		catch (RepositoryException e) {
			RepositoryConnections.closeQuietly(aConn);
			throw e;
		}
	}

	/**
	 * Return a RepositoryResult over all the statements in the repository.
	 * @return an iteration of all statements
	 */
	public static RepositoryResult<Statement> getStatements(final Repository theRepository) {
		return getStatements(theRepository, null, null, null);
	}

	/**
	 * Return a RepositoryResult over the statements in this Repository which match the given spo pattern.
	 *
	 * @param theSubj       the subject to search for, or null for any
	 * @param thePred       the predicate to search for, or null for any
	 * @param theObj        the object to search for, or null for any
	 * @param theContext    the contexts for the statement(s)
	 *
	 * @return an Iterable over the matching statements
	 */
	public static RepositoryResult<Statement> getStatements(final Repository theRepo, final Resource theSubj,
	                                                        final IRI thePred, final Value theObj, final Resource... theContext) {
		RepositoryConnection aConn = null;
		try {
			aConn = theRepo.getConnection();

            return ConnectionClosingRepositoryResult.newResult(aConn, aConn.getStatements(theSubj, thePred, theObj, true, theContext));
		}
		catch (Exception ex) {
			RepositoryConnections.closeQuietly(aConn);

			LOGGER.error("There was an error getting statements, returning empty iteration.", ex);

			return new RepositoryResult<>(emptyStatementIteration());
		}
	}

	/**
	 * Return an empty Iteration over Statements
	 * @return an empty iteration
	 */
	private static CloseableIteration<Statement, RepositoryException> emptyStatementIteration() {
		return new EmptyIteration<>();
	}
}
