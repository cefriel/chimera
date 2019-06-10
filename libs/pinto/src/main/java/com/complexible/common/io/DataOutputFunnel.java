/*
 * Copyright (c) 2005-2013 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

package com.complexible.common.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * <p>Similar to the Guava {@link com.google.common.hash.Funnel}, implementations of this class are expected to take the input provided to the funnel
 * method and write it to the DataOutput object.  To be used as an alternative to {@link java.io.Serializable} a way to generically encapsulate
 * and algorithm for serializing an object in some form.</p>
 *
 * <p>There is no requirement, generally, that the serialization must be complete such that the object can be reconstructed later; however,
 * concrete implementations can impose this as part of their design.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public interface DataOutputFunnel<T, O extends DataOutput> {

    /**
     * Send a stream of data from the source object into the DataOutput sink.
     *
     * @param theObj        the object to read from
     * @param theTo         the output to write to
     *
     * @throws IOException  if there is an error writing
     */
    public void funnel(final T theObj, final O theTo) throws IOException;
}
