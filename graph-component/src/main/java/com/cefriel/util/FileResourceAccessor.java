package com.cefriel.util;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResourceAccessor {

    public static Exchange getFileResource(ChimeraResource resource, CamelContext context) {
        ConsumerTemplate consumer = context.createConsumerTemplate();

        Path filePath = Paths.get(resource.getUrl());
        String baseDirectory = filePath.getParent().toString();
        String fileName = filePath.getFileName().toString();

        String callUrl = "file:/" + baseDirectory + "/?filename=" + fileName;

        return consumer.receive(callUrl);
    }
}
