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

import java.lang.annotation.*;

/**
 * <p>Field level annotation to specify which RDF property a field and its value map to.</p>
 * <p>
 * Usage:
 * <code><pre>
 * &#64;Namespaces({"", "http://xmlns.com/foaf/0.1/",
 *			 "foaf", "http://xmlns.com/foaf/0.1/",
 * 		     "dc", "http://purl.org/dc/elements/1.1/"})
 * public class MyClass {
 *  ...
 * 	&#64;RdfProperty("foaf:firstName")
 *  public String firstName;
 * }
 * </pre></code>
 * </p>
 *
 * @author Michael Grove
 * @since 0.1
 */
@Target({ElementType.FIELD, ElementType.METHOD}) @Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RdfProperty {

	/**
	 * @return the list of @Link that is used to create a chain of triples
	 */
	Link[] links() default {};

	/**
	 * @return the name of the last property in the chain
	 */
	String propertyName();

	/**
	 * @return true if the annotated attribute contains more than one value (is a list)
	 */
	boolean isList() default false;

	/**
	 * For literal valued properties, this specifies which language tag to retrieve and save from the RDF
	 * @return the language value, such as 'en' or 'fr' or the empty string for any language typed literals including
	 * those without language types specified.
	 */
	String language() default "";

	/**
	 * @return the hard coded object for the last triple in the chain
	 * If the value is set the annotated attribute must not have @RdfsClass in its Java class.
	 */
//	String value() default "";
	String value() default "";

	boolean isXsdUri() default false;
	
	boolean jump2class() default false;

	String datatype() default "";
}
