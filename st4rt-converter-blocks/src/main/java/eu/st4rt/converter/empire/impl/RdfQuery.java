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

package eu.st4rt.converter.empire.impl;

import com.complexible.common.base.Dates;
import com.google.common.collect.Lists;

import eu.st4rt.converter.empire.Dialect;
import eu.st4rt.converter.empire.EmpireOptions;
import eu.st4rt.converter.empire.annotation.AnnotationChecker;
import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.annotation.runtime.Proxy;
import eu.st4rt.converter.empire.annotation.runtime.ProxyAwareList;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.ResultSet;
import eu.st4rt.converter.empire.util.BeanReflectUtil;
import eu.st4rt.converter.empire.util.EmpireUtil;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.BindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Implementation of the JPA {@link Query} interface for RDF based query languages.</p>
 *
 * @author	Michael Grove
 * @since 	0.1
 * @version 1.0
 */
public final class RdfQuery implements Query {
	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RdfQuery.class.getName());

	/**
	 * Variable parameter token in queries
	 */
	public static final String VARIABLE_TOKEN = "??";

	/**
	 * Regex for finding the variable token(s) in a query string
	 */
	public static final String VT_RE = "\\?\\?";

	/**
	 * The default name expected to be used in queries to denote what is to be returned as objects from the result
     * set of the query.  Can by changed by specifying a QueryHint with the key {@link #HINT_PROJECTION_VAR}
	 */
    protected static final String MAGIC_PROJECTION_VAR = "result";

    /**
     * Key of the {@link javax.persistence.QueryHint} to specify a different projection var
     * than specified by the default {@link #MAGIC_PROJECTION_VAR}
     */
    public static final String HINT_PROJECTION_VAR = "projection-var";

    /**
     * Key of the {@link javax.persistence.QueryHint} to specify the bean/entity class to be returned by the query.
     */
    public static final String HINT_ENTITY_CLASS = "entity-class";

	/**
	 * The DataSource the query will be executed against
	 */
	private DataSource mSource;

	/**
	 * The raw query string
	 */
	private String mQuery;

	/**
	 * The bean class, this is the type of objects returned by this query
	 */
	private Class mClass;

	/**
	 * Map of parameter index (not string index, their numbered index, eg the first parameter (1), the second (2))
	 * to the value of that parameter
	 */
	private Map<Integer, Value> mIndexedParameters = new HashMap<>();

	/**
	 * Map of parameter names to their values
	 */
	private Map<String, Value> mNamedParameters = new HashMap<>();

	/**
	 * The current limit of the query, or -1 for no limit
	 */
	private int mLimit = -1;

	/**
	 * The current result set offset, or -1 for no offset
	 */
	private int mOffset = -1;

	/**
	 * Whether or not the query results are distinct, the default is true.
	 */
	private boolean mIsDistinct = true;

	/**
	 * Whether or not this is a construct query.
	 */
	private boolean mIsConstruct = false;

	/**
	 * The map of asserted query hints.
	 */
	private Map<String, Object> mHints = new HashMap<>();

	/**
	 * The dialect of the query represented by this query object.
	 */
	private Dialect mQueryDialect;

	private static String UNAMED_VAR_REGEX = VT_RE + "[\\.\\s})]";

	private static String NAMED_VAR_REGEX = VT_RE + "[a-zA-Z0-9_\\-]+";

	/**
	 * Create a new RdfQuery
	 * @param theSource the data source the query is run against
	 * @param theQueryString the query string
	 */
	public RdfQuery(final DataSource theSource, String theQueryString) {
		mSource = theSource;

		mQuery = theQueryString;

		mQueryDialect = theSource.getQueryFactory().getDialect();

		mQueryDialect.validateQueryFormat(getQueryString(), getProjectionVarName());

		// trying to guess if this is a construct query or not.  this is not foolproof, but since the only way of
		// definitely specifying this right now is to cast a query object as an RdfQuery and use setConstruct, that
		// is not ideal.  so we'll take a crack guessing it here.
		if (getQueryString().trim().toLowerCase().startsWith("construct")) {
			setConstruct(true);
		}

		parseParameters();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return query();
	}

	/**
	 * Returns the class of Java beans returned as the results of the executed query.  When no bean class is specified,
	 * raw {@link BindingSet} objects are returned.
	 * @return the class, or null if one is not specified.
	 */
	public Class getBeanClass() {
        if (mClass != null) {
		    return mClass;
        }
        else if (getHints().containsKey(HINT_ENTITY_CLASS)) {
			Object aValue = getHints().get(HINT_ENTITY_CLASS);
            if (aValue instanceof Class) {
                return (Class) aValue;
            }
            else {
                try {
                    return BeanReflectUtil.loadClass(aValue.toString());
                }
                catch (ClassNotFoundException e) {
                    LOGGER.error("Invalid Entity class query set, value not found: " + aValue);
                    return null;
                }
            }
        }
        else {
            return null;
        }
	}

	/**
	 * Sets the class of Java beans returned by executions of this query.
	 * @param theClass the bean class
	 * @return this query object
	 */
	public Query setBeanClass(Class<?> theClass) {
		mClass = theClass;

		return this;
	}

	/**
	 * Return the DataSource the query will be run against.
	 * @return the source
	 * @see DataSource
	 */
	DataSource getSource() {
		return mSource;
	}

	/**
	 * Set the DataSource the query will be run against
	 * @param theSource the new source
	 */
	void setSource(final DataSource theSource) {
		mSource = theSource;
	}

	/**
	 * Return the raw query string as provided by the user.  This will contain un-escaped variables and is likely
	 * to be missing its type (select | construct).
	 * @return the un-modified query string
	 */
	protected String getQueryString() {
		return mQuery;
	}

	/**
	 * Return the result set limit for this query
	 * @return the limit
	 */
	public int getMaxResults() {
		return mLimit;
	}

	/**
	 * Return the current offset of this query
	 * @return the offset index
	 */
	public int getFirstResult() {
		return mOffset;
	}

	/**
	 * Set whether or not to enable the distinct modifier for this query
	 * @param theDistinct true to enable, false otherwise
	 * @return this query instance
	 */
	public Query setDistinct(boolean theDistinct) {
		mIsDistinct = theDistinct;

		return this;
	}

	/**
	 * Return whether or not the distinct modifier is enabled for this query
	 * @return true if the results will be distinct, false otherwise
	 */
	public boolean isDistinct() {
		return mIsDistinct;
	}

	/**
	 * Set whether or not this query object represents a construct query.
	 * @param theConstruct true to set this as a construct query, false otherwise
	 * @return this query instance
	 * @see #isConstruct
	 */
	public Query setConstruct(boolean theConstruct) {
		mIsConstruct = theConstruct;
		return this;
	}

	/**
	 * Return whether or not this is a construct query.  If this is an instance of a construct query, getSingleResult
	 * will return a {@link Graph} and getResultList will return a List with a single element which is an instance of
	 * Graph.  Otherwise, when it's a select query, these will return a single
	 * {@link BindingSet}, or a list of Bindings (or instances of
	 * the Bean class, when specified) respectively.
	 * @return true if this is a construct query, false otherwise.
	 */
	public boolean isConstruct() {
		return mIsConstruct;
	}

	/**
	 * Execute the describe query.
	 * @return the resulting RDF graph
	 * @throws QueryException if there is an error while querying
	 */
	public Model executeDescribe() throws QueryException {
		return getSource().describe(query());
	}

	/**
	 * Execute an ask query.
	 * @return the boolean result of the ask query
	 * @throws QueryException if there is an error while querying
	 */
	public boolean executeAsk() throws QueryException {
		return getSource().ask(query());
	}

	/**
	 * Performs a select query
	 * @return the result set
	 * @throws QueryException if there is an error while querying
	 */
	public ResultSet executeSelect() throws QueryException {
		return getSource().selectQuery(query());
	}

	/**
	 * Performs a construct query
	 * @return the result graph
	 * @throws QueryException if there is an error while querying
	 */
	public Model executeConstruct() throws QueryException {
		return getSource().graphQuery(query());
	}

	/**
	 * @inheritDoc
	 */
	@SuppressWarnings("unchecked")
	public List getResultList() {
		List aList = new ProxyAwareList();

		try {
			if (isConstruct()) {
				Model aGraph = getSource().graphQuery(query());
				aList.add(aGraph);
			}
			else {

				try (ResultSet aResults = getSource().selectQuery(query())) {
					if (getBeanClass() != null) {
						// for now, by convention, for this to work like the JPQL stuff where you do something like
						// "from Product pr join pr.poc as p where p.id = ?" and expect to get a list of Product instances
						// back as the result set, you *MUST* have a var in the projection called 'result' which is
						// the URI of the things you want to get back; when you don't do this, we prefix your partial query
						// with this string
						while (aResults.hasNext()) {
							BindingSet aBS = aResults.next();

							Object aObj;

							String aVarName = getProjectionVarName();

							if (aBS.getValue(aVarName) instanceof IRI && AnnotationChecker.isValid(getBeanClass())) {
								if (EmpireOptions.ENABLE_QUERY_RESULT_PROXY) {
									aObj = new Proxy(getBeanClass(), EmpireUtil.asPrimaryKey(aBS.getValue(aVarName)), getSource());
								}
								else {
									aObj = RdfGenerator.fromRdf(getBeanClass(),
									                            EmpireUtil.asPrimaryKey(aBS.getValue(aVarName)),
									                            getSource());
								}
							}
							else {
								aObj = new RdfGenerator.ValueToObject(getSource(), null,
								                                      getBeanClass(), null).apply(aBS.getValue(aVarName));
							}

							// if the object could not be created, or it was and its not the bean class type, or not a proxy
							// for something of the bean class type, then we could not bind the value in the result set
							// which is an error.
							if (aObj == null
							    || !(getBeanClass().isInstance(aObj) || (aObj instanceof Proxy && getBeanClass().isAssignableFrom(((Proxy) aObj)
								                                                                                                      .getProxyClass())))) {
								throw new PersistenceException("Cannot bind query result to bean: " + getBeanClass());
							}
							else {
								aList.add(aObj);
							}
						}
					}
					else {
						aList.addAll(Lists.newArrayList(aResults));
					}
				}
			}
		}
		catch (Exception e) {
			throw new PersistenceException(e);
		}

		return aList;
	}

	/**
	 * Returns the name of the projection variable that is to represent the return value of the query.  By default
	 * this is {@link #MAGIC_PROJECTION_VAR} but you can override this by setting the {@link #HINT_PROJECTION_VAR}
	 * QueryHint value.
	 * @return the name of the projection variable to grab
	 */
	protected String getProjectionVarName() {
        if (getHints().containsKey(HINT_PROJECTION_VAR)) {
            return getHints().get(HINT_PROJECTION_VAR).toString();
        }
        else {
            return MAGIC_PROJECTION_VAR;
        }
    }

	/**
	 * @inheritDoc
	 */
	public Object getSingleResult() {
		List aResults = getResultList();

		if (aResults == null || aResults.isEmpty()) {
			throw new NoResultException();
		}
		else if (aResults.size() > 1) {
			throw new NonUniqueResultException();
		}

		return aResults.get(0);
	}

	/**
	 * @inheritDoc
	 */
	public int executeUpdate() {
		throw new UnsupportedOperationException("Update operations are not supported.");
	}

	/**
	 * @inheritDoc
	 */
	public Query setMaxResults(final int theLimit) {
		mLimit = theLimit;

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setFirstResult(final int theOffset) {
		mOffset = theOffset;

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setHint(final String theName, final Object theObj) {
		mHints.put(theName, theObj);

		return this;
	}

	/**
	 * Return a map of the current query hints
	 * @return the query hints
	 */
	protected Map<String, Object> getHints() {
		return mHints;
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final String theName, final Object theObj) {
		validateParameterName(theName);

		mNamedParameters.put(theName, validateParameterValue(theObj));

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final String theName, final Date theDate, final TemporalType theTemporalType) {
		Calendar aCal = Calendar.getInstance();
		aCal.setTime(theDate);

		return setParameter(theName, aCal, theTemporalType);
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final String theName, final Calendar theCalendar, final TemporalType theTemporalType) {
		validateParameterName(theName);

		Value aValue = asValue(theCalendar, theTemporalType);

		mNamedParameters.put(theName, aValue);

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final int theIndex, final Object theValue) {
		validateParameterIndex(theIndex);

		mIndexedParameters.put(theIndex, validateParameterValue(theValue));

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final int theIndex, final Date theDate, final TemporalType theTemporalType) {
		validateParameterIndex(theIndex);

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setParameter(final int theIndex, final Calendar theCalendar, final TemporalType theTemporalType) {
		validateParameterIndex(theIndex);

		return this;
	}

	/**
	 * @inheritDoc
	 */
	public Query setFlushMode(final FlushModeType theFlushModeType) {
		if (theFlushModeType != FlushModeType.AUTO) {
			throw new IllegalArgumentException("Commit style flush mode not supported");
		}

		return this;
	}

	/**
	 * Return the given date object with the specified temporal type as a {@link Value}
	 * @param theDate the date
	 * @param theTemporalType the type to extract from the date
	 * @return the time w.r.t to the TemportalType as a Value
	 */
	private Value asValue(final Calendar theDate, final TemporalType theTemporalType) {
		Value aValue = null;

		switch (theTemporalType) {
			case DATE:
				aValue = SimpleValueFactory.getInstance().createLiteral(Dates.date(theDate.getTime()), XMLSchema.DATE);
				break;
			case TIME:
				aValue = SimpleValueFactory.getInstance().createLiteral(Dates.datetime(theDate.getTime()), XMLSchema.TIME);
				break;
			case TIMESTAMP:
				aValue = SimpleValueFactory.getInstance().createLiteral("" + theDate.getTime().getTime(), XMLSchema.TIME);
				break;
		}

		return aValue;
	}

	/**
	 * Validate that a parameter with the given name exists
	 * @param theName the parameter name to validate
	 * @throws IllegalArgumentException thrown if a parameter with the given name does not exist
	 */
	private void validateParameterName(String theName) {
		if (!mNamedParameters.containsKey(theName)) {
			throw new IllegalArgumentException("Parameter with name '" + theName + "' does not exist");
		}
	}

	/**
	 * Validate that the specified instance is a {@link Value} or can be
	 * {@link empire.annotation.RdfGenerator.AsValueFunction turned into one}
	 * @param theValue the instance to validate
	 * @return the validated value
	 */
	private Value validateParameterValue(Object theValue) {
		if (!(theValue instanceof Value)) {
			try {
				return new RdfGenerator.AsValueFunction().apply(theValue);
			}
			catch (RuntimeException e) {
				// this is currently what is thrown when the function cannot transform the value
				throw new IllegalArgumentException(e);
			}
		}
		else {
			return (Value) theValue;
		}
	}

	/**
	 * Validate that a parameter at the given index exists
	 * @param theIndex the index to validate
	 * @throws IllegalArgumentException if a parameter at the given index does not exist
	 */
	private void validateParameterIndex(int theIndex) {
		if (!mIndexedParameters.containsKey(theIndex)) {
			throw new IllegalArgumentException("Parameter at index " + theIndex + " does not exist.");
		}
	}

	/**
	 * Given the query string fragment, replace all variable parameter tokens with the values specified by the user
	 * through the various setParameter methods.
	 * @param theQuery the query fragment
	 * @return the query string with all parameter variables replaced
	 * @see #setParameter
	 */
	private String insertVariables(String theQuery) {
		String aBuffer = theQuery;

		for (String aName : mNamedParameters.keySet()) {
			boolean containsParam = Pattern.compile(VT_RE+aName).matcher(aBuffer).find();
			if (mNamedParameters.get(aName) != null && containsParam) {
				aBuffer = replaceVariable(aBuffer, aName, mNamedParameters.get(aName));
			}
		}

		int aIndex = 1;
		while (aBuffer.contains(VARIABLE_TOKEN)) {
			boolean containsParam = Pattern.compile(VT_RE).matcher(aBuffer).find();
			if (mIndexedParameters.get(aIndex) != null && containsParam) {
				aBuffer = aBuffer.replaceFirst(VT_RE, mQueryDialect.asQueryString(mIndexedParameters.get(aIndex++)));
			}
			else {
				break;
			}
		}

		return aBuffer;
	}

	private String replaceVariable(String theQuery, String theVariable, Value theValue) {
		// using this instead of replaceAll -- which keeps giving group does not exist errors.  I think my regex must
		// be subtly (is that a word?) wrong and I'm just not seeing it.  This works, for now.

		StringBuilder aQueryBuffer = new StringBuilder();

		Matcher m = Pattern.compile(VT_RE+theVariable).matcher(theQuery);

		int start = 0;
		while (m.find()) {
			aQueryBuffer.append(theQuery.substring(start, m.start()));
			aQueryBuffer.append(mQueryDialect.asQueryString(theValue));

			start = m.start() + m.group(0).length();
		}

		aQueryBuffer.append(theQuery.substring(start));

		return aQueryBuffer.toString();
	}

	/**
	 * Given a query fragment from {@link #getQueryString} pull out all the variable parameters
	 */
	private void parseParameters() {
		mNamedParameters.clear();
		mIndexedParameters.clear();

		Matcher aMatcher = Pattern.compile(UNAMED_VAR_REGEX).matcher(getQueryString());

		// i'm pretty sure the JPA stuff is 1-indexed rather than the normal 0-indexed
		int aIndex = 1;
		while (aMatcher.find()) {
			mIndexedParameters.put(aIndex++, null);
		}

		aMatcher = Pattern.compile(NAMED_VAR_REGEX).matcher(getQueryString());

		while (aMatcher.find()) {
			mNamedParameters.put(getQueryString().substring(aMatcher.start() + VARIABLE_TOKEN.length(), aMatcher.end()), null);
		}
	}

	protected boolean startsWithKeyword(String theQuery) {
		String q = theQuery.toLowerCase().trim();
		return q.startsWith("select") || q.startsWith("construct") || q.startsWith("ask") || q.startsWith("describe");
	}

	/**
	 * Return a valid, executable query instance from the specified query fragment, and user specified settings such
	 * as parameter values, limit, offset, etc.
	 * @return a valid query that can be run against a DataSource
	 */
	protected String query() {
		// use some regexs to look for and remove limits and offsets specified in the query string and store them locally
		// these will get postfixed to the query later on.
		boolean containsLimit = Pattern.compile("limit(\\s)*[0-9]+[^}]*").matcher(getQueryString()).find();
		boolean containsOffset = Pattern.compile("offset(\\s)*[0-9]+[^}]*").matcher(getQueryString()).find();

		if (containsLimit) {
			String aLimitGrabRegex = "limit(\\s)*[0-9]+";
			Matcher m = Pattern.compile(aLimitGrabRegex).matcher(getQueryString());

			if (m.find() && getMaxResults() == -1) {
				setMaxResults(Integer.parseInt(m.group(0).split(" ")[1]));
			}

			mQuery = mQuery.replaceAll(aLimitGrabRegex, "");
		}

		if (containsOffset) {
			String aOffsetGrabRegex = "offset(\\s)*[0-9]+";
			Matcher m = Pattern.compile(aOffsetGrabRegex).matcher(getQueryString());

			if (m.find() && getFirstResult() == -1) {
				setFirstResult(Integer.parseInt(m.group(0).split(" ")[1]));
			}
			
			mQuery = mQuery.replaceAll(aOffsetGrabRegex, "");
		}

		String queryStr = insertVariables(getQueryString()).trim();

		queryStr = replaceUnusedVariableTokens(queryStr);

//		validateVariables();

		// TODO: should we get the values for the keywords used here (select, distinct, construct, limit, offset) from
		// the subclass rather than hard coding them?  or will these be the same for all rdf based query languages?

		StringBuffer aQuery = new StringBuffer(queryStr);

        if (!aQuery.toString().toLowerCase().startsWith(mQueryDialect.patternKeyword())
			&& !startsWithKeyword(aQuery.toString())) {
            aQuery.insert(0, mQueryDialect.patternKeyword());
        }

        StringBuilder aStart = new StringBuilder();
		if (!startsWithKeyword(getQueryString())) {
			aStart.insert(0, isConstruct() ? "construct " : "select ").append(isDistinct() ? " distinct " : "").append(" ");
			
			if (isConstruct()) {
				aStart.append(" * ");
			}
			else {
				aStart.append(mQueryDialect.asProjectionVar(getProjectionVarName())).append(" ");
			}
		}

        aQuery.insert(0, aStart.toString());

		if (getMaxResults() != -1) {
			aQuery.append(" limit ").append(getMaxResults());
		}

		if (getFirstResult() != -1) {
			aQuery.append(" offset ").append(getFirstResult());
		}

		mQueryDialect.insertNamespaces(aQuery);

		return aQuery.toString();
	}

	/**
	 * Replaces all unused variable place holders with syntactically correct replacements.  Unnamed variable tokens "??"
	 * in the query string get replaced with "[]" and named variable tokens "??foo" get turned into normal variables,
	 * e.g. "?foo"
	 * @param theQuery the query
	 * @return the query w/ unused variables replaced w/ the appropriate equivalents.
	 */
	private String replaceUnusedVariableTokens(String theQuery) {
		StringBuilder aQueryBuffer = new StringBuilder();

		Matcher m = Pattern.compile(NAMED_VAR_REGEX).matcher(theQuery);

		int start = 0;
		while (m.find()) {
			aQueryBuffer.append(theQuery.substring(start, m.start()));
			aQueryBuffer.append(mQueryDialect.asVar(m.group(0).replaceAll(VT_RE, "")));

			start = m.start() + m.group(0).length();
		}

		aQueryBuffer.append(theQuery.substring(start));

		return aQueryBuffer.toString().replaceAll(UNAMED_VAR_REGEX, mQueryDialect.asVar(null) + " ");
	}
}
