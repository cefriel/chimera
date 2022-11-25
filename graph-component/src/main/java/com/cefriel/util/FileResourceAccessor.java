package com.cefriel.util;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResourceAccessor {

    private final static String filePrefix = "file://";
    public static Exchange getFileResource(ChimeraResourceBean resource, CamelContext context) {
        ConsumerTemplate consumer = context.createConsumerTemplate();
        String fileURI = resource.getUrl();
        fileURI = fileURI.replace(filePrefix, "");

        Path filePath = Paths.get(fileURI);
        String baseDirectory = filePath.getParent().toString();
        String fileName = filePath.getFileName().toString();

        String callUrl = filePrefix + baseDirectory + "/?filename=" + fileName;

        return consumer.receive(callUrl);
    }

    public static InputStream getFileResourceInputStream(ChimeraResourceBean resource, CamelContext context) {
        Exchange response = getFileResource(resource, context);
        String bodyAsString = response.getMessage().getBody(String.class);
        return new ByteArrayInputStream(bodyAsString.getBytes());
    }
}
