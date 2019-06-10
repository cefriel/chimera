package com.complexible.common.io;

import java.io.DataInput;
import java.io.IOException;

/**
 * <p>An interface for an object which can take input from {@link DataInput} and create an object out of the contents
 * of the input, similar to the {@link com.google.common.io.ByteProcessor} in Guava.</p>
 *
 * @author  Michael Grove
 * @since   1.0
 * @version 1.0
 *
 * @param <T> the type of object created
 */
public interface DataInputProcessor<T, I extends DataInput> {

    /**
     * Create an object from the contents of the input
     *
     * @param theInput  the input to read from
     * @return          the object created
     *
     * @throws IOException  if there was an error while reading from the input or the input contained a malformed object
     */
    public T processInput(final I theInput) throws IOException;
}
