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

package eu.st4rt.converter.empire.sesame;

import java.net.ConnectException;
import java.net.URI;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.openrdf.util.ModelBuildingRDFHandler;

import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.MutableDataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.ResultSet;
import eu.st4rt.converter.empire.ds.SupportsNamedGraphs;
import eu.st4rt.converter.empire.ds.SupportsTransactions;
import eu.st4rt.converter.empire.ds.TripleSource;
import eu.st4rt.converter.empire.ds.impl.AbstractDataSource;
import eu.st4rt.converter.empire.impl.RdfQueryFactory;
import eu.st4rt.converter.empire.impl.sparql.SPARQLDialect;


/**
 * <p>Implementation of the DataSource interface(s) backed by a Sesame 2 repository.  This can be used as a base class
 * for any back-end which supports the Sesame 2 SAIL api, such as BigData, OWLIM, Neo4j, and others.</p>
 *
 * @author 	Michael Grove
 * @since 	0.6
 * @version 1.0
 */
public final class RepositoryDataSource extends AbstractDataSource implements MutableDataSource, TripleSource, SupportsNamedGraphs, SupportsTransactions {

	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryDataSource.class);

	/**
	 * The underlying Sesame repository
	 */
	private Repository mRepository;

	/**
	 * The connection to the repository
	 */
	private RepositoryConnection mConnection;

	/**
	 * The query languge to use when sending queries to the repository
	 */
	private QueryLanguage mQueryLang;

	/**
	 * Create a new RepositoryDataSource which uses the SPARQL query dialect for its Query API
	 * @param theRepository the sesame repository to back this data source
	 */
	public RepositoryDataSource(final Repository theRepository) {
		this(theRepository, false);
	}

	/**
	 * Create a new RepositoryDataSource
	 * @param theRepository the sesame repository to back this data source
	 * @param theUseSerql true to use the serql query dialect with this data source, false to default to sparql
	 */
	RepositoryDataSource(final Repository theRepository, boolean theUseSerql) {
		mRepository = theRepository;

		// TODO: add the SupportsTransactions interface to this class so Empire notices it natively supports
		// transactions.  right now, changes within a transaction are not "live", even within the same
		// connection, so it looks like adds & deletes fail.  not good.  need a different isolation level
		// for the transactions for it to work right.  or i go back to the drawing board on some parts of entitymanager
		// sesame 3 will have different transaction isolation levels, settable for each repo, which is what we need
		// i think.  really, the catch is that transactions behave differently (different isolation levels) depending
		// on the repository implementation.  so its' not easy to do *one* solution that will work for all of them
		// so we have to rely on our naive poor-man's transaction implementation.


		mQueryLang = QueryLanguage.SPARQL;
		setQueryFactory(new RdfQueryFactory(this, SPARQLDialect.instance()));

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final Model theGraph) throws DataSourceException {
		assertConnected();

		try {
			mConnection.add(theGraph);
		}
		catch (RepositoryException e) {
			rollback();

			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final Model theGraph) throws DataSourceException {
		assertConnected();

		try {
			mConnection.remove(theGraph);
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean isConnected() {
		try {
			return mConnection != null && mConnection.isOpen() && super.isConnected();
		}
		catch (RepositoryException e) {
			LOGGER.error("There was an error while connecting", e);

			return false;
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void connect() throws ConnectException {
		if (!isConnected()) {
			setConnected(true);
			try {
				mConnection = mRepository.getConnection();

				//mConnection.setAutoCommit(false);
			}
			catch (RepositoryException e) {
				throw (ConnectException) new ConnectException("There was an error establishing the connection").initCause(e);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void disconnect() {
		assertConnected();

		try {
			if (mConnection.isActive()) {
				mConnection.rollback();
			}

			mConnection.close();

			setConnected(false);

			mRepository.shutDown();
		}
		catch (RepositoryException e) {
			LOGGER.error("There was an error while disconnecting", e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public ResultSet selectQuery(final String theQuery) throws QueryException {
		assertConnected();

		TupleQueryResult aResult = null;

		try {

			aResult = mConnection.prepareTupleQuery(mQueryLang, theQuery).evaluate();


			return new TupleQueryResultSet(aResult);
		}
		catch (Exception e) {
			throw new QueryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Model graphQuery(final String theQuery) throws QueryException {
		assertConnected();

		ModelBuildingRDFHandler aHandler = new ModelBuildingRDFHandler();

		try {
			GraphQuery aQuery = mConnection.prepareGraphQuery(mQueryLang, theQuery);
			aQuery.evaluate(aHandler);			
			return aHandler.getModel();
		}
		catch (Exception e) {
			throw new QueryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean ask(final String theQuery) throws QueryException {
		try {
			return mConnection.prepareBooleanQuery(mQueryLang, theQuery).evaluate();
		}
		catch (Exception e) {
			throw new QueryException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Model describe(final String theQuery) throws QueryException {
		return graphQuery(theQuery);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void add(final URI theGraphURI, final Model theGraph) throws DataSourceException {
		assertConnected();

		try {
			mConnection.add(theGraph, mConnection.getValueFactory().createIRI(theGraphURI.toString()));
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final URI theGraphURI) throws DataSourceException {
		assertConnected();

		try {
			Resource aContext = mConnection.getValueFactory().createIRI(theGraphURI.toString());

			mConnection.remove(mConnection.getStatements(null, null, null, true, aContext), aContext);
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void remove(final URI theGraphURI, final Model theGraph) throws DataSourceException {
		assertConnected();

		try {
			mConnection.remove(theGraph, mConnection.getValueFactory().createIRI(theGraphURI.toString()));
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void begin() throws DataSourceException {
		assertConnected();

		try {
			mConnection.begin();
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void commit() throws DataSourceException {
		assertConnected();

		try {
			mConnection.commit();
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	public Repository getRepository() {
		return mRepository;
	}

	public void clearRepository() {
		mConnection.clear();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void rollback() throws DataSourceException {
		assertConnected();

		try {
			mConnection.rollback();
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Iterable<Statement> getStatements(Resource theSubject, IRI thePredicate, Value theObject)
			throws DataSourceException {
		try {
			return () -> Iterations.stream(mConnection.getStatements(theSubject, thePredicate, theObject, true)).iterator();
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Iterable<Statement> getStatements(Resource theSubject, IRI thePredicate, Value theObject, Resource theContext)
			throws DataSourceException {
		if (theContext == null) {
			// if context is null, this means any context should match -- we can forward request to getStatements() without context
			return getStatements(theSubject, thePredicate, theObject);
		}

		try {
			return () -> Iterations.stream(mConnection.getStatements(theSubject, thePredicate, theObject, true, theContext)).iterator();
		}
		catch (RepositoryException e) {
			throw new DataSourceException(e);
		}
	}

	@Override
	public void updateQuery(String theQuery) throws QueryException {
		assertConnected();
		try {
			updateQuery(theQuery);
		}
		catch (Exception e) {
			throw new QueryException(e);
		}
	}

}
