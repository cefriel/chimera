/* IT2Rail. Contract H2020 - N. 636078
 *
 * Unless required by applicable law or agreed to in writing, this software
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 * See the IT2Rail Consortium Agreement for the specific language governing permissions and
 * limitations of use.
 */


package eu.st4rt.converter.org.it2rail.empire.rdf4jdatasouce;

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
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.openrdf.util.ModelBuildingRDFHandler;

import eu.st4rt.converter.empire.QueryFactory;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.MutableDataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.ResultSet;
import eu.st4rt.converter.empire.ds.SupportsNamedGraphs;
import eu.st4rt.converter.empire.ds.SupportsTransactions;
import eu.st4rt.converter.empire.ds.TripleSource;
import eu.st4rt.converter.empire.ds.impl.AbstractDataSource;
import eu.st4rt.converter.empire.sesame.TupleQueryResultSet;


public final class InMemoryRepositoryDataSource extends AbstractDataSource implements MutableDataSource, TripleSource, SupportsNamedGraphs, SupportsTransactions {

	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryRepositoryDataSource.class);

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


	InMemoryRepositoryDataSource(final Repository theRepository, QueryFactory qFactory) {
		this(theRepository, false, qFactory);
	}


	InMemoryRepositoryDataSource(final Repository theRepository, boolean theUseSerql, QueryFactory qFactory) {
		mRepository = theRepository;

		mQueryLang = QueryLanguage.SPARQL;
		//setQueryFactory(new RdfQueryFactory(this, SPARQLDialect.instance()));
		if(qFactory != null)
			setQueryFactory(qFactory);

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
	public void updateQuery(final String theQuery) throws QueryException {
		assertConnected();

		ModelBuildingRDFHandler aHandler = new ModelBuildingRDFHandler();

		try {
			Update aQuery = mConnection.prepareUpdate(theQuery);
			aQuery.execute();			
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
}
