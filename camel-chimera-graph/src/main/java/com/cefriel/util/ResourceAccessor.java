package com.cefriel.util;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

public class ResourceAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceAccessor.class);

    sealed interface ChimeraResource permits ClassPathResource, FileResource, HeaderResource, HttpResource, PropertyResource {}
    private record FileResource (String url, String serializationFormat) implements ChimeraResource {}
    private record HttpResource (String url, String serializationFormat, TypeAuthConfig authConfig) implements ChimeraResource {}
    private record HeaderResource (String url, String serializationFormat) implements ChimeraResource {}
    private record PropertyResource (String url, String serializationFormat) implements ChimeraResource {}
    private record ClassPathResource(String url, String serializationFormat) implements ChimeraResource {}

    private final static String filePrefix = "file://";
    private final static String httpPrefix = "http://";
    private final static String httpsPrefix = "https://";
    private final static String classPathPrefix = "classpath://";
    private final static String headerPrefix = "header://";
    private final static String propertyPrefix = "property://";

    public static InputStream open(ChimeraResourceBean resource, Exchange exchange) throws Exception {
        ChimeraResource chimeraResource = specialize(resource);

        if (chimeraResource instanceof FileResource)
            return open((FileResource) chimeraResource);
        else if (chimeraResource instanceof HttpResource)
            return open((HttpResource) chimeraResource, exchange.getContext());
        else if (chimeraResource instanceof ClassPathResource)
            return open((ClassPathResource) chimeraResource);
        else if (chimeraResource instanceof HeaderResource)
            return open((HeaderResource) chimeraResource, exchange);
        else if (chimeraResource instanceof PropertyResource)
            return open((PropertyResource) chimeraResource, exchange);
        else
            throw new InvalidParameterException("Resource: " + chimeraResource + " is not of a supported type.");
    }

    private static InputStream open(FileResource resource) throws FileNotFoundException {
        String fileURI = resource.url();
        fileURI = fileURI.replace(filePrefix, "");
        Path filePath = Paths.get(fileURI);
        return new FileInputStream(filePath.toString());
    }

    private static InputStream open(HttpResource resource, CamelContext context) throws Exception {
        ProducerTemplate producer = context.createProducerTemplate();
        ExchangeBuilder exchangeRequestTemp =  ExchangeBuilder.anExchange(context).withHeader(Exchange.HTTP_METHOD, "GET");

        String callUrl;
        TypeAuthConfig authConfig = resource.authConfig();
        // ugly syntax that is fixed with switch pattern matching (requires bump to java 19)
        if (authConfig instanceof AuthTokenConfigBean) {
            exchangeRequestTemp.withHeader("Authorization", "Bearer " + ((AuthTokenConfigBean) authConfig).getAuthToken());
            callUrl = resource.url();
        } else if (authConfig instanceof AuthConfigBean) {
            callUrl = resource.url() + "?" + authConfig.toString();
        } else {
            // if no AuthConfig is passed, might be because it is not needed or because of a mistake
            callUrl = resource.url();
        }
        Exchange exchangeRequest = exchangeRequestTemp.build();

        Exchange response = producer.send(callUrl, exchangeRequest);

        if (response.getException() != null) {
            // todo should this cause an exception or handle resource as null?
            throw response.getException();
        }

        return response.getMessage().getBody(InputStream.class);
    }

    private static InputStream open(HeaderResource resource, Exchange exchange) throws NoSuchHeaderException {
        // assume that a resource can only specify one header
        String headerName = resource.url().replace("header://", "");

        if (exchange.getMessage().getHeader(headerName) != null) {
            return exchange.getMessage().getHeader(headerName, InputStream.class);
        }
        else {
            throw new NoSuchHeaderException(exchange, headerName, String.class);
        }
    }
    private static InputStream open(PropertyResource resource, Exchange exchange) throws NoSuchPropertyException {
        String propertyName = resource.url().replace("property://", "");
        if (exchange.getProperty(propertyName) != null)
            return exchange.getProperty(propertyName, InputStream.class);
        else
            throw new NoSuchPropertyException(exchange, propertyName, String.class);
    }

    private static InputStream open(ClassPathResource resource) {
        String res = resource.url();
        res = res.replace(classPathPrefix, "");
        return UniLoader.class.getClassLoader().getResourceAsStream(res);
    }

    private static ChimeraResource specialize (ChimeraResourceBean resourceBean) {
        if (resourceBean.getUrl().startsWith(filePrefix))
            return new FileResource(resourceBean.getUrl(), resourceBean.getSerializationFormat());
        else if (resourceBean.getUrl().startsWith(httpPrefix) || resourceBean.getUrl().startsWith(httpsPrefix))
            return new HttpResource(resourceBean.getUrl(), resourceBean.getSerializationFormat(), resourceBean.getAuthConfig());
        else if (resourceBean.getUrl().startsWith(headerPrefix))
            return new HeaderResource(resourceBean.getUrl(), resourceBean.getSerializationFormat());
        else if (resourceBean.getUrl().startsWith(propertyPrefix))
            return new PropertyResource(resourceBean.getUrl(), resourceBean.getSerializationFormat());
        else if (resourceBean.getUrl().startsWith(classPathPrefix))
            return new ClassPathResource(resourceBean.getUrl(), resourceBean.getSerializationFormat());
        else
            throw new InvalidParameterException("Resource: " + resourceBean + " with url " + resourceBean.getUrl() + " is not supported.");
    }
}
