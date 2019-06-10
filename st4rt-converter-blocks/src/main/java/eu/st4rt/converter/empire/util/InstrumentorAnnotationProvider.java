/*
 * Copyright (c) 2009-2011 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package eu.st4rt.converter.empire.util;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import eu.st4rt.converter.empire.spi.Instrumentor;

/**
 * <p>Implementation of the AnnotationProvider class which uses the JVM java-agent {@link Instrumentor} to provide information about annotations on the current code.</p>
 *
 * @author Michael Grove
 * @version 0.7
 * @since 0.7
 */
final class InstrumentorAnnotationProvider implements EmpireAnnotationProvider {

	/**
	 * @inheritDoc
	 */
	public Collection<Class<?>> getClassesWithAnnotation(final Class<? extends Annotation> theAnnotation) {
		return Instrumentor.isInitialized() ? Instrumentor.annotatedWith(theAnnotation) : Collections.<Class<?>>emptySet();
	}
}
