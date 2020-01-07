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

package it.cefriel.chimera.util;

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.impl.StatementImpl;

import com.complexible.common.openrdf.model.Models2;

/**
 * <p>Utility functions for converting between the Jena and Sesame API's</p>
 *
 * @author  Michael Grove
 * @since   0.1
 * @version 1.0
 */
public class JenaSesameUtils {

	/**
	 * Internal model used to create instances of Jena API objects
	 */
	private static final Model mInternalModel = ModelFactory.createDefaultModel();

	/**
	 * Sesame value factory for creating instances of Sesame API objects
	 */
	private static final ValueFactory FACTORY = SimpleValueFactory.getInstance();

	/**
	 * Convert the given Jena Resource into a Sesame Resource
	 * @param theRes the jena resource to convert
	 * @return the jena resource as a sesame resource
	 */
	public static org.eclipse.rdf4j.model.Resource asSesameResource(Resource theRes) {
		if (theRes == null) {
			return null;
		}
		else if (theRes.canAs(Property.class)) {
			return asSesameURI(theRes.as(Property.class));
		}
		else {
			return FACTORY.createBNode(theRes.getId().getLabelString());
		}
	}

	/**
	 * Convert the given Jena Property instance to a Sesame URI instance
	 * @param theProperty the Jena Property to convert
	 * @return the Jena property as a Sesame Instance
	 */
	public static org.eclipse.rdf4j.model.IRI asSesameURI(Property theProperty) {
		if (theProperty == null) {
			return null;
		}
		else {
			return FACTORY.createIRI(theProperty.getURI());
		}
	}

	/**
	 * Convert the given Jena Literal to a Sesame Literal
	 * @param theLiteral the Jena Literal to convert
	 * @return the Jena Literal as a Sesame Literal
	 */
	public static org.eclipse.rdf4j.model.Literal asSesameLiteral(Literal theLiteral) {
		if (theLiteral == null) {
			return null;
		}
		else if (theLiteral.getLanguage() != null && !theLiteral.getLanguage().equals("")) {
			return FACTORY.createLiteral(theLiteral.getLexicalForm(),
										 theLiteral.getLanguage());
		}
		else if (theLiteral.getDatatypeURI() != null) {
			return FACTORY.createLiteral(theLiteral.getLexicalForm(),
										 FACTORY.createIRI(theLiteral.getDatatypeURI()));
		}
		else {
			return FACTORY.createLiteral(theLiteral.getLexicalForm());
		}
	}

	/**
	 * Convert the given Jena node as a Sesame Value
	 * @param theNode the Jena node to convert
	 * @return the jena node as a Sesame Value
	 */
	public static Value asSesameValue(RDFNode theNode) {
		if (theNode == null) {
			return null;
		}
		else if (theNode.canAs(Literal.class)) {
			return asSesameLiteral(theNode.as(Literal.class));
		}
		else {
			return asSesameResource(theNode.as(Resource.class));
		}
	}

	/**
	 * Convert the given Sesame Resource to a Jena Resource
	 * @param theRes the sesame resource to convert
	 * @return the sesame resource as a jena resource
	 */
	public static org.apache.jena.rdf.model.Resource asJenaResource(org.eclipse.rdf4j.model.Resource theRes) {
		if (theRes == null) {
			return null;
		}
		else if (theRes instanceof IRI) {
			return asJenaURI( (IRI) theRes);
		}
		else {
			return mInternalModel.createResource(new AnonId(((BNode) theRes).getID()));
		}
	}

	/**
	 * Convert the sesame value to a Jena Node
	 * @param theValue the Sesame value
	 * @return the sesame value as a Jena node
	 */
	public static RDFNode asJenaNode(Value theValue) {
		if (theValue instanceof org.eclipse.rdf4j.model.Literal) {
			return asJenaLiteral( (org.eclipse.rdf4j.model.Literal) theValue);
		}
		else {
			return asJenaResource( (org.eclipse.rdf4j.model.Resource) theValue);
		}
	}

	/**
	 * Convert the Sesame URI to a Jena Property
	 * @param theURI the sesame URI
	 * @return the URI as a Jena property
	 */
	public static Property asJenaURI(final IRI theURI) {
		if (theURI == null) {
			return null;
		}
		else {
			return mInternalModel.getProperty(theURI.toString());
		}
	}

	/**
	 * Convert a Sesame Literal to a Jena Literal
	 * @param theLiteral the Sesame literal
	 * @return the sesame literal converted to Jena
	 */
	public static org.apache.jena.rdf.model.Literal asJenaLiteral(org.eclipse.rdf4j.model.Literal theLiteral) {
		if (theLiteral == null) {
			return null;
		}
		else if (theLiteral.getLanguage().isPresent()) {
			return mInternalModel.createLiteral(theLiteral.getLabel(),
												theLiteral.getLanguage().get());
		}
		else if (theLiteral.getDatatype() != null) {
			return mInternalModel.createTypedLiteral(theLiteral.getLabel(),
													 theLiteral.getDatatype().toString());
		}
		else {
			return mInternalModel.createLiteral(theLiteral.getLabel());
		}
	}

	/**
	 * Convert the Sesame Graph to a Jena Model
	 * @param theGraph the Graph to convert
	 * @return the set of statements in the Sesame Graph converted and saved in a Jena Model
	 */
	public static Model asJenaModel(org.eclipse.rdf4j.model.Model theGraph) {
		Model aModel = ModelFactory.createDefaultModel();

		for (final org.eclipse.rdf4j.model.Statement aStmt : theGraph) {
			aModel.add(asJenaStatement(aStmt));
		}

		return aModel;
	}

	/**
	 * Convert the Jena Model to a Sesame Graph
	 * @param theModel the model to convert
	 * @return the set of statements in the Jena model saved in a sesame Graph
	 */
	public static org.eclipse.rdf4j.model.Model asSesameGraph(Model theModel) {
		org.eclipse.rdf4j.model.Model aGraph = Models2.newModel();

		StmtIterator sIter = theModel.listStatements();
		while (sIter.hasNext()) {
			aGraph.add(asSesameStatement(sIter.nextStatement()));
		}

		sIter.close();

		return aGraph;
	}

	/**
	 * Convert a Jena Statement to a Sesame statement
	 * @param theStatement the statement to convert
	 * @return the equivalent Sesame statement
	 */
	public static org.eclipse.rdf4j.model.Statement asSesameStatement(Statement theStatement) {
		return new StatementImpl(asSesameResource(theStatement.getSubject()),
								 asSesameURI(theStatement.getPredicate()),
								 asSesameValue(theStatement.getObject()));
	}

	/**
	 * Convert a Sesame statement to a Jena statement
	 * @param theStatement the statemnet to convert
	 * @return the equivalent Jena statement
	 */
	public static Statement asJenaStatement(org.eclipse.rdf4j.model.Statement theStatement) {
		return mInternalModel.createStatement(asJenaResource(theStatement.getSubject()),
											  asJenaURI(theStatement.getPredicate()),
											  asJenaNode(theStatement.getObject()));
	}

//	/**
//	 * An implementation of the Sesame ValueFactory interface which relaxes Sesame's opressive constraint that
//	 * the URI *must* be a valid URI, ie something with a namespace & a local name.
//	 */
//	private static class LaxSesameValueFactory extends ValueFactoryImpl {
//
//		/**
//		 * Creates a new Sesame URI object from the URI string, which can be just a local name, in which case the
//		 * namespace of the URI object will be the empty string.
//		 * @inheritDoc
//		 */
//		@Override
//		public URI createURI(String theURI) {
//			try {
//				return super.createURI(theURI);
//			}
//			catch (IllegalArgumentException e) {
//				return new URIImpl("", theURI);
//			}
//		}
//	}
}
