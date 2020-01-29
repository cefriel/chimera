/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.processor.zip;

import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFolderProcessor implements Processor {

    private static Logger log = Logger.getLogger(ZipFolderProcessor.class.getName());

    private static final String FOLDER_TO_ZIP = "folder_to_zip";

    @Override
    public void process(Exchange exchange) throws Exception {
        String folder = exchange.getIn().getHeader(FOLDER_TO_ZIP, String.class);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        try (ZipOutputStream zs = new ZipOutputStream(outstream)) {
            Path pp = Paths.get(folder);
            Files.walk(pp)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println(e);
                        }
                    });
        }
        InputStream is = new ByteArrayInputStream(outstream.toByteArray());
        exchange.getMessage().setBody(is, InputStream.class);
        exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/zip");
        String filename = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
        if (filename == null)
            filename = "myzip";
        String context = exchange.getProperty(ProcessorConstants.CONTEXT_ID, String.class);
        if (context == null)
            context = "";
        exchange.getMessage().setHeader("Content-Disposition", "inline; filename=\"" + filename + "-" + context + "\"");
    }

}
