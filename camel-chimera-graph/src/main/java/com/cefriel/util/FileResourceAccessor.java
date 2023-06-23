package com.cefriel.util;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

        consumer.start();
        Exchange response = consumer.receive(callUrl);
        consumer.stop();

        return response;
    }
    public static String getFilePath(ChimeraResourceBean fileResource) {
        return fileResource.getUrl().replace(filePrefix, "");
    }
    public static InputStream getFileResource(ChimeraResourceBean resource) throws FileNotFoundException {
        String fileURI = resource.getUrl();
        fileURI = fileURI.replace(filePrefix, "");
        Path filePath = Paths.get(fileURI);
        return new FileInputStream(filePath.toString());
    }

    public static InputStream getFileResourceInputStream(ChimeraResourceBean resource, CamelContext context) {
        Exchange response = getFileResource(resource, context);
        String bodyAsString = response.getMessage().getBody(String.class);
        return new ByteArrayInputStream(bodyAsString.getBytes());
    }
}
