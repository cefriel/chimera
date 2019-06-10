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

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;

import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Query;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complexible.common.openrdf.model.Models2;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import eu.st4rt.converter.empire.Empire;
import eu.st4rt.converter.empire.EmpireException;
import eu.st4rt.converter.empire.EmpireGenerated;
import eu.st4rt.converter.empire.EmpireOptions;
import eu.st4rt.converter.empire.annotation.AnnotationChecker;
import eu.st4rt.converter.empire.annotation.InvalidRdfException;
import eu.st4rt.converter.empire.annotation.RdfGenerator;
import eu.st4rt.converter.empire.ds.DataSource;
import eu.st4rt.converter.empire.ds.DataSourceException;
import eu.st4rt.converter.empire.ds.DataSourceUtil;
import eu.st4rt.converter.empire.ds.MutableDataSource;
import eu.st4rt.converter.empire.ds.QueryException;
import eu.st4rt.converter.empire.ds.SupportsNamedGraphs;
import eu.st4rt.converter.empire.ds.SupportsTransactions;
import eu.st4rt.converter.empire.ds.impl.TransactionalDataSource;
import eu.st4rt.converter.empire.util.BeanReflectUtil;
import eu.st4rt.converter.empire.util.EmpireUtil;
import st4rt.convertor.empire.annotation.RdfsClass;
import st4rt.convertor.empire.annotation.SupportsRdfId;

/**
 * <p>Implementation of the JPA {@link EntityManager} interface to support the persistence model over
 * an RDF database.</p>
 *
 * @author	Michael Grove
 * @since	0.1
 * @version	1.0
 *
 * @see EntityManager
 * @see DataSource
 */
public final class EntityManagerImpl implements EntityManager {
	/**
	 * The logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerImpl.class.getName());

	/**
	 * Whether or not this EntityManagerImpl is open
	 */
	private boolean mIsOpen = false;

	/**
	 * The underlying data source
	 */
	private MutableDataSource mDataSource;

	public MutableDataSource getmDataSource() {
		return mDataSource;
	}

	

	/**
	 * The current transaction
	 */
	private EntityTransaction mTransaction;

	/**
	 * The Entity Listeners for our managed entities.
	 */
	private Map<Object, Collection<Object>> mManagedEntityListeners = new WeakHashMap<>();

	/**
	 * The current collapsed view of a DataSourceOperation which is a merged set of adds & removes to the DataSource.
	 * Used during the canonical EntityManager operations such as merge, persist, remove
	 */
	private DataSourceOperation mOp;

	/**
	 * The list of things which are ready to be cascaded.  They are tracked in this list to help prevent infinite loops
	 */
	private Collection<Object> mCascadePending = new HashSet<>();

	/**
	 * Create a new EntityManagerImpl
	 * @param theSource the underlying RDF datasource used for persistence operations
	 */
	public EntityManagerImpl(MutableDataSource theSource) {

		// TODO: sparql for everything, just convert serql into sparql
		// TODO: work like JPA/hibernate -- if something does not have a @Transient on it, convert it.  we'll just need to coin a URI in those cases
		// TODO: add an @RdfsLabel annotation that will use the value of a property as the label during annotation
		// TODO: support for owl/rdfs annotations not mappable to JPA annotations such as min/max cardinality and others.

		mIsOpen = true;

		mDataSource = theSource;
	}

	/**
	 * @inheritDoc
	 */
	public void flush() {
		assertOpen();
		
		// we'll do nothing here since our default implementation doesn't queue up changes, they're made
		// as soon as remove/persist are called
	}

	/**
	 * @inheritDoc
	 */
	public void setFlushMode(final FlushModeType theFlushModeType) {
		assertOpen();

		if (theFlushModeType != FlushModeType.AUTO) {
			throw new IllegalArgumentException("Commit style flush mode not supported");
		}
	}

	/**
	 * @inheritDoc
	 */
	public FlushModeType getFlushMode() {
		assertOpen();
		
		return FlushModeType.AUTO;
	}

	/**
	 * @inheritDoc
	 */
	public void lock(final Object theObj, final LockModeType theLockModeType) {
		throw new PersistenceException("Lock is not supported.");
	}
	
	/**
	 * @inheritDoc
	 */
	public void refresh(Object theObj) {
		assertStateOk(theObj);

		assertContains(theObj);

		Object aDbObj = find(theObj.getClass(), EmpireUtil.asSupportsRdfId(theObj).getRdfId());

        Collection<AccessibleObject> aAccessors = Sets.newHashSet();

        aAccessors.addAll(BeanReflectUtil.getAnnotatedFields(aDbObj.getClass()));
        aAccessors.addAll(BeanReflectUtil.getAnnotatedGetters(aDbObj.getClass(), true));

		if (theObj instanceof EmpireGenerated) {
			((EmpireGenerated)theObj).setAllTriples(((EmpireGenerated)aDbObj).getAllTriples());
			((EmpireGenerated)theObj).setInstanceTriples(((EmpireGenerated)aDbObj).getInstanceTriples());
		}

        try {
            for (AccessibleObject aAccess : aAccessors) {
                Object aValue = BeanReflectUtil.safeGet(aAccess, aDbObj);
                
                AccessibleObject aSetter = BeanReflectUtil.asSetter(aDbObj.getClass(), aAccess);
                
                BeanReflectUtil.safeSet(aSetter, theObj, aValue);
            }
        }
        catch (InvocationTargetException e) {
            throw new PersistenceException(e);
        }
    }

	/**
	 * @inheritDoc
	 */
	public void clear() {
		assertOpen();

		cleanState();
	}

	/**
	 * @inheritDoc
	 */
	public boolean contains(final Object theObj) {
		assertStateOk(theObj);

		try {
			return DataSourceUtil.exists(getDataSource(), theObj);
		}
		catch (DataSourceException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public Query createQuery(final String theQueryString) {
		return getDataSource().getQueryFactory().createQuery(theQueryString);
	}

	/**
	 * @inheritDoc
	 */
	public Query createNamedQuery(final String theName) {
		return getDataSource().getQueryFactory().createNamedQuery(theName);
	}

	/**
	 * @inheritDoc
	 */
	public Query createNativeQuery(final String theQueryString) {
		return getDataSource().getQueryFactory().createNativeQuery(theQueryString);
	}

	/**
	 * @inheritDoc
	 */
	public Query createNativeQuery(final String theQueryString, final Class theResultClass) {
		return getDataSource().getQueryFactory().createNativeQuery(theQueryString, theResultClass);
	}

	/**
	 * @inheritDoc
	 */
	public Query createNativeQuery(final String theQueryString, final String theResultSetMapping) {
		return getDataSource().getQueryFactory().createNativeQuery(theQueryString, theResultSetMapping);
	}

	/**
	 * @inheritDoc
	 */
	public void joinTransaction() {
		assertOpen();

		// TODO: maybe do something?  I don't really understand what this method is supposed to do.  from the javadoc
		// the intent is not clear.  i need to do a little more reading on this.  for now, lets make it fail
		// like a user would expect, but not do anything.  that's not ideal, but we'll eventually sort this out.
	}

	/**
	 * @inheritDoc
	 */
	public Object getDelegate() {
		return mDataSource;
	}

	/**
	 * @inheritDoc
	 */
	public void close() {
		if (!isOpen()) {
			throw new IllegalStateException("EntityManager is already closed.");
		}

		getDataSource().disconnect();

		mIsOpen = false;

		cleanState();
	}

	/**
	 * Clean up the current state of the EntityManager, release attached entities and the like.
	 */
	private void cleanState() {
		mManagedEntityListeners.clear();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isOpen() {
		return mIsOpen;
	}

	/**
	 * @inheritDoc
	 */
	public EntityTransaction getTransaction() {
		if (mTransaction == null) {
			mTransaction = new DataSourceEntityTransaction(asSupportsTransactions());
		}

		return mTransaction;
	}

	/**
	 * @inheritDoc
	 */
	public void persist(final Object theObj) {
		assertStateOk(theObj);

		try {
			assertNotContains(theObj);
		}
		catch (Throwable e) {
			throw new EntityExistsException(e);
		}

		try {
			prePersist(theObj);

			boolean isTopOperation = (mOp == null);

			DataSourceOperation aOp = new DataSourceOperation();

			Model aData = RdfGenerator.asRdf(theObj, getDataSource());

			if (doesSupportNamedGraphs() && EmpireUtil.hasNamedGraphSpecified(theObj)) {
				aOp.add(EmpireUtil.getNamedGraph(theObj), aData);
			}
			else {
				aOp.add(aData);
			}

			joinCurrentDataSourceOperation(aOp);

			cascadeOperation(theObj, new IsPersistCascade(), new MergeCascade());

			finishCurrentDataSourceOperation(isTopOperation);

			postPersist(theObj);
		}
		catch (InvalidRdfException ex) {
			throw new IllegalStateException(ex);
		}
		catch (DataSourceException ex) {
			throw new PersistenceException(ex);
		}
		catch (RuntimeException e) {
			if (mTransaction != null) {
				mTransaction.setRollbackOnly();
			}
			throw e;
		}
	}
	
	public String persistAndReturnSubject(final Object theObj) {
		assertStateOk(theObj);
        String result = null;
        
		try {
			assertNotContains(theObj);
		}
		catch (Throwable e) {
			throw new EntityExistsException(e);
		}

		try {
			prePersist(theObj);

			boolean isTopOperation = (mOp == null);

			DataSourceOperation aOp = new DataSourceOperation();

			Model aData = RdfGenerator.asRdf(theObj, getDataSource());

			if (doesSupportNamedGraphs() && EmpireUtil.hasNamedGraphSpecified(theObj)) {
				aOp.add(EmpireUtil.getNamedGraph(theObj), aData);
			}
			else {
				aOp.add(aData);
			}

			joinCurrentDataSourceOperation(aOp);

			cascadeOperation(theObj, new IsPersistCascade(), new MergeCascade());

			finishCurrentDataSourceOperation(isTopOperation);

			postPersist(theObj);
			//+++++++
			Iterator<Statement> iter = aData.iterator();
			while(iter.hasNext()) {
				Statement statement = iter.next();
				result = statement.getSubject().stringValue();
				break;
			}
			//++++
		}
		catch (InvalidRdfException ex) {
			throw new IllegalStateException(ex);
		}
		catch (DataSourceException ex) {
			throw new PersistenceException(ex);
		}
		catch (RuntimeException e) {
			if (mTransaction != null) {
				mTransaction.setRollbackOnly();
			}
			throw e;
		}
		finally {
			return result;
		}
	}

	private MutableDataSource getDataSource() {
		return (MutableDataSource) getDelegate();
	}

	private void finishCurrentDataSourceOperation(boolean theIsTop) throws DataSourceException {
		if (theIsTop) {
			mCascadePending.clear();
			mOp.execute();
			mOp = null;
		}
	}

	/**
	 * @inheritDoc
	 */
	@SuppressWarnings("unchecked")
	public <T> T merge(final T theT) {
		assertStateOk(theT);

		Model aExistingData = null;
		
		if (theT instanceof EmpireGenerated) {
			aExistingData = ((EmpireGenerated) theT).getInstanceTriples();
		}
		

		if (aExistingData == null || aExistingData.isEmpty()) {
			// it looks like this bean instance does not have instance triples set properly (for some reason)
			// if we assume that aExistingData is empty, then no triples will be removed in the try/catch section below,
			// which can lead to duplicate triples

			// while in ideal world, this situation should not occur, below is an attempt to alleviate the case (i.e.,
			// find out what the instance triples actually are)
			try {
				if (theT instanceof EmpireGenerated) {
					// if bean has been generated by Empire, then we can try to read its copy from the database, and use the triples from that copy
					Object aDbObj = find(((EmpireGenerated) theT).getInterfaceClass(), EmpireUtil.asSupportsRdfId(theT).getRdfId());

					if (aDbObj != null) { 
						aExistingData = ((EmpireGenerated) aDbObj).getInstanceTriples();
					}
					else {
						aExistingData = Models2.newModel();
					}
				}
				else {
					// as a fall back, we can perform a describe to find all related triples for the individual;
					// unfortunately, describe returns more information (in fact, it probably gives us more what getAllTriples() would return
					// rather than getInstanceTriples()), but there is not much else we can do
					aExistingData = assertContainsAndDescribe(theT);
				}
			}
			catch (IllegalArgumentException e) {
				// when everything else fails, just assume that existing data was indeed empty ...
				aExistingData = Models2.newModel();
			}
		}

		try {
			preUpdate(theT);

			Model aData = RdfGenerator.asRdf(theT, getDataSource());

			boolean isTopOperation = (mOp == null);

			DataSourceOperation aOp = new DataSourceOperation();

			if (doesSupportNamedGraphs() && EmpireUtil.hasNamedGraphSpecified(theT)) {
				URI aGraphURI = EmpireUtil.getNamedGraph(theT);

				aOp.remove(aGraphURI, aExistingData);
				aOp.add(aGraphURI, aData);
			}
			else {
				aOp.remove(aExistingData);
				aOp.add(aData);
			}

			joinCurrentDataSourceOperation(aOp);

			// cascade the merge
			cascadeOperation(theT, new IsMergeCascade(), new MergeCascade());

			finishCurrentDataSourceOperation(isTopOperation);

			postUpdate(theT);

            return theT;
		}
		catch (DataSourceException ex) {
			throw new PersistenceException(ex);
		}
		catch (InvalidRdfException ex) {
			throw new IllegalStateException(ex);
		}
		catch (RuntimeException e) {
			if (mTransaction != null) {
				mTransaction.setRollbackOnly();
			}

			throw e;
		}
	}

	private void joinCurrentDataSourceOperation(final DataSourceOperation theOp) {
		if (mOp == null) {
			mOp = theOp;
		}
		else {
			mOp.merge(theOp);
		}
	}

	private <T> void cascadeOperation(T theT, CascadeTest theCascadeTest, CascadeAction theAction) {
		// if we've already cascaded this, move on to the next thing, we don't want infinite loops
		if (mCascadePending.contains(theT)) {
			return;
		}
		else {
			mCascadePending.add(theT);
		}

		Collection<AccessibleObject> aAccessors = Sets.newHashSet();

		aAccessors.addAll(BeanReflectUtil.getAnnotatedFields(theT.getClass()));
		aAccessors.addAll(BeanReflectUtil.getAnnotatedGetters(theT.getClass(), true));

		for (AccessibleObject aObj : aAccessors) {
			if (theCascadeTest.test(aObj)) {
				try {
					Object aAccessorValue = BeanReflectUtil.safeGet(aObj, theT);

					if (aAccessorValue == null) {
						continue;
					}

					theAction.test(aAccessorValue);
				}
				catch (Exception e) {
					throw new PersistenceException(e);
				}
			}
		}
	}

	private class MergeCascade extends CascadeAction {
		public void cascade(Object theValue) {
			// is it the correct JPA behavior to persist a value when it does not exist during a cascaded
			// merge?  or should that be a PersistenceException just like any normal merge for an un-managed
			// object?
			if (AnnotationChecker.isValid(theValue.getClass())) {
				if (contains(theValue)) {
					merge(theValue);
				}
				else {
					persist(theValue);
				}
			}
		}
	}

	private class RemoveCascade extends CascadeAction {
		public void cascade(Object theValue) {
			if (AnnotationChecker.isValid(theValue.getClass())) {
				if (contains(theValue)) {
					remove(theValue);
				}
			}
		}
	}

	private class IsMergeCascade extends CascadeTest {
		public boolean test(final AccessibleObject theValue) {
			return BeanReflectUtil.isMergeCascade(theValue);
		}
	}

	private class IsRemoveCascade extends CascadeTest {
		public boolean test(final AccessibleObject theValue) {
			return BeanReflectUtil.isRemoveCascade(theValue);
		}
	}

	private class IsPersistCascade extends CascadeTest {
		public boolean test(final AccessibleObject theValue) {
			return BeanReflectUtil.isPersistCascade(theValue);
		}
	}

	private abstract class CascadeTest implements Predicate<AccessibleObject> {
	}

	private abstract class CascadeAction implements Predicate<Object> {
		public abstract void cascade(Object theObj);

		public final boolean test(Object theObj) {
			// is it an error if you specify a cascade type for something that cannot be
			// cascaded?  such as strings, or a non Entity instance?
			if (Collection.class.isAssignableFrom(theObj.getClass())) {
				for (Object aValue : (Collection) theObj) {
					cascade(aValue);
				}
			}
			else {
				cascade(theObj);
			}

			return true;
		}
	}

	/**
	 * @inheritDoc
	 */
	public void remove(final Object theObj) {
		assertStateOk(theObj);

		Model aData = assertContainsAndDescribe(theObj);

		try {
			preRemove(theObj);

			boolean isTopOperation = (mOp == null);

			DataSourceOperation aOp = new DataSourceOperation();

			// we were transforming the current object to RDF and deleting that, but i dont think that's the intended
			// behavior.  you want to delete everything about the object in the database, not the properties specifically
			// on the thing being deleted -- there's an obvious case where there could be a delta between them and you
			// don't delete everything.  so we'll do a describe on the object and delete everything we know about it
			// i.e. everything where its in the subject position.

			//Graph aData = RdfGenerator.asRdf(theObj);
			//Graph aData = DataSourceUtil.describe(getDataSource(), theObj);

			if (doesSupportNamedGraphs() && EmpireUtil.hasNamedGraphSpecified(theObj)) {
				aOp.remove(EmpireUtil.getNamedGraph(theObj), aData);
			}
			else {
				aOp.remove(aData);
			}

			joinCurrentDataSourceOperation(aOp);

			cascadeOperation(theObj, new IsRemoveCascade(), new RemoveCascade());

			finishCurrentDataSourceOperation(isTopOperation);

			postRemove(theObj);
		}
		catch (DataSourceException ex) {
			throw new PersistenceException(ex);
		}
		catch (RuntimeException e) {
			if (mTransaction != null) {
				mTransaction.setRollbackOnly();
			}
			throw e;
		}
	}

	/**
	 * @inheritDoc
	 */
	public <T> T find(final Class<T> theClass, final Object theObj) {
		assertOpen();

		try {
			AnnotationChecker.assertValid(theClass);
		}
		catch (EmpireException e) {
			throw new IllegalArgumentException(e);
		}

		try {
			if (DataSourceUtil.exists(getDataSource(), EmpireUtil.asPrimaryKey(theObj))) {
				T aT = RdfGenerator.fromRdf(theClass, EmpireUtil.asPrimaryKey(theObj), getDataSource());

				postLoad(aT);

				return aT;
			}
			else {
				return null;
			}
		}
		catch (InvalidRdfException e) {
			throw new IllegalArgumentException("Type is not valid, or object with key is not a valid Rdf Entity.", e);
		}
		catch (DataSourceException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * @inheritDoc
	 */
	public <T> T getReference(final Class<T> theClass, final Object theObj) {
		assertOpen();

		T aObj = find(theClass, theObj);

		if (aObj == null) {
			throw new EntityNotFoundException("Cannot find Entity with primary key: " + theObj);
		}

		return aObj;
	}

	/**
	 * Enforce that the object exists in the database
	 * @param theObj the object that should exist
	 * @throws IllegalArgumentException thrown if the object does not exist in the database
	 */
	private void assertContains(Object theObj) {
		if (!contains(theObj)) {
			throw new IllegalArgumentException("Entity does not exist: " + theObj);
		}
	}

	/**
	 * Performs the same checks as assertContains, but returns the Graph describing the resource as a result so you
	 * do not need to perform a subsequent call to the database to get the data that is checked during containment
	 * checks in the first place, thus saving a query to the database.
	 * @param theObj the object that should exist.
	 * @return The graph describing the resource
	 * @throws IllegalArgumentException thrown if the object does not exist in the database
	 */
	private Model assertContainsAndDescribe(Object theObj) {
		assertStateOk(theObj);

		try {
			Model aGraph = DataSourceUtil.describe(getDataSource(), theObj);

			if (aGraph.isEmpty()) {
				throw new IllegalArgumentException("Entity does not exist: " + theObj);
			}

			return aGraph;
		}
		catch (QueryException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Enforce that the object does not exist in the database
	 * @param theObj the object that should not exist
	 * @throws IllegalArgumentException thrown if the object already exists in the database
	 */
	private void assertNotContains(Object theObj) {
		if (contains(theObj)) {
			throw new IllegalArgumentException("Entity already exists: " + theObj);
		}
	}

	/**
	 * Assert that the state of the EntityManager is ok; that it is open, and the specified object is a valid Rdf entity.
	 * @param theObj the object to check
	 * @throws IllegalStateException if the EntityManager is closed
	 * @throws IllegalArgumentException thrown if the value is not a valid Rdf Entity
	 */
	private void assertStateOk(Object theObj) {
		assertOpen();
		assertSupported(theObj);
	}

	/**
	 * Enforce that the EntityManager is open
	 * @throws IllegalStateException thrown if the EntityManager is closed or not yet open
	 */
	private void assertOpen() {
		if (!isOpen()) {
			throw new IllegalStateException("Cannot perform operation, EntityManager is not open");
		}
	}

	/**
	 * Assert that the object can be supported by this EntityManager, that is it a valid Rdf entity
	 * @param theObj the object to validate
	 * @throws IllegalArgumentException thrown if the object is not a valid Rdf entity.
	 */
	private void assertSupported(final Object theObj) {
		Preconditions.checkArgument(theObj != null, "null objects are not supported");
		Preconditions.checkArgument(theObj instanceof SupportsRdfId, "Persistent RDF objects must implement the SupportsRdfId interface.");

		assertEntity(theObj);
		assertRdfClass(theObj);

		try {
			AnnotationChecker.assertValid(theObj.getClass());
		}
		catch (EmpireException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Enforce that the object has the {@link Entity} annotation
	 * @param theObj the instance
	 * @throws IllegalArgumentException if the instances does not have the Entity Annotation
	 * @see Entity
	 */
	private void assertEntity(final Object theObj) {
		if (EmpireOptions.ENFORCE_ENTITY_ANNOTATION) {
			assertHasAnnotation(theObj, Entity.class);
		}
	}

	/**
	 * Enforce that the object has the {@link RdfsClass} annotation
	 * @param theObj the instance
	 * @throws IllegalArgumentException if the instances does not have the RdfClass annotation
	 * @see RdfsClass
	 */
	private void assertRdfClass(final Object theObj) {
		assertHasAnnotation(theObj, RdfsClass.class);
	}

	/**
	 * Verify that the instance has the specified annotation
	 * @param theObj the instance
	 * @param theAnnotation the annotation the instance is required to have
	 * @throws IllegalArgumentException thrown if the instance does not have the required annotation
	 */
	private void assertHasAnnotation(final Object theObj, final Class<? extends Annotation> theAnnotation) {
		if (!BeanReflectUtil.hasAnnotation(theObj.getClass(), theAnnotation)) {
			throw new IllegalArgumentException("Object (" + theObj.getClass() + ") is not an " + theAnnotation.getSimpleName());
		}
	}

	/**
	 * Returns whether or not the data source supports operations on named sub-graphs
	 * @return true if it does, false otherwise.  Returning true indicates calls to {@link #asSupportsNamedGraphs()}
	 * will return successfully without a ClassCastException
	 */
	private boolean doesSupportNamedGraphs() {
		return getDataSource() instanceof SupportsNamedGraphs;
	}

	/**
	 * Returns a reference to an object (the data source) which can perform operations on named sub-graphs
	 * @return the data source as a {@link SupportsNamedGraphs}
	 * @throws ClassCastException thrown if the data source does not implements SupportsNamedGraphs
	 */
	private SupportsNamedGraphs asSupportsNamedGraphs() {
		return (SupportsNamedGraphs) getDataSource();
	}

	/**
	 * Returns a reference to an object (the DataSource) which supports Transactions.  Transaction support is
	 * provided by either the DataSource's native transaction support, or via our naive
	 * {@link TransactionalDataSource transactional wrapper}.
	 * @return a source which supports transactions
	 */
	private SupportsTransactions asSupportsTransactions() {
		if (mDataSource instanceof SupportsTransactions) {
			return (SupportsTransactions) mDataSource;
		}
		else {
			// it doesnt support transactions natively, so we'll wrap it in our naive transaction support.
			return new TransactionalDataSource(mDataSource);
		}
	}

	/**
	 * Fire the PostPersist lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void postPersist(final Object theObj) {
		handleLifecycleCallback(theObj, PostPersist.class);
	}

	/**
	 * Fire the PostRemove lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void postRemove(final Object theObj) {
		handleLifecycleCallback(theObj, PostRemove.class);
	}

	/**
	 * Fire the PostLoad lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void postLoad(final Object theObj) {
		handleLifecycleCallback(theObj, PostLoad.class);
	}

	/**
	 * Fire the PreRemove lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void preRemove(final Object theObj) {
		handleLifecycleCallback(theObj, PreRemove.class);
	}

	/**
	 * Fire the PreUpdate lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void preUpdate(final Object theObj) {
		handleLifecycleCallback(theObj, PreUpdate.class);
	}

	/**
	 * Fire the PostUpdate lifecycle event for the Entity
	 * @param theObj the Entity to fire the event for
	 */
	private void postUpdate(final Object theObj) {
		handleLifecycleCallback(theObj, PostUpdate.class);
	}

	/**
	 * Fire the PrePersist lifecycle event for the Entity
	 * @param theObj the entity to fire the event for
	 */
	private void prePersist(final Object theObj) {
		handleLifecycleCallback(theObj, PrePersist.class);
	}

	/**
	 * Handle the dispatching of the specified lifecycle event
	 * @param theObj the object involved in the event
	 * @param theLifecycleAnnotation the annotation denoting the event, such as {@link PrePersist}, {@link PostLoad}, etc.
	 */
	private void handleLifecycleCallback(final Object theObj, final Class<? extends Annotation> theLifecycleAnnotation) {
		if (theObj == null) {
			return;
		}

		Collection<Method> aMethods = BeanReflectUtil.getAnnotatedMethods(theObj.getClass(), theLifecycleAnnotation);

		Throwable aError = null;

		// Entity methods take no arguments...
		try {
			for (Method aMethod : aMethods) {
				aMethod.invoke(theObj);
			}
		}
		catch (Exception e) {
			LOGGER.info("There was an error during entity lifecycle notification for annotation: " +
						 theLifecycleAnnotation + " on object: " + theObj +".", e);

			Throwables.propagateIfInstanceOf(e, PersistenceException.class);
			throw new PersistenceException(e);
		}

		for (Object aListener : getEntityListeners(theObj)) {
			Collection<Method> aListenerMethods = BeanReflectUtil.getAnnotatedMethods(aListener.getClass(), theLifecycleAnnotation);

			// EntityListeners methods take a single arguement, the entity
			try {
				for (Method aListenerMethod : aListenerMethods) {
					aListenerMethod.invoke(aListener, theObj);
				}
			}
			catch (Exception e) {
				LOGGER.info("There was an error during lifecycle notification for annotation: " +
							 theLifecycleAnnotation + " on object: " + theObj + ".", e);

				Throwables.propagateIfInstanceOf(e, PersistenceException.class);
				throw new PersistenceException(e);
			}
		}
	}

	/**
	 * Get or create the list of EntityListeners for an object.  If a list is created, it will be kept around and
	 * re-used for later persistence operations.
	 * @param theObj the object to get EntityLIsteners for
	 * @return the list of EntityListeners for the object, or null if they do not exist
	 */
	private Collection<Object> getEntityListeners(final Object theObj) {
		Collection<Object> aListeners = mManagedEntityListeners.get(theObj);

		if (aListeners == null) {
			EntityListeners aEntityListeners = BeanReflectUtil.getAnnotation(theObj.getClass(), EntityListeners.class);

			if (aEntityListeners != null) {
				// if there are entity listeners, lets create them
				aListeners = new LinkedHashSet<>();
				for (Class<?> aClass : aEntityListeners.value()) {
					try {
						aListeners.add(Empire.get().instance(aClass));
					}
					catch (Exception e) {
						LOGGER.error("There was an error instantiating an EntityListener. ", e);
					}
				}

				mManagedEntityListeners.put(theObj, aListeners);
			}
			else {
				aListeners = Collections.emptyList();
			}
		}

		return aListeners;
	}

	/**
	 * Class which encapsulates a set of adds & removes to a DataSource.  Used to process a set of changes in a single
	 * operation, well, two operations.  Remove and then Add.  Also will verify that all objects that should have been
	 * added/removed from the KB have been added or removed.
	 * @author Michael Grove
	 * @since 0.7
	 * @version 0.7
	 */
	protected class DataSourceOperation {
		// HashMap's used here rather than the more generic Map interface because we allow null keys (no specified
		// named graph) which HashMap allows, while generically Map makes no guarantees about this, so we're explicit here.

		private final Map<URI, Model> mAdd;
		private final Map<URI, Model> mRemove;

		private final Set<Object> mVerifyAdd = Sets.newHashSet();
		private final Set<Object> mVerifyRemove = Sets.newHashSet();

		/**
		 * Create a new DataSourceOperation
		 */
		DataSourceOperation() {
			mAdd = Maps.newHashMap();
			mRemove = Maps.newHashMap();
		}

		/**
		 * Execute this operation.  Removes & adds all the specified data in as few database calls as possible.  Then
		 * verifies the results of the operations
		 * @throws DataSourceException if there is an error while performing the add/remove operations
		 * @throws PersistenceException if any objects were failed to be added or removed from the database
		 */
		public void execute() throws DataSourceException {
			// TODO: should this be in its own transaction?  or join the current one?

            if (getDataSource() instanceof SupportsTransactions) {
                ((SupportsTransactions)getDataSource()).begin();
            }

            try {
                for (URI aGraphURI : mRemove.keySet()) {
                    if (doesSupportNamedGraphs() && aGraphURI != null) {
                        asSupportsNamedGraphs().remove(aGraphURI, mRemove.get(aGraphURI));
                    }
                    else {
                        getDataSource().remove(mRemove.get(aGraphURI));
                    }
                }

                for (URI aGraphURI : mAdd.keySet()) {
                    if (doesSupportNamedGraphs() && aGraphURI != null) {
                        asSupportsNamedGraphs().add(aGraphURI, mAdd.get(aGraphURI));
                    }
                    else {
                        getDataSource().add(mAdd.get(aGraphURI));
                    }
                }

                if (getDataSource() instanceof SupportsTransactions) {
                    ((SupportsTransactions)getDataSource()).commit();
                }

                verify();
            }
            catch (DataSourceException e) {
                if (getDataSource() instanceof SupportsTransactions) {
                    ((SupportsTransactions)getDataSource()).rollback();
                }
            }
        }

		/**
		 * Add the specified object to the list of objects that should be removed from the database when this operation
		 * is executed.
		 * @param theObj the object that should be revmoed from the database when the operation is executed
		 */
		public void verifyRemove(final Object theObj) {
			mVerifyRemove.add(theObj);
		}

		/**
		 * Add the specified object to the list of objects that should be added to the database when this operation
		 * is executed.
		 * @param theObj the object that should be added to the database when the operation is executed
		 */
		public void verifyAdd(final Object theObj) {
			mVerifyAdd.add(theObj);
		}

		/**
		 * Verify that all the objects to be added/removed were completed successfully.
		 * @throws PersistenceException if an add or remove failed for any reason
		 */
		private void verify() {
			for (Object aObj : mVerifyRemove) {
				if (contains(aObj)) {
					throw new PersistenceException("Remove failed for object: " + aObj.getClass() + " -> " + EmpireUtil.asSupportsRdfId(aObj).getRdfId());
				}
			}

			for (Object aObj : mVerifyAdd) {
				if (!contains(aObj)) {
					throw new PersistenceException("Addition failed for object: " + aObj.getClass() + " -> " + EmpireUtil.asSupportsRdfId(aObj).getRdfId());
				}
			}
		}

		/**
		 * Add this graph to the set of data to be added when this operation is executed
		 * @param theGraph the graph to be added
		 */
		public void add(final Model theGraph) {
			add(null, theGraph);
		}

		/**
		 * Add this graph to the set of data to be added, to the specified named graph, when this operation is executed
		 * @param theGraphURI the named graph the data should be added to
		 * @param theGraph the data to add
		 */
		public void add(final URI theGraphURI, final Model theGraph) {
			Model aGraph = mAdd.get(theGraphURI);

			if (aGraph == null) {
				aGraph = Models2.newModel();
			}

			aGraph.addAll(theGraph);

			mAdd.put(theGraphURI, aGraph);
		}

		/**
		 * Add this graph to the set of data to be removed when this operation is executed
		 * @param theGraph the graph to be removed
		 */
		public void remove(final Model theGraph) {
			remove(null, theGraph);
		}

		/**
		 * Add this graph to the set of data to be removed, from the specified named graph, when this operation is executed
		 * @param theGraphURI the named graph the data should be removed from
		 * @param theGraph the data to remove
		 */
		public void remove(final URI theGraphURI, final Model theGraph) {
			Model aGraph = mRemove.get(theGraphURI);

			if (aGraph == null) {
				aGraph = Models2.newModel();
			}

			aGraph.addAll(theGraph);

			mRemove.put(theGraphURI, aGraph);
		}

		/**
		 * Merge the operation with this one.  This will merge all the changes being tracked into a single operation.
		 * @param theOp the operation to merge
		 */
		public void merge(final DataSourceOperation theOp) {
			for (Map.Entry<URI, Model> aEntry : theOp.mRemove.entrySet()) {
				remove(aEntry.getKey(), aEntry.getValue());
			}

			for (Map.Entry<URI, Model> aEntry : theOp.mAdd.entrySet()) {
				add(aEntry.getKey(), aEntry.getValue());
			}

			mVerifyAdd.addAll(theOp.mVerifyAdd);
			mVerifyRemove.addAll(theOp.mVerifyRemove);
		}
	}
}
