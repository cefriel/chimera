/*
 * Copyright (c) 2005-2012 Clark & Parsia, LLC. <http://www.clarkparsia.com>
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>An {@link java.io.InputStream} that supports peeking at the next byte in the stream</p>
 *
 * @author  Michael Grove
 *
 * @since   2.3.1
 * @version 2.3.1
 */
public class PeekingInputStream extends InputStream {
    private final BufferedInputStream mStream;

    public PeekingInputStream(final InputStream theStream) {
        // wrapping in BufferedInputStream guarantees that mark/reset in peek will work
        mStream = theStream instanceof BufferedInputStream
                  ? (BufferedInputStream) theStream
                  : new BufferedInputStream(theStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        return mStream.read();
    }

    /**
     * Return the next byte that will be returned by {@link #read}
     * @return  the next byte to be read
     *
     * @throws java.io.IOException  if there is an error peeking at the next byte
     */
    public int peek() throws IOException {
        mStream.mark(1);
        int aByte = mStream.read();
        mStream.reset();
        return aByte;
    }
}
