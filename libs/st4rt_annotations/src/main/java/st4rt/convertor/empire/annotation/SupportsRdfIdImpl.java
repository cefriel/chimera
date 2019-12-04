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

package st4rt.convertor.empire.annotation;

import st4rt.convertor.empire.annotation.SupportsRdfId;
import com.google.common.base.Objects;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.rdf4j.model.BNode;

/**
 * <p>Utility implementation of the {@link SupportsRdfId} interface.</p>
 *
 * @author Michael Grove
 * @since 0.1
 * @version 0.6.3
 */
public final class SupportsRdfIdImpl implements SupportsRdfId {
    /**
     * the rdf key identifier
     */
    private RdfKey mId;

    /**
     * Create a new SupportsRdfIdImpl
     */
    public SupportsRdfIdImpl() {
        // TODO: I think canonically, this should put the @Id JPA annotation on mId, but that would badly
        // mix with Play! since they already assign their own @Id.  This is something to remember for the future though.

        mId = null;
    }

    /**
     * Create a new SupportsRdfIdImpl
     * @param theId the rdf:ID of the object
     */
    public SupportsRdfIdImpl(final RdfKey theId) {
        mId = theId;
    }

    /**
     * Create a new SupportsRdfIdImpl
     * @param theId the rdf:ID of the object
     */
    public SupportsRdfIdImpl(final URI theId) {
        this(new URIKey(theId));
    }

    /**
     * Create a new SupportsRdfIdImpl
     * @param theId the identifier of the object
     */
    public SupportsRdfIdImpl(final String theId) {
        this(asPrimaryKey(theId));
    }

    /**
     * @inheritDoc
     */
    public RdfKey getRdfId() {
        return mId;
    }

    /**
     * @inheritDoc
     */
    public void setRdfId(final RdfKey theId) {
        if (mId != null) {
            throw new IllegalStateException("Cannot set the rdf id of an object once it is set");
        }

        mId = theId;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(final Object theObj) {
        if (this == theObj) {
            return true;
        }

        if (theObj == null || getClass() != theObj.getClass()) {
            return false;
        }

        final SupportsRdfIdImpl that = (SupportsRdfIdImpl) theObj;

        return Objects.equal(mId, that.mId);
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return mId == null ? 0 : mId.hashCode();
    }

    public static SupportsRdfId.RdfKey asPrimaryKey(Object theObj) {
        if (theObj == null) {
            throw new IllegalArgumentException("null is not a valid primary key for an Entity");
        }

        try {
            if (theObj instanceof URI) {
                return new SupportsRdfId.URIKey((URI) theObj);
            }
            else if (theObj instanceof URL) {
                return new SupportsRdfId.URIKey(((URL) theObj).toURI());
            }
            else if (theObj instanceof org.eclipse.rdf4j.model.IRI) {
                return new SupportsRdfId.URIKey( URI.create( ((org.eclipse.rdf4j.model.IRI) theObj).stringValue()) );
            }
            else if (theObj instanceof org.eclipse.rdf4j.model.BNode) {
                return new SupportsRdfId.BNodeKey( ((BNode) theObj).getID() );
            }
            else {
                if (isURI(theObj.toString())) {
                    return new SupportsRdfId.URIKey(new URI(theObj.toString()));
                }
                else {
                    if (theObj.toString().matches("[a-zA-Z_0-9]{1}[a-zA-Z_\\-0-9]*")) {
                        return new SupportsRdfId.BNodeKey(theObj.toString());
                    }
                    else if (theObj.toString().matches("_:[a-zA-Z_0-9]{1}[a-zA-Z_\\-0-9]*")) {
                        return new SupportsRdfId.BNodeKey(theObj.toString().substring(2));
                    }
                    throw new IllegalArgumentException("'" + theObj + "' is not a valid primary key, it is not a URI or a valid BNode identifer");
                }
            }
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(theObj + " is not a valid primary key, it is not a URI.", e);
        }
    }

    public static boolean isURI(final Object theObj) {
        if (theObj instanceof URI || theObj instanceof URL) {
            return true;
        }

        try {
            URI.create(theObj.toString());
            return true;
        }
        catch (Exception theE) {
            return false;
        }
    }
}
