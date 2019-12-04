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
import java.io.Serializable;

import com.google.common.base.Charsets;

/**
 * <p>Utility class which contains output funnels for common primitive types and serializables.</p>
 *
 * @author Michael Grove
 * @since   1.0
 * @version 1.0
 */
public final class DataOutputFunnels {

    /**
     * No instances
     */
    private DataOutputFunnels() {
        throw new AssertionError();
    }

    public static <T extends Serializable> DataOutputFunnel<T, DataOutput> serializable() {
        return new SerializableFunnel<T>();
    }

    public static enum Primitives implements DataOutputFunnel {
        Int(new DataOutputFunnel() {
            @Override
            public void funnel(final Object theObj, final DataOutput theTo) throws IOException {
                theTo.writeInt((Integer) theObj);
            }
        }),
        Long(new DataOutputFunnel() {
            @Override
            public void funnel(final Object theObj, final DataOutput theTo) throws IOException {
                theTo.writeLong((Long) theObj);
            }
        }),
        Float(new DataOutputFunnel() {
            @Override
            public void funnel(final Object theObj, final DataOutput theTo) throws IOException {
                theTo.writeFloat( (Float) theObj);
            }
        }),
        String(new DataOutputFunnel() {
            @Override
            public void funnel(final Object theObj, final DataOutput theTo) throws IOException {
                final String aObj = (String) theObj;
                final byte[] aBytes = aObj.getBytes(Charsets.UTF_8);

                theTo.writeInt(aBytes.length);
                theTo.write(aBytes);
            }
        });

        private final DataOutputFunnel mOutputWriter;

        private Primitives(final DataOutputFunnel theOutputWriter) {
            mOutputWriter = theOutputWriter;
        }

        @Override
        public void funnel(final Object theObj, final DataOutput theTo) throws IOException {
            mOutputWriter.funnel(theObj, theTo);
        }
    }

    private static final class SerializableFunnel<T extends Serializable> implements DataOutputFunnel<T, DataOutput> {

        @Override
        public void funnel(final T theObj, final DataOutput theTo) throws IOException {
            byte[] aBytes = ByteStreams2.toByteArray(theObj);

            theTo.writeInt(aBytes.length);
            theTo.write(aBytes);
        }
    }
}
