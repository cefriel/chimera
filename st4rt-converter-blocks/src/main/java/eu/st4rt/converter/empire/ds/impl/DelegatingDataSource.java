/*
 * Copyright (c) 2009-2010 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.ds.impl;

import org.eclipse.rdf4j.model.Model;

import eu.st4rt.converter.empire.QueryFactory;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.ResultSet;

import java.net.ConnectException;

/**
 * <p>Implementation of the {@link DataSource} interface that just delegates all calls to the underlying DataSource.</p>
 *
 * @author  Michael Grove
 * @since   0.7
 * @version 1.0
 */
public class DelegatingDataSource implements DataSource {

	/**
	 * The actual underyling DataDource
	 */
	private DataSource mDelegate;

	/**
	 * Create a new DelegatingDataSource
	 * @param theDelegate the underlying DataSource
	 */
	public DelegatingDataSource(final DataSource theDelegate) {
		mDelegate = theDelegate;
	}

	/**
	 * Return the underlying DataSource which all calls are delegated to
	 * @return the actual DataSource
	 */
	public DataSource getDelegate() {
		return mDelegate;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isConnected() {
		return mDelegate.isConnected();
	}

	/**
	 * @inheritDoc
	 */
	public void connect() throws ConnectException {
		mDelegate.connect();
	}

	/**
	 * @inheritDoc
	 */
	public void disconnect() {
		mDelegate.disconnect();
	}

	/**
	 * @inheritDoc
	 */
	public ResultSet selectQuery(final String theQuery) throws QueryException {
		return mDelegate.selectQuery(theQuery);
	}

	/**
	 * @inheritDoc
	 */
	public Model graphQuery(final String theQuery) throws QueryException {
		return mDelegate.graphQuery(theQuery);
	}

	/**
	 * @inheritDoc
	 */
	public QueryFactory getQueryFactory() {
		return mDelegate.getQueryFactory();
	}

	/**
	 * @inheritDoc
	 */
	public boolean ask(final String theQuery) throws QueryException {
		return mDelegate.ask(theQuery);
	}

	/**
	 * @inheritDoc
	 */
	public Model describe(final String theQuery) throws QueryException {
		return mDelegate.describe(theQuery);
	}

	@Override
	public void updateQuery(String theQuery) throws QueryException {
		mDelegate.updateQuery(theQuery);
	}
}
