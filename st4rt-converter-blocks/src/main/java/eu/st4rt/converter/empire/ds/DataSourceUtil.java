/*
 * Copyright (c) 2009-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.ds;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.openrdf.model.Models2;
import com.google.common.collect.Iterables;

import eu.st4rt.converter.empire.Dialect;
import eu.st4rt.converter.empire.Empire;
import eu.st4rt.converter.empire.ds.impl.TripleSourceAdapter;
import eu.st4rt.converter.empire.impl.sparql.ARQSPARQLDialect;
import eu.st4rt.converter.empire.util.EmpireUtil;

/**
 * <p>Collection of utility methods for working with Empire DataSources</p>
 *
 * @author	Michael Grove
 *
 * @since	0.7
 * @version	1.0
 *
 * @see DataSource
 * @see TripleSource
 */
public final class DataSourceUtil {
	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Empire.class.getName());

	/**
	 * No instances
	 */
	private DataSourceUtil() {
	}

	/**
	 * <p>Returns the given {@link DataSource} as a {@link TripleSource}.  If the DataSource does not natively support
	 * the interface, a wrapper is provided that delegates the triple level calls to SPARQL queries.</p>
	 * @param theSource the source
	 * @return the DataSource as a TripleSource.
	 * @see TripleSourceAdapter
	 * @throws DataSourceException if the TripleSource cannot be created.
	 */
	public static TripleSource asTripleSource(DataSource theSource) throws DataSourceException {
		if (theSource == null) {
			throw new DataSourceException("Cannot create triple source from null data source");
		}

		if (theSource instanceof TripleSource) {
			return (TripleSource) theSource;
		}
		else {
			return new TripleSourceAdapter(theSource);
		}
	}

	/**
	 * Do a poor-man's describe on the given resource, querying its context if that is supported, or otherwise
	 * querying the graph in general.
	 * @param theSource the {@link DataSource} to query
	 * @param theObj the object to do the "describe" operation on
	 * @return all the statements about the given object
	 * @throws QueryException if there is an error while querying for the graph
	 */
	public static Model describe(DataSource theSource, Object theObj) throws QueryException {
		String aNG = null;

		if (EmpireUtil.asSupportsRdfId(theObj).getRdfId() == null) {
			return Models2.newModel();
		}

		if (theSource instanceof SupportsNamedGraphs && EmpireUtil.hasNamedGraphSpecified(theObj)) {
			java.net.URI aURI = EmpireUtil.getNamedGraph(theObj);

			if (aURI != null) {
				aNG = aURI.toString();
			}
		}

		Resource aResource = EmpireUtil.asResource(EmpireUtil.asSupportsRdfId(theObj));

		return describe(theSource, aResource, aNG);
	}

	public static Model describe(DataSource theSource, Resource aResource) throws QueryException {
		return describe(theSource, aResource, null);
	}

	public static Model describe(DataSource theSource, Resource aResource, String aNG) throws QueryException {

		Dialect aDialect = theSource.getQueryFactory().getDialect();

		// bnode instabilty in queries will just yield either a parse error or incorrect query results because the bnode
		// will get treated as a variable, and it will just grab the entire database, which is not what we want
		if (aResource instanceof BNode && !(aDialect instanceof ARQSPARQLDialect)) {
			return Models2.newModel();
		}

		// TODO: if source supports describe queries, use that.

		String aSPARQL = "construct {?s ?p ?o}\n" +
				(aNG == null ? "" : "from <" + aNG + ">\n") +
				"where {?s ?p ?o. filter(?s = " + aDialect.asQueryString(aResource) + ")}";
		//				"where {?s ?p ?o. " + /*filter(?s = " + aDialect.asQueryString(aResource) + ")*/ " }";


		String aSeRQL = "construct {s} p {o}\n" +
				(aNG == null ? "from\n" : "from context <" + aNG + ">\n") +
				"{s} p {o} where s = " + aDialect.asQueryString(aResource) + "";

		Model aGraph;

		// fall back on sparql
		aGraph = theSource.graphQuery(aSPARQL);

		return aGraph;
	}

	/**
	 * Do a poor-man's ask on the given resource to see if any triples using the resource (as the subject) exist,
	 * querying its context if that is supported, or otherwise querying the graph in general.
	 * @param theSource the {@link DataSource} to query
	 * @param theObj the object to do the "ask" operation on
	 * @return true if there are statements about the object, false otherwise
	 * @throws QueryException if there is an error while querying for the graph
	 */
	public static boolean exists(DataSource theSource, Object theObj) throws QueryException {
		String aNG = null;

		if (EmpireUtil.asSupportsRdfId(theObj).getRdfId() == null) {
			return false;
		}

		if (theSource instanceof SupportsNamedGraphs && EmpireUtil.hasNamedGraphSpecified(theObj)) {
			java.net.URI aURI = EmpireUtil.getNamedGraph(theObj);

			if (aURI != null) {
				aNG = aURI.toString();
			}
		}

		Dialect aDialect = theSource.getQueryFactory().getDialect();

		String aSPARQL = "select distinct ?s\n" +
				(aNG == null ? "" : "from <" + aNG + ">\n") +
				"where {?s ?p ?o. filter(?s = " + aDialect.asQueryString(EmpireUtil.asResource(EmpireUtil.asSupportsRdfId(theObj))) + ") } limit 1";

		String aSeRQL = "select distinct s\n" +
				(aNG == null ? "from\n" : "from context <" + aNG + ">\n") +
				"{s} p {o} where s = " + aDialect.asQueryString(EmpireUtil.asResource(EmpireUtil.asSupportsRdfId(theObj))) + " limit 1";

		ResultSet aResults;

		// fall back on sparql
		aResults = theSource.selectQuery(aSPARQL);


		try {
			return aResults.stream().findFirst().isPresent();
		}
		finally {
			aResults.close();
		}
	}

	/**
	 * Return the type of the resource in the data source.
	 * @param theSource the data source
	 * @param theConcept the concept whose type to lookup
	 * @return the rdf:type of the concept, or null if there is an error or one cannot be found.
	 */
	public static org.eclipse.rdf4j.model.Resource getType(DataSource theSource, Resource theConcept) {
		Iterable<org.eclipse.rdf4j.model.Resource> aTypes = getTypes(theSource, theConcept);
		if (aTypes == null || Iterables.isEmpty(aTypes)) {
			return null;
		}
		else {
			return Iterables.getFirst(aTypes, null);
		}
	}

	public static Iterable<org.eclipse.rdf4j.model.Resource> getTypes(final DataSource theSource, final Resource theConcept) {
		if (theSource == null) {
			return Collections.emptySet();
		}

		try {
			final Collection<Value> aTypes = getValues(theSource, theConcept, RDF.TYPE);
			if (aTypes.isEmpty()) {
				return Collections.emptySet();
			}
			else {
				return aTypes.stream().map(aType -> (Resource) aType).collect(Collectors.toList());
			}
		}
		catch (DataSourceException e) {
			LOGGER.error("There was an error while getting the type of a resource", e);

			return Collections.emptySet();
		}
	}

	/**
	 * Return the values for the property on the given resource.
	 * @param theSource the data source to query for values
	 * @param theSubject the subject to get property values for
	 * @param thePredicate the property to get values for
	 * @return a collection of all the values of the property on the given resource
	 * @throws DataSourceException if there is an error while querying the data source.
	 */
	public static Collection<Value> getValues(final DataSource theSource, final Resource theSubject, final org.eclipse.rdf4j.model.IRI thePredicate) throws DataSourceException {
		final String aSPARQLQuery = "select ?obj\n" +
				"where {\n" +
				theSource.getQueryFactory().getDialect().asQueryString(theSubject) + " <" + thePredicate.stringValue() + "> ?obj.  }";

		final String aSERQLQuery = "select obj\n" +
				"from\n" +
				"{"+theSource.getQueryFactory().getDialect().asQueryString(theSubject) + "} <" + thePredicate.stringValue() + "> {obj}  ";

		ResultSet aResults = null;

		try {

			aResults = theSource.selectQuery(aSPARQLQuery);

			return aResults.stream().map(theIn -> theIn.getValue("obj"))
					.collect(Collectors.toSet());
		}
		catch (Exception e) {
			throw new DataSourceException(e);
		}
		finally {
			if (aResults != null) {
				aResults.close();
			}
		}
	}

	/**
	 * Return the values for the property on the given resource.
	 * @param theSource the data source to query for values
	 * @param theSubject the subject to get property values for
	 * @param thePredicate the property to get values for
	 * @return the first value of the resource
	 * @throws DataSourceException if there is an error while querying the data source.
	 */
	public static Value getValue(final DataSource theSource, final Resource theSubject, final org.eclipse.rdf4j.model.IRI thePredicate) throws DataSourceException {
		Collection<Value> aValues = getValues(theSource, theSubject, thePredicate);
		if (aValues.isEmpty()) {
			return null;
		}
		else {
			return aValues.iterator().next();
		}
	}
}
