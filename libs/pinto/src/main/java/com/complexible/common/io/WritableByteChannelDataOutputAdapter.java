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
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * <p>An adapter for a {@link java.io.DataOutput} to use it as a {@link java.nio.channels.WritableByteChannel}.</p>
 *
 * <p>This assumes it's always open, and is generally uncloseable.  It's the responsibility of the user to
 * manage the open/close status of whatever the underlying output of the {@code DataOutput} is.</p>
 *
 * @author  Michael Grove
 * @since   2.4.1
 * @version 2.4.1
 */
public final class WritableByteChannelDataOutputAdapter extends ForwardingDataOutput implements WritableByteChannel, DataOutput {

    public WritableByteChannelDataOutputAdapter(final DataOutput theOutput) {
        super(theOutput);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int write(final ByteBuffer theByteBuffer) throws IOException {
        byte[] aBytes = new byte[theByteBuffer.remaining()];
        theByteBuffer.get(aBytes);
        delegate().write(aBytes);
        return aBytes.length;
    }
}
