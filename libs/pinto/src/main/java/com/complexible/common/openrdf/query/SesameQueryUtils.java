/*
 * Copyright (c) 2009-2015 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.openrdf.query;



import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.algebra.ProjectionElem;
import org.eclipse.rdf4j.query.algebra.Slice;
import org.eclipse.rdf4j.query.algebra.TupleExpr;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.QueryModelVisitorBase;
import org.eclipse.rdf4j.query.parser.ParsedQuery;

import com.google.common.collect.Sets;

/**
 * <p>Collection of utility methods for working with the OpenRdf Sesame Query API.</p>
 *
 * @author	Michael Grove
 * @since	0.2
 * @version 0.8
 */
public final class SesameQueryUtils {

	/**
	 * No instances
	 */
	private SesameQueryUtils() {
		throw new AssertionError();
	}

	/**
	 * Provide cast-exception safe access to a (@link URI} value in a {@link BindingSet}
	 * @param theBindingSet	the BindingSet
	 * @param theKey		the binding name
	 * @return				the URI value for the key, or null if the key did not have a binding or if it was not a URI
	 */
	public static IRI getIRI(final BindingSet theBindingSet, final String theKey) {
		Value aVal = theBindingSet.getValue(theKey);
		if (aVal instanceof IRI) {
			return (IRI) aVal;
		}
		else {
			return null;
		}
	}

	/**
	 * Provide cast-exception safe access to a {@link Literal} value in a {@link BindingSet}
	 *
	 * @param theBindingSet	the BindingSet
	 * @param theKey		the binding name
	 * @return				the Literal value for the key, or null if the key did not have a binding or if it was not a Literal
	 */
	public static Literal getLiteral(final BindingSet theBindingSet, final String theKey) {
		Value aVal = theBindingSet.getValue(theKey);
		if (aVal instanceof Literal) {
			return (Literal) aVal;
		}
		else {
			return null;
		}
	}

	/**
	 * Provide cast-exception safe access to a {@link Resource} value in a {@link BindingSet}
	 * @param theBindingSet	the BindingSet
	 * @param theKey		the binding name
	 * @return				the Resource value for the key, or null if the key did not have a binding or if it was not a Resource
	 */
	public static Resource getResource(final BindingSet theBindingSet, final String theKey) {
		Value aVal = theBindingSet.getValue(theKey);
		if (aVal instanceof Resource) {
			return (Resource) aVal;
		}
		else {
			return null;
		}
	}

	/**
	 * Provide cast-exception safe access to a {@link BNode} value in a {@link BindingSet}
	 * @param theBindingSet	the BindingSet
	 * @param theKey		the binding name
	 * @return				the BNode value for the key, or null if the key did not have a binding or if it was not a BNode
	 */
	public static BNode getBNode(final BindingSet theBindingSet, final String theKey) {
		Value aVal = theBindingSet.getValue(theKey);
		if (aVal instanceof BNode) {
			return (BNode) aVal;
		}
		else {
			return null;
		}
	}

	/**
	 * Return the list of vars used in the projection of the provided TupleExpr
	 * @param theExpr	the query expression
	 * @return			the vars in the projection
	 */
	public static Collection<String> getProjection(TupleExpr theExpr) {
		final Collection<String> aVars = Sets.newHashSet();

		try {
			theExpr.visit(new AbstractQueryModelVisitor<RuntimeException>() {
				@Override
				public void meet(final ProjectionElem theProjectionElem) throws RuntimeException {
					super.meet(theProjectionElem);

					aVars.add(theProjectionElem.getTargetName());
					aVars.add(theProjectionElem.getSourceName());
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return aVars;
	}

	/**
	 * Return the query string rendering of the {@link Value}
	 * @param theValue	the value to render
	 * @return 			the value rendered in its query string representation
	 */
	public static String getARQSPARQLQueryString(Value theValue) {
        StringBuilder aBuffer = new StringBuilder();

        if (theValue instanceof IRI) {
	        IRI aURI = (IRI) theValue;
            aBuffer.append("<").append(aURI.toString()).append(">");
        }
        else if (theValue instanceof BNode) {
            aBuffer.append("<_:").append(((BNode)theValue).getID()).append(">");
        }
        else if (theValue instanceof Literal) {
            Literal aLit = (Literal)theValue;

            aBuffer.append("\"\"\"").append(escape(aLit.getLabel())).append("\"\"\"").append(aLit.getLanguage().isPresent() ? "@" + aLit.getLanguage().get() : "");

            if (aLit.getDatatype() != null && !Literals.isLanguageLiteral(aLit)) {
                aBuffer.append("^^<").append(aLit.getDatatype().toString()).append(">");
            }
        }

        return aBuffer.toString();
	}

	/**
	 * Return the query string rendering of the {@link Value}
	 * @param theValue	the value to render
	 * @return 			the value rendered in its query string representation
	 */
	public static String getSPARQLQueryString(Value theValue) {
        StringBuilder aBuffer = new StringBuilder();

        if (theValue instanceof IRI) {
	        IRI aURI = (IRI) theValue;
            aBuffer.append("<").append(aURI.toString()).append(">");
        }
        else if (theValue instanceof BNode) {
            aBuffer.append("_:").append(((BNode)theValue).getID());
        }
        else if (theValue instanceof Literal) {
            Literal aLit = (Literal)theValue;

	        aBuffer.append("\"\"\"").append(escape(aLit.getLabel())).append("\"\"\"").append(aLit.getLanguage().isPresent() ? "@" + aLit.getLanguage().get() : "");

	        if (aLit.getDatatype() != null && !Literals.isLanguageLiteral(aLit)) {
		        aBuffer.append("^^<").append(aLit.getDatatype().toString()).append(">");
	        }
        }

        return aBuffer.toString();
	}
	
	/**
	 * Return the query string rendering of the {@link Value}
	 * @param theValue	the value to render
	 * @return 			the value rendered in its query string representation
	 */
	public static String getSerqlQueryString(Value theValue) {
        StringBuilder aBuffer = new StringBuilder();

        if (theValue instanceof IRI) {
	        IRI aURI = (IRI) theValue;
            aBuffer.append("<").append(aURI.toString()).append(">");
        }
        else if (theValue instanceof BNode) {
            aBuffer.append("_:").append(((BNode)theValue).getID());
        }
        else if (theValue instanceof Literal) {
            Literal aLit = (Literal)theValue;

            aBuffer.append("\"").append(escape(aLit.getLabel())).append("\"").append(aLit.getLanguage() != null ? "@" + aLit.getLanguage() : "");

            if (aLit.getDatatype() != null && aLit.getLanguage() == null) {
                aBuffer.append("^^<").append(aLit.getDatatype().toString()).append(">");
            }
        }

        return aBuffer.toString();
	}

	/**
	 * Properly escape out any special characters in the query string.  Replaces unescaped double quotes with \" and replaces slashes '\' which
	 * are not a valid escape sequence such as \t or \n with a double slash '\\' so they are unescaped correctly by a SPARQL parser.
	 * 
	 * @param theString	the query string to escape chars in
	 * @return 			the escaped query string
	 */
	public static String escape(String theString) {
		theString = theString.replaceAll("\"", "\\\\\"");
		
		StringBuffer aBuffer = new StringBuffer();
		Matcher aMatcher = Pattern.compile("\\\\([^tnrbf\"'\\\\])").matcher(theString);
		while (aMatcher.find()) {
			aMatcher.appendReplacement(aBuffer, String.format("\\\\\\\\%s", aMatcher.group(1)));
		}
		aMatcher.appendTail(aBuffer);

		return aBuffer.toString();
	}

    /**
     * Set the value of the limit on the query object to a new value, or specify a limit if one is not specified.
     * @param theQuery the query to alter
     * @param theLimit the new limit
     */
    public static void setLimit(final ParsedQuery theQuery, final int theLimit) {
        try {
            SliceMutator aLimitSetter = SliceMutator.changeLimit(theLimit);
            theQuery.getTupleExpr().visit(aLimitSetter);

            if (!aLimitSetter.limitWasSet()) {
                Slice aSlice = new Slice();

                aSlice.setLimit(theLimit);
                aSlice.setArg(theQuery.getTupleExpr());

                theQuery.setTupleExpr(aSlice);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the value of the limit on the query object to a new value, or specify a limit if one is not specified.
     * @param theQuery	the query to alter
     * @param theOffset	the new limit
     */
    public static void setOffset(final ParsedQuery theQuery, final int theOffset) {
        try {
            SliceMutator aLimitSetter = SliceMutator.changeOffset(theOffset);
            theQuery.getTupleExpr().visit(aLimitSetter);

            if (!aLimitSetter.offsetWasSet()) {
                Slice aSlice = new Slice(theQuery.getTupleExpr());

                aSlice.setOffset(theOffset);

                theQuery.setTupleExpr(aSlice);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
     * Implementation of a {@link org.openrdf.query.algebra.QueryModelVisitor} which will set the limit or offset of a query
     * object to the provided value.  If there is no slice operator specified, {@link #limitWasSet} and {@link #offsetWasSet} will return false.
     */
    private static class SliceMutator extends QueryModelVisitorBase<Exception> {
        /**
         * Whether or not the limit was set on the query object
         */
        private boolean mLimitWasSet = false;

		/**
		 * Whether or not the offset was set on the query object
		 */
		private boolean mOffsetWasSet = false;

        /**
         * The new limit for the query
         */
        private final int mNewLimit;

		/**
		 * The new offset for the query
		 */
		private final int mNewOffset;

        /**
         * Create a new SetLimit object
         * @param theNewLimit 	the new limit to use for the query, or -1 to not set
		 * @param theNewOffset	the new offset to use for the query, or -1 to not set
         */
        private SliceMutator(final int theNewLimit, final int theNewOffset) {
            mNewLimit = theNewLimit;
			mNewOffset = theNewOffset;
        }

		static SliceMutator changeLimit(final int theNewLimit) {
			return new SliceMutator(theNewLimit, -1);
		}

		static SliceMutator changeOffset(final int theNewOffset) {
			return new SliceMutator(-1, theNewOffset);
		}

		static SliceMutator changeLimitAndOffset(final int theNewLimit, final int theNewOffset) {
			return new SliceMutator(theNewLimit, theNewOffset);
		}

		/**
         * Resets the state of this visitor so it can be re-used.
         */
        public void reset() {
            mLimitWasSet = false;
			mOffsetWasSet = false;
        }

        /**
         * Return whether or not the limit was set by this visitor
         * @return true if the limit was set, false otherwse
         */
        public boolean limitWasSet() {
            return mLimitWasSet;
        }

		/**
		 * Retun whether or not the offset was set by this visitor
		 * @return true of the offset was set, false otherwise
		 */
		public boolean offsetWasSet() {
			return mOffsetWasSet;
		}

        /**
         * @inheritDoc
         */
        @Override
        public void meet(Slice theSlice) {
			if (mNewLimit > 0) {
            	mLimitWasSet = true;
            	theSlice.setLimit(mNewLimit);
			}

			if (mNewOffset > 0) {
				mOffsetWasSet = true;
				theSlice.setOffset(mNewOffset);
			}
        }
    }
}
