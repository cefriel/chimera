/*
 * Copyright (c) 2019-2022 Cefriel.
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

package com.cefriel.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
// todo check if still best method to copy inpustream to use multiple times
public class TemplateLowererInitializer {

    private final List<byte[]> templates;

    public TemplateLowererInitializer(List<InputStream> templateStreams) throws IOException {
        if (templateStreams == null)
            this.templates = null;
        else {
            templates = new ArrayList<>();
            for (InputStream is : templateStreams) {
                ByteArrayOutputStream baos = getOutputStream(is);
                templates.add(baos.toByteArray());
                baos.close();
            }
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

    public InputStream getTemplateStream(int index) throws IOException {
        if (this.templates != null)
            return new ByteArrayInputStream(templates.get(index));
        else
            throw new IOException("InputStream not initialized");
    }

    public int getNumberTemplates () {
        if (templates != null)
            return templates.size();
        else
            return 0;
    }
}
