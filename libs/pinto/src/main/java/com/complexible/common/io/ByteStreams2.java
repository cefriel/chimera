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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.google.common.primitives.Ints;

/**
 * <p>Utility class to hold functionality not already contained in {@link com.google.common.io.ByteStreams}</p>
 *
 * @author  Michael Grove
 * @since   2.3.1
 * @version 3.1.2
 */
public final class ByteStreams2 {

    /**
     * No instances
     */
    private ByteStreams2() {
        throw new AssertionError();
    }

    /**
     * Convert the object into an array of bytes.  The object <b>must</b> be {@link java.io.Serializable}
     * as this will use {@link ObjectOutputStream} to convert the object into a byte array.
     * The results of this method can later be used in conjunction with {@link java.io.ObjectInputStream}
     * to re-create the object.
     *
     * @param theObj    the object to convert into a byte array
     * @return          the object as an array of bytes in its serialized form
     *
     * @throws IOException  if there was an error while serializing the object
     */
    public static byte[] toByteArray(final Object theObj) throws IOException {

        ByteArrayOutputStream aByteOutput = new ByteArrayOutputStream();
        ObjectOutputStream aObjOut = new ObjectOutputStream(aByteOutput);
        aObjOut.writeObject(theObj);
        aObjOut.flush();

        return aByteOutput.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromByteArray(final byte[] theBytes) throws IOException, ClassNotFoundException, ClassCastException {
        return (T) new ObjectInputStream(new ByteArrayInputStream(theBytes)).readObject();
    }

    public static int readInt(final InputStream theStream) throws IOException {
        byte[] array = new byte[Ints.BYTES];
        ByteStreams.readFully(theStream, array);
        return Ints.fromByteArray(array);
    }

    public static void writeInt(final OutputStream theStream, final int theInt) throws IOException {
	    theStream.write((byte) (theInt >> 24));
	    theStream.write((byte) (theInt >> 16));
	    theStream.write((byte) (theInt >> 8));
	    theStream.write((byte) (theInt));
    }

	public static void writeLong(final OutputStream theStream, final long theLong) throws IOException {
		theStream.write((byte) (theLong >> 56));
		theStream.write((byte) (theLong >> 48));
		theStream.write((byte) (theLong >> 40));
		theStream.write((byte) (theLong >> 32));
		theStream.write((byte) (theLong >> 24));
		theStream.write((byte) (theLong >> 16));
		theStream.write((byte) (theLong >> 8));
		theStream.write((byte) (theLong));
	}

    public static byte[] gzip(final byte[] theBytes) throws IOException {
        final ByteArrayOutputStream aOut = new ByteArrayOutputStream(theBytes.length);

	    final GZIPOutputStream aZipped = new GZIPOutputStream(aOut);
	    final ByteArrayInputStream aIn = new ByteArrayInputStream(theBytes);

	    Closer aCloser = Closer.create();
	    aCloser.register(aZipped);
	    aCloser.register(aIn);

	    try {
		    ByteStreams.copy(aIn, aZipped);
	    }
	    finally {
		    aCloser.close();
	    }

        return aOut.toByteArray();
    }
}
