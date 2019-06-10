// Copyright (c) 2010 - 2013, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// For more information about licensing and copyright of this software, please contact
// inquiries@clarkparsia.com or visit http://stardog.com

package com.complexible.common.openrdf.query;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;



/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   1.1
 * @version 1.1
 */
public class DelegatingBindingSet implements BindingSet {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7720094974576944686L;
	private final BindingSet mBindingSet;

    public DelegatingBindingSet(final BindingSet theBindingSet) {
        mBindingSet = theBindingSet;
    }

    protected BindingSet getDelegate() {
        return mBindingSet;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int size() {
        return mBindingSet.size();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Iterator<Binding> iterator() {
        return mBindingSet.iterator();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean hasBinding(final String theName) {
        return mBindingSet.hasBinding(theName);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Value getValue(final String theName) {
        return mBindingSet.getValue(theName);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<String> getBindingNames() {
        return mBindingSet.getBindingNames();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Binding getBinding(final String theName) {
        return mBindingSet.getBinding(theName);
    }
}
