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

import java.io.OutputStream;

/**
 * <p>A {@link java.io.DataOutput} as an {@link OutputStream}, akin to {@link java.io.DataOutputStream} in that both classes are a {@link java.io.DataOutput}
 * and an {@link java.io.OutputStream}, so rather than declaring a parameter is type {@code DataOutputStream} you can revert
 * to using {@code <T extends OutputStream & DataOutput>} and be able to use either class.  Unlike {@link java.io.DataOutputStream},
 * this class assumes you have a {@code DataOutput} to start with rather than an {@link java.io.OutputStream}.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 */
public class OutputStreamDataOutput extends OutputStream implements DataOutput {
    private final DataOutput mOutput;

    public OutputStreamDataOutput(final DataOutput theInput) {
        mOutput = theInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] theBytes) throws IOException {
        mOutput.write(theBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] theBytes, final int theOffset, final int theLimit) throws IOException {
        mOutput.write(theBytes, theOffset, theLimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final int theInt) throws IOException {
        mOutput.write(theInt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBoolean(final boolean theBool) throws IOException {
        mOutput.writeBoolean(theBool);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeByte(final int theByte) throws IOException {
        mOutput.writeByte(theByte);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBytes(final String theString) throws IOException {
        mOutput.writeBytes(theString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChar(final int theChar) throws IOException {
        mOutput.writeChar(theChar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeChars(final String theString) throws IOException {
        mOutput.writeChars(theString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeDouble(final double theDouble) throws IOException {
        mOutput.writeDouble(theDouble);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeFloat(final float theFloat) throws IOException {
        mOutput.writeFloat(theFloat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInt(final int theInt) throws IOException {
        mOutput.writeInt(theInt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeLong(final long theLong) throws IOException {
        mOutput.writeLong(theLong);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeShort(final int theShort) throws IOException {
        mOutput.writeShort(theShort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeUTF(final String theString) throws IOException {
        mOutput.writeUTF(theString);
    }
}
