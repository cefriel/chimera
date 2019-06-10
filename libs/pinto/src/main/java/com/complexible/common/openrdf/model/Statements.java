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

package com.complexible.common.openrdf.model;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.turtle.TurtleUtil;


/**
 * <p>Some common Function implementations for working with Statements</p>
 *
 * @author  Michael Grove
 * @since	0.4.1
 * @version	4.0
 */
public final class Statements {

	/**
	 * No instances
	 */
	private Statements() {
		throw new AssertionError();
	}

	public static Predicate<Statement> subjectIs(final Resource theSubj) {
		return theStmt -> {
			return Objects.equals(theStmt.getSubject(), theSubj);
		};
	}

	public static Predicate<Statement> predicateIs(final IRI thePred) {
		return theStmt -> {
			return Objects.equals(theStmt.getPredicate(), thePred);
		};
	}

	public static Predicate<Statement> objectIs(final Value theObj) {
		return theStmt -> {
			return Objects.equals(theStmt.getObject(), theObj);
		};
	}

	public static Predicate<Statement> contextIs(final Resource theContext) {
		return theStmt -> {
			return Objects.equals(theStmt.getContext(), theContext);
		};
	}

	public static Predicate<Statement> objectIs(final Class<? extends Value> theValue) {
		return theStmt -> {
			return theValue.isInstance(theStmt.getObject());
		};
	}

	public static Function<Statement, Statement> applyContext(final Resource theContext) {
		return applyContext(theContext, SimpleValueFactory.getInstance());
	}

	public static Function<Statement, Statement> applyContext(final Resource theContext, final ValueFactory theValueFactory) {
		return theStmt -> {
			if (Objects.equals(theContext, theStmt.getContext())) {
				return theStmt;
			}
			else {
				return theValueFactory.createStatement(theStmt.getSubject(), theStmt.getPredicate(), theStmt.getObject(), theContext);
			}
		};
	}

	public static Predicate<Statement> matches(final Resource theSubject, final IRI thePredicate, final Value theObject,
	                                           final Resource... theContexts) {
		return theStatement -> {
			if (theSubject != null && !theSubject.equals(theStatement.getSubject())) {
				return false;
			}
			if (thePredicate != null && !thePredicate.equals(theStatement.getPredicate())) {
				return false;
			}
			if (theObject != null && !theObject.equals(theStatement.getObject())) {
				return false;
			}

			if (theContexts == null || theContexts.length == 0) {
				// no context specified, SPO were all equal, so this is equals as null/empty context is a wildcard
				return true;
			}
			else {
				Resource aContext = theStatement.getContext();

				for (Resource aCxt : theContexts) {
					if (aCxt == null && aContext == null) {
						return true;
					}
					if (aCxt != null && aCxt.equals(aContext)) {
						return true;
					}
				}

				return false;
			}
		};
	}

	public static Function<Statement, Optional<Resource>> subjectOptional() {
		return theStatement -> Optional.of(theStatement.getSubject());
	}

	public static Function<Statement, Optional<IRI>> predicateOptional() {
		return theStatement -> Optional.of(theStatement.getPredicate());
	}

	public static Function<Statement, Optional<Value>> objectOptional() {
		return theStatement -> Optional.of(theStatement.getObject());
	}

	public static Function<Statement, Optional<Literal>> objectAsLiteral() {
		return theStatement -> theStatement.getObject() instanceof Literal ? Optional.of((Literal) theStatement.getObject())
		                                                                   : Optional.<Literal>empty();
	}

	public static Function<Statement, Optional<Resource>> objectAsResource() {
		return theStatement -> theStatement.getObject() instanceof Resource ? Optional.of((Resource) theStatement.getObject())
		                                                                    : Optional.<Resource>empty();
	}

	public static Function<Statement, Optional<Resource>> contextOptional() {
		return theStatement -> theStatement.getContext() != null ? Optional.of(theStatement.getContext())
		                                                         : Optional.<Resource>empty();
	}

	/**
	 * Return whether or not the literal object is valid.  This will return true if the literal represented by this
	 * object would have been parseable.  Used to validate input coming in from users from non-IO sources (which get
	 * validated via the fact they got parsed).
	 *
	 * Validates the language tag is not malformed and basic XSD datatype checks.
	 *
	 * @param theLiteral	the literal to validate
	 *
	 * @return 				true if its a valid/parseable literal, false otherwise
	 */
	public static boolean isLiteralValid(final Literal theLiteral) {
		if (Literals.isLanguageLiteral(theLiteral)) {
			final String aLang = theLiteral.getLanguage().get();

			if (!TurtleUtil.isLanguageStartChar(aLang.charAt(0))) {
				return false;
			}

			for (int aIndex = 1; aIndex < aLang.length(); aIndex++) {
				if (!TurtleUtil.isLanguageChar(aLang.charAt(aIndex))) {
					return false;
				}
			}
		}

		// TODO: all datatypes?  all variations?
		if (theLiteral.getDatatype() != null && theLiteral.getDatatype().getNamespace().equals(XMLSchema.NAMESPACE)) {
			final String aTypeName = theLiteral.getDatatype().getLocalName();

			try {
				if (aTypeName.equals(XMLSchema.DATETIME.getLocalName())) {
					theLiteral.calendarValue();
				}
				else if (aTypeName.equals(XMLSchema.INT.getLocalName())) {
					theLiteral.intValue();
				}
				else if (aTypeName.equals(XMLSchema.FLOAT.getLocalName())) {
					theLiteral.floatValue();
				}
				else if (aTypeName.equals(XMLSchema.LONG.getLocalName())) {
					theLiteral.longValue();
				}
				else if (aTypeName.equals(XMLSchema.DOUBLE.getLocalName())) {
					theLiteral.doubleValue();
				}
				else if (aTypeName.equals(XMLSchema.SHORT.getLocalName())) {
					theLiteral.shortValue();
				}
				else if (aTypeName.equals(XMLSchema.BOOLEAN.getLocalName())) {
					theLiteral.booleanValue();
				}
				else if (aTypeName.equals(XMLSchema.BYTE.getLocalName())) {
					theLiteral.byteValue();
				}
				else if (aTypeName.equals(XMLSchema.DECIMAL.getLocalName())) {
					theLiteral.decimalValue();
				}
			}
			catch (Exception e) {
				return false;
			}
		}

		return true;
	}
}
