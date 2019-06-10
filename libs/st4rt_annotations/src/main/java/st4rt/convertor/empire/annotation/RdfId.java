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
 * <p>Field level annotation to specify the field that should be used, if possible, to generate the objects
 * rdf:ID from.  This annotation will only be used the first time an object is turned into RDF, subsequent operations
 * will use the pre-existing rdf:ID and this annotation will be ignored.</p>
 *
 * @author Michael Grove
 * @since 0.1
 */
@Target({ElementType.FIELD}) @Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RdfId {
	//public static final String DEFAULT = "urn:clarkparsia.com:empire:";
	public static final String DEFAULT = "http://it2rail.org/id/";

	/**
	 * Return the namespace prefix to use when generating the rdf:ID
	 * @return the namespace prefix
	 */
	String namespace() default DEFAULT;
}
