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

package com.complexible.common.openrdf.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import com.complexible.common.openrdf.model.Models2;
import com.google.common.collect.Sets;

/**
 * <p>Utility class for creating statements about a particular resource.</p>
 *
 * @author	Michael Grove
 * @since	0.1
 * @version	4.0
 */
public class ResourceBuilder {
    private final Model mGraph;
    private final Resource mRes;
    private final ValueFactory mValueFactory;

    public ResourceBuilder(final Resource theRes) {
        this(Models2.newModel(), SimpleValueFactory.getInstance(), theRes);
    }

    protected ResourceBuilder(final Model theGraph, final ValueFactory theValueFactory, final Resource theRes) {
        mRes = theRes;
        mGraph = theGraph;
        mValueFactory = theValueFactory;
    }

	public ResourceBuilder addProperty(IRI theProperty, java.net.URI theURI) {
		return addProperty(theProperty, mValueFactory.createIRI(theURI.toString()));
	}

	public ResourceBuilder addProperty(IRI theProperty, List<? extends Value> theList) {
		Resource aListRes = mValueFactory.createBNode();

		mGraph.add(getResource(), theProperty, aListRes);

		Iterator<? extends Value> aResIter = theList.iterator();
		while (aResIter.hasNext()) {
			mGraph.add(aListRes, RDF.FIRST, aResIter.next());
			if (aResIter.hasNext()) {
				BNode aNextListElem = mValueFactory.createBNode();
				mGraph.add(aListRes, RDF.REST, aNextListElem);
				aListRes = aNextListElem;
			}
			else {
				mGraph.add(aListRes, RDF.REST, RDF.NIL);
			}
		}

		return this;
	}

    public ResourceBuilder addProperty(IRI theProperty, Value theValue) {
        if (theValue != null) {
            mGraph.add(mRes, theProperty, theValue);
        }

        return this;
    }

    public Resource getResource() {
        return mRes;
    }

    public Model model() {
        return mGraph;
    }

    public ResourceBuilder addProperty(IRI theProperty, ResourceBuilder theBuilder) {
        if (theBuilder != null) {
            addProperty(theProperty, theBuilder.getResource());

            mGraph.addAll(Sets.newHashSet(theBuilder.mGraph));
        }

        return this;
    }

    public ResourceBuilder addProperty(IRI theProperty, String theValue) {
		if (theValue != null) {
			return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

    public ResourceBuilder addProperty(IRI theProperty, Integer theValue) {
		if (theValue != null) {
        	return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

    public ResourceBuilder addProperty(IRI theProperty, Long theValue) {
		if (theValue != null) {
	        return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

    public ResourceBuilder addProperty(IRI theProperty, Short theValue) {
		if (theValue != null) {
	        return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

    public ResourceBuilder addProperty(IRI theProperty, Double theValue) {
		if (theValue != null) {
        	return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

	/**
	 * Add a xsd:dateTime property to the resource
	 *
	 * @param theProperty   the property
	 * @param theValue      the date-time object
	 * @return              this builder
	 */
	public ResourceBuilder addProperty(IRI theProperty, Date theValue) {
		if (theValue != null) {
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(theValue);

			try {
				return addProperty(theProperty, mValueFactory.createLiteral(DatatypeFactory.newInstance().newXMLGregorianCalendar(c).toXMLFormat(), XMLSchema.DATETIME));
			}
			catch (DatatypeConfigurationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		else {
			return this;
		}
	}

    public ResourceBuilder addProperty(IRI theProperty, Float theValue) {
		if (theValue != null) {
	        return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

    public ResourceBuilder addProperty(IRI theProperty, Boolean theValue) {
		if (theValue != null) {
        	return addProperty(theProperty, mValueFactory.createLiteral(theValue));
		}
		else {
			return this;
		}
    }

	@SuppressWarnings("unchecked")
	public ResourceBuilder addProperty(IRI theProperty, Object theObject) {
		if (theObject == null) {
			return this;
		}
		else if (theObject instanceof Boolean) {
			return addProperty(theProperty, (Boolean) theObject);
		}
		else if (theObject instanceof Long) {
			return addProperty(theProperty, (Long) theObject);
		}
		else if (theObject instanceof Integer) {
			return addProperty(theProperty, (Integer) theObject);
		}
		else if (theObject instanceof Short) {
			return addProperty(theProperty, (Short) theObject);
		}
		else if (theObject instanceof Float) {
			return addProperty(theProperty, (Float) theObject);
		}
		else if (theObject instanceof Date) {
			return addProperty(theProperty, (Date) theObject);
		}
		else if (theObject instanceof Double) {
			return addProperty(theProperty, (Double) theObject);
		}
		else if (theObject instanceof Value) {
			return addProperty(theProperty, (Value) theObject);
		}
		else if (theObject instanceof List) {
			try {
				return addProperty(theProperty, (List<Value>) theObject);
			}
			catch (ClassCastException e) {
				e.printStackTrace();
				return this;
			}
		}
		else if (theObject instanceof ResourceBuilder) {
			return addProperty(theProperty, (ResourceBuilder) theObject);
		}
		else if (theObject instanceof java.net.URI) {
			return addProperty(theProperty, (java.net.URI) theObject);
		}
		else {
			return addProperty(theProperty, theObject.toString());
		}
	}

    public ResourceBuilder addLabel(String theLabel) {
        return addProperty(RDFS.LABEL, theLabel);
    }

    public ResourceBuilder addType(IRI theType) {
        return addProperty(RDF.TYPE, theType);
    }
}

