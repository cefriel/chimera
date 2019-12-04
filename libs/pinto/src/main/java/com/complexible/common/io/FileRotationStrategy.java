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


import java.io.File;

/**
 * <p>Interface for implementing file rotation policies.</p>
 *
 * @author  Michael Grove
 * @since   1.0.1
 * @version 1.0.1
 *
 * @see SizeRotationStrategy
 * @see TimeRotationStrategy
 */
public interface FileRotationStrategy {
    /**
     * Return whether or not the file needs to be rotated based on the policy this interface implements
     * @param theFile   the file
     * @return          true if the file needs to be rotated, false otherwise
     */
    public boolean needsRotation(final File theFile);
}
