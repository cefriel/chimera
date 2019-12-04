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

import org.eclipse.rdf4j.model.*;

import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.TripleSource;

import javax.persistence.Query;

/**
 * <p>Wraps a general {@link DataSource}, implementing getStatements using SPARQL queries.</p>
 *
 * @author  Pedro Oliveira
 * @author  Michael Grove
 * @since   0.7
 * @version 1.0
 */
public class TripleSourceAdapter extends DelegatingDataSource implements TripleSource {
	private static final String SUBJECT_FILTER = "FILTER (?s = ??ss)";

	private static final String OBJECT_FILTER = "FILTER (?o = ??oo)";

	private static final String SUBJECT_OBJECT_FILTER = "FILTER (?s = ??ss && ?o = ??oo)";

	public TripleSourceAdapter(DataSource source) {
		super(source);
	}

	/**
	 * @inheritDoc
	 */
	public Iterable<Statement> getStatements(Resource theSubject, IRI thePredicate, Value theObject) throws DataSourceException {
		// Subject and object restrictions are implemented as filters, because some implementations can have problems
		// dealing with bnodes

		String aFilter = "";
		
		if (theSubject != null && theObject != null) {
			aFilter = SUBJECT_OBJECT_FILTER;
		}
		else if (theSubject != null) {
			aFilter = SUBJECT_FILTER;
		}
		else if (theObject != null) {
			aFilter = OBJECT_FILTER;
		}

		Query aQuery = getQueryFactory().createQuery("construct {?s ??p ?o} where { ?s ??p ?o . " + aFilter + " }");

		if (theSubject != null) {
			aQuery.setParameter("ss", theSubject);
		}

		if (thePredicate != null) {
			aQuery.setParameter("p", thePredicate);
		}

		if (theObject != null) {
			aQuery.setParameter("oo", theObject);
		}

		return (Model) aQuery.getSingleResult();
	}
	
	/**
	 * @inheritDoc
	 */
	public Iterable<Statement> getStatements(Resource theSubject, IRI thePredicate, Value theObject, Resource theContext) throws DataSourceException {
		if (theContext == null) {
			// if context is null, this means any context should match -- we can forward request to getStatements() without context
			return getStatements(theSubject, thePredicate, theObject);
		}
		// Subject and object restrictions are implemented as filters, because some implementations can have problems
		// dealing with bnodes

		String aFilter = "";
		
		if (theSubject != null && theObject != null) {
			aFilter = SUBJECT_OBJECT_FILTER;
		}
		else if (theSubject != null) {
			aFilter = SUBJECT_FILTER;
		}
		else if (theObject != null) {
			aFilter = OBJECT_FILTER;
		}

		// query will work only if the context is set
		Query aQuery = getQueryFactory().createQuery("construct {?s ??p ?o} where { graph ??g { ?s ??p ?o . " + aFilter + "} }");

		if (theSubject != null) {
			aQuery.setParameter("ss", theSubject);
		}

		if (thePredicate != null) {
			aQuery.setParameter("p", thePredicate);
		}

		if (theObject != null) {
			aQuery.setParameter("oo", theObject);
		}
		
		aQuery.setParameter("g", theContext);

		return (Model) aQuery.getSingleResult();
	}

}
