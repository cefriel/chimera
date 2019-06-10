package com.complexible.common.openrdf.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.common.iteration.Iteration;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * <p>Additional {@link Model} utilities</p>
 *
 * @author  Michael Grove
 * @since   4.0
 * @version 4.0
 */
public final class Models2 {

	private Models2() {
		throw new AssertionError();
	}

	public static Collector<Statement, Model, Model> toModel() {
		return new Collector<Statement, Model, Model>() {
			@Override
			public Supplier<Model> supplier() {
				return Models2::newModel;
			}

			@Override
			public BiConsumer<Model, Statement> accumulator() {
				return Model::add;
			}

			@Override
			public BinaryOperator<Model> combiner() {
				return (theGraph, theOtherGraph) -> {
					theGraph.addAll(theOtherGraph);
					return theGraph;
				};
			}

			@Override
			public Function<Model, Model> finisher() {
				return Function.identity();
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Sets.newHashSet(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
			}
		};
	}

	public static Model of(final Path thePath) throws IOException {
		return ModelIO.read(thePath);
	}

	public static Model newModel() {
		return new LinkedHashModel();
	}

	public static Model newModel(final Iterable<Statement> theStmts) {
		Model aModel = newModel();

		Iterables.addAll(aModel, theStmts);

		return aModel;
	}

	public static Model newModel(final Iterator<Statement> theStmts) {
		Model aModel = newModel();

		Iterators.addAll(aModel, theStmts);

		return aModel;
	}

	public static Model newModel(final Statement... theStmts) {
		Model aModel = newModel();

		Collections.addAll(aModel, theStmts);

		return aModel;
	}

	public static <E extends Exception, T extends Iteration<Statement, E>> Model newModel(final T theStmts) throws E {
		Model aModel = newModel();

		Iterations.stream(theStmts)
		          .forEach(aModel::add);

		return aModel;
	}

	/**
	 * Returns a copy of the provided graph where all the statements belong to the specified context.
	 * This will overwrite any existing contexts on the statements in the graph.
	 *
	 * @param theGraph		the graph
	 * @param theResource	the context for all the statements in the graph
	 * @return 				the new graph
	 */
	public static Model withContext(final Iterable<Statement> theGraph, final Resource theResource) {
		final Model aModel = newModel();

		for (Statement aStmt : theGraph) {
			aModel.add(SimpleValueFactory.getInstance().createStatement(aStmt.getSubject(),
		                                                                aStmt.getPredicate(),
		                                                                aStmt.getObject(),
		                                                                theResource));
		}

		return aModel;
	}

	/**
	 * Return a new Graph which is the union of all the provided graphs.
	 *
	 * @param theGraphs the graphs to union
	 * @return			the union of the graphs
	 */
	public static Model union(final Model... theGraphs) {
		Model aModel = newModel();

		for (Model aGraph : theGraphs) {
			aModel.addAll(aGraph);
		}

		return aModel;
	}


	/**
	 * Return the value of the property for the given subject.  If there are multiple values, only the first value will
	 * be returned.
	 *
	 * @param theGraph	the graph
	 * @param theSubj	the subject
	 * @param thePred	the property of the subject whose value should be retrieved
	 *
	 * @return 			optionally, the value of the the property for the subject
	 */
	public static Optional<Value> getObject(final Model theGraph, final Resource theSubj, final IRI thePred) {
		Iterator<Value> aCollection = theGraph.filter(theSubj, thePred, null).objects().iterator();

		if (aCollection.hasNext()) {
			return Optional.of(aCollection.next());
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * Return the value of of the property as a Literal
	 *
	 * @param theGraph	the graph
	 * @param theSubj	the resource
	 * @param thePred	the property whose value is to be retrieved
	 * @return 			Optionally, the property value as a literal.  Value will be absent of the SP does not have an O, or the O is not a literal
	 */
	public static Optional<Literal> getLiteral(final Model theGraph, final Resource theSubj, final IRI thePred) {
		Optional<Value> aVal = getObject(theGraph, theSubj, thePred) ;

		if (aVal.isPresent() && aVal.get() instanceof Literal) {
			return Optional.of((Literal) aVal.get());
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * Return the value of of the property as a Resource
	 *
	 * @param theGraph	the graph
	 * @param theSubj	the resource
	 * @param thePred	the property whose value is to be retrieved
	 * @return 			Optionally, the property value as a Resource.  Value will be absent of the SP does not have an O, or the O is not a Resource*
	 */
	public static Optional<Resource> getResource(final Model theGraph, final Resource theSubj, final IRI thePred) {
		Optional<Value> aVal = getObject(theGraph, theSubj, thePred) ;

		if (aVal.isPresent() && aVal.get() instanceof Resource) {
			return Optional.of((Resource) aVal.get());
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * Returns the value of the property on the given resource as a boolean.
	 *
	 * @param theGraph	the graph
	 * @param theSubj	the resource
	 * @param thePred	the property
	 * @return 			Optionally, the value of the property as a boolean.  Value will be absent if the SP does not have an O,
	 * 					or that O is not a literal or not a valid boolean value
	 */
	public static Optional<Boolean> getBooleanValue(final Model theGraph, final Resource theSubj, final IRI thePred) {
		Optional<Literal> aLitOpt = getLiteral(theGraph, theSubj, thePred);

		if (!aLitOpt.isPresent()) {
			return Optional.empty();
		}

		Literal aLiteral = aLitOpt.get();

		if (((aLiteral.getDatatype() != null && aLiteral.getDatatype().equals(XMLSchema.BOOLEAN))
		     || (aLiteral.getLabel().equalsIgnoreCase("true") || aLiteral.getLabel().equalsIgnoreCase("false")))) {
			return Optional.of(Boolean.valueOf(aLiteral.getLabel()));
		}
		else {
			return Optional.empty();
		}
	}

	/**
	 * Returns whether or not the given resource is a rdf:List
	 *
	 * @param theGraph	the graph
	 * @param theRes	the resource to check
	 *
	 * @return			true if its a list, false otherwise
	 */
	public static boolean isList(final Model theGraph, final Resource theRes) {
		return theRes != null && (theRes.equals(RDF.NIL) || theGraph.stream().filter(Statements.matches(theRes, RDF.FIRST, null)).findFirst().isPresent());
	}

	/**
	 * Return the contents of the given list by following the rdf:first/rdf:rest structure of the list
	 * @param theGraph	the graph
	 * @param theRes	the resource which is the head of the list
	 *
	 * @return 			the contents of the list.
	 */
	public static List<Value> asList(final Model theGraph, final Resource theRes) {
		List<Value> aList = Lists.newArrayList();

		Resource aListRes = theRes;

		while (aListRes != null) {

			Optional<Resource> aFirst = getResource(theGraph, aListRes, RDF.FIRST);
			Optional<Resource> aRest = getResource(theGraph, aListRes, RDF.REST);

			if (aFirst.isPresent()) {
				aList.add(aFirst.get());
			}

			if (aRest.orElse(RDF.NIL).equals(RDF.NIL)) {
				aListRes = null;
			}
			else {
				aListRes = aRest.get();
			}
		}

		return aList;
	}

	/**
	 * Return the contents of the list serialized as an RDF list
	 * @param theResources	the list
	 * @return				the list as RDF
	 */
	public static Model toList(final List<Value> theResources) {
		Resource aCurr = SimpleValueFactory.getInstance().createBNode();

		int i = 0;
		Model aGraph = newModel();

		for (Value aRes : theResources) {
			Resource aNext = SimpleValueFactory.getInstance().createBNode();
			aGraph.add(aCurr, RDF.FIRST, aRes);
			aGraph.add(aCurr, RDF.REST, ++i < theResources.size() ? aNext : RDF.NIL);
			aCurr = aNext;
		}

		return aGraph;
	}
	/**
	 * Return an {@link Iterable} of the types of the {@link Resource} in the specified {@link Model}
	 *
	 * @param theGraph	the graph
	 * @param theRes	the resource
	 * @return			the asserted rdf:type's of the resource
	 */
	public static Iterable<Resource> getTypes(final Model theGraph, final Resource theRes) {
		return theGraph.stream()
		               .filter(Statements.matches(theRes, RDF.TYPE, null))
		               .map(Statement::getObject)
		               .map(theObject -> (Resource) theObject)
		               .collect(Collectors.toList());
	}

	public static boolean isInstanceOf(final Model theGraph, final Resource theSubject, final Resource theType) {
		return theGraph.contains(SimpleValueFactory.getInstance().createStatement(theSubject, RDF.TYPE, theType));
	}

}
