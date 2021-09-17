/*
 * Copyright (c) 2019-2021 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cefriel.chimera.rml;

import be.ugent.rml.access.Access;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class InputStreamAccess implements Access {

    private String stream;
    private byte[] bytes;

    InputStreamAccess(String stream, Map<String, InputStream> inputStreamsMap) throws IOException {
        if (stream == null)
            throw new IOException("InputStream key not specified");
        if (inputStreamsMap == null)
            throw new IOException("InputStreamMap not initialised");
        this.stream  = stream;
        // Copy the stream to avoid consuming it
        synchronized (inputStreamsMap) {
            InputStream is = inputStreamsMap.get(stream);
            if (is == null)
                this.bytes = null;
            else {
                ByteArrayOutputStream baos = getOutputStream(is);
                inputStreamsMap.put(stream, new ByteArrayInputStream(baos.toByteArray()));
                this.bytes = baos.toByteArray();
            }
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.bytes != null)
            return new ByteArrayInputStream(bytes);
        else
            throw new IOException("InputStream " + stream + " not found in the InputStreamMap");
    }

    private ByteArrayOutputStream getOutputStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }

    /**
     * This methods returns the datatypes of the file.
     * This method always returns null, because the datatypes can't be determined from a input stream for the moment.
     * @return the datatypes of the file.
     */
    @Override
    public Map<String, String> getDataTypes() {
        return null;
    }

}