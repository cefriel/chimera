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

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Adapter to treat a {@link DataInput} as an InputStream.</p>
 *
 * @author  Michael Grove
 * @since   2.3.1
 * @version 2.3.1
 */
public class DataInputToInputStreamAdapter extends InputStream {
    private final DataInput mInput;

    private DataInputToInputStreamAdapter(final DataInput theInput) {
        mInput = theInput;
    }

    public static InputStream create(final DataInput theDataInput) {
        return theDataInput instanceof InputStream
               ? (InputStream) theDataInput
               : new DataInputToInputStreamAdapter(theDataInput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws IOException {
        try {
            return mInput.readByte();
        }
        catch (EOFException e) {
            return -1;
        }
    }
}
