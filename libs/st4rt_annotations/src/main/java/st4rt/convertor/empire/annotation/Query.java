package st4rt.convertor.empire.annotation;

import java.lang.annotation.*;

/**
 * @author Mohammad Mehdi Pourhashem Kallehbasti
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD}) @Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

	/**
	 * The type of query to be executed that can be either Lifting or Lowering
	 */
	enum QueryType {
		Lifting,
		Lowering
	}

	/**
	 * @return the name of corresponding query that should be looked up in queries.xml
	 */
	String name();

	/**
	 * set the maximum number of results returned by the query
	 */
	int limit() default 100000;
	/**
	 * @return the attributes names whose values are required for running this query.
	 */
	String[] inputs() default {};

	/**
	 * @return the attributes names whose values must be set using the query result
	 */
	String[] outputs() default {};

	/**
	 * @return the type of the query whose default value is Lifting. 
	 */
	public QueryType type() default QueryType.Lifting;

}