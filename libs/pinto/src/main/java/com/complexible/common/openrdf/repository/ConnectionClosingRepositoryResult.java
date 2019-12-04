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

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;

/**
 * <p>A {}@link RepositoryResult} which will close the {@link RepositoryConnection} which created it when it's closed.</p>
 *
 * @author  Michael Grove
 * @since   2.0
 * @version 2.0
 */
public final class ConnectionClosingRepositoryResult<T> extends RepositoryResult<T> {
	private final RepositoryConnection mConn;

	private ConnectionClosingRepositoryResult(final RepositoryConnection theConn, final RepositoryResult<T> theResult) {
		super(theResult);
		mConn = theConn;
	}

	public static <T> RepositoryResult<T> newResult(final RepositoryConnection theConn, final RepositoryResult<T> theResult) {
		return new ConnectionClosingRepositoryResult<T>(theConn, theResult);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected void handleClose() throws RepositoryException {
		super.handleClose();
		mConn.close();
	}
}
