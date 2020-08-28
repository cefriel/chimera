/*
 * Copyright 2020 Cefriel.
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

package com.cefriel.chimera.lowerer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TemplateLowererInitializer {

    private byte[] bytes;

    public TemplateLowererInitializer(InputStream templateStream) throws IOException {
        if (templateStream == null)
            this.bytes = null;
        else {
            ByteArrayOutputStream baos = getOutputStream(templateStream);
            this.bytes = baos.toByteArray();
        }
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

    public InputStream getTemplateStream() throws IOException {
        if (this.bytes != null)
            return new ByteArrayInputStream(bytes);
        else
            throw new IOException("InputStream not initialized");
    }
}
