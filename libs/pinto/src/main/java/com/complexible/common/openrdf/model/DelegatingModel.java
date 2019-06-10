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

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.util.ModelException;



/**
 * <p></p>
 *
 * @author  Michael Grove
 * @since   4.0
 * @version 4.0
 */
@SuppressWarnings("deprecation")
public abstract class DelegatingModel implements Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6318797016902311675L;
	private final Model mModel;

	public DelegatingModel(final Model theModel) {
		mModel = theModel;
	}

	@Override
	public boolean add(final Resource subj, final IRI pred, final Value obj, final Resource... contexts) {
		return mModel.add(subj, pred, obj, contexts);
	}

	@Override
	@Deprecated
	public boolean add(final Resource subj, final URI pred, final Value obj, final Resource... contexts) {
		return mModel.add(subj, pred, obj, contexts);
	}

	@Override
	public boolean clear(final Resource... context) {
		return mModel.clear(context);
	}

	@Override
	public boolean contains(final Resource subj, final IRI pred, final Value obj, final Resource... contexts) {
		return mModel.contains(subj, pred, obj, contexts);
	}

	@Override
	@Deprecated
	public boolean contains(final Resource subj, final URI pred, final Value obj, final Resource... contexts) {
		return mModel.contains(subj, pred, obj, contexts);
	}

	@Override
	public Set<Resource> contexts() {
		return mModel.contexts();
	}

	@Override
	public Model filter(final Resource subj, final IRI pred, final Value obj, final Resource... contexts) {
		return mModel.filter(subj, pred, obj, contexts);
	}

	@Override
	@Deprecated
	public Model filter(final Resource subj, final URI pred, final Value obj, final Resource... contexts) {
		return mModel.filter(subj, pred, obj, contexts);
	}

	@Override
	public Optional<Namespace> getNamespace(final String prefix) {
		return mModel.getNamespace(prefix);
	}

	@Override
	public Set<Namespace> getNamespaces() {
		return mModel.getNamespaces();
	}

	@Override
	@Deprecated
	public Optional<IRI> objectIRI() throws ModelException {
		return mModel.objectIRI();
	}

	@Override
	@Deprecated
	public Optional<Literal> objectLiteral() throws ModelException {
		return mModel.objectLiteral();
	}

	@Override
	@Deprecated
	public Optional<Resource> objectResource() throws ModelException {
		return mModel.objectResource();
	}

	@Override
	public Set<Value> objects() {
		return mModel.objects();
	}

	@Override
	@Deprecated
	public Optional<String> objectString() throws ModelException {
		return mModel.objectString();
	}

	@Override
	@Deprecated
	public Optional<IRI> objectURI() throws ModelException {
		return mModel.objectURI();
	}

	@Override
	@Deprecated
	public Optional<Value> objectValue() throws ModelException {
		return mModel.objectValue();
	}

	@Override
	public Set<IRI> predicates() {
		return mModel.predicates();
	}

	@Override
	public boolean remove(final Resource subj, final IRI pred, final Value obj, final Resource... contexts) {
		return mModel.remove(subj, pred, obj, contexts);
	}

	@Override
	@Deprecated
	public boolean remove(final Resource subj, final URI pred, final Value obj, final Resource... contexts) {
		return mModel.remove(subj, pred, obj, contexts);
	}

	@Override
	public Optional<Namespace> removeNamespace(final String prefix) {
		return mModel.removeNamespace(prefix);
	}

	@Override
	public void setNamespace(final Namespace namespace) {
		mModel.setNamespace(namespace);
	}

	@Override
	public Namespace setNamespace(final String prefix, final String name) {
		return mModel.setNamespace(prefix, name);
	}

	@Override
	@Deprecated
	public Optional<BNode> subjectBNode() throws ModelException {
		return mModel.subjectBNode();
	}

	@Override
	@Deprecated
	public Optional<IRI> subjectIRI() throws ModelException {
		return mModel.subjectIRI();
	}

	@Override
	@Deprecated
	public Optional<Resource> subjectResource() throws ModelException {
		return mModel.subjectResource();
	}

	@Override
	public Set<Resource> subjects() {
		return mModel.subjects();
	}

	@Override
	@Deprecated
	public Optional<IRI> subjectURI() throws ModelException {
		return mModel.subjectURI();
	}

	@Override
	public Model unmodifiable() {
		return mModel.unmodifiable();
	}

	@Override
	@Deprecated
	public ValueFactory getValueFactory() {
		return mModel.getValueFactory();
	}

	@Override
	@Deprecated
	public Iterator<Statement> match(final Resource subj, final IRI pred, final Value obj, final Resource... contexts) {
		return mModel.match(subj, pred, obj, contexts);
	}

	@Override
	public boolean add(final Statement e) {
		return mModel.add(e);
	}

	@Override
	public boolean addAll(final Collection<? extends Statement> c) {
		return mModel.addAll(c);
	}

	@Override
	public void clear() {
		mModel.clear();
	}

	@Override
	public boolean contains(final Object o) {
		return mModel.contains(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return mModel.containsAll(c);
	}

	@Override
	public boolean equals(final Object o) {
		return mModel.equals(o);
	}

	@Override
	public int hashCode() {
		return mModel.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return mModel.isEmpty();
	}

	@Override
	public Iterator<Statement> iterator() {
		return mModel.iterator();
	}

	@Override
	public Stream<Statement> parallelStream() {
		return mModel.parallelStream();
	}

	@Override
	public boolean remove(final Object o) {
		return mModel.remove(o);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return mModel.removeAll(c);
	}

	@Override
	public boolean removeIf(final Predicate<? super Statement> filter) {
		return mModel.removeIf(filter);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return mModel.retainAll(c);
	}

	@Override
	public int size() {
		return mModel.size();
	}

	@Override
	public Spliterator<Statement> spliterator() {
		return mModel.spliterator();
	}

	@Override
	public Stream<Statement> stream() {
		return mModel.stream();
	}

	@Override
	public Object[] toArray() {
		return mModel.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return mModel.toArray(a);
	}

	@Override
	public void forEach(final Consumer<? super Statement> action) {
		mModel.forEach(action);
	}
}
