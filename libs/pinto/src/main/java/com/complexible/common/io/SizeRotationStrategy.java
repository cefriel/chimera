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

import com.google.common.base.Preconditions;

/**
 * <p>A {@link FileRotationStrategy rotation strategy} which will rotate a file once it grows
 * past a certain size threshold (in bytes).</p>
 *
 * @author  Michael Grove
 * @since   1.0.1
 * @version 1.0.1
 */
public final class SizeRotationStrategy implements FileRotationStrategy {
    private final long mMaxSize;

    /**
     * Create a new SizeRotationStrategy
     * @param theMaxSize    the max log file size in bytes.
     */
    public SizeRotationStrategy(final long theMaxSize) {
        Preconditions.checkArgument(theMaxSize > 0);
        mMaxSize = theMaxSize;
    }

    public long getMaxSize() {
        return mMaxSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needsRotation(final File theFile) {
        return theFile.length() > mMaxSize;
    }
}
