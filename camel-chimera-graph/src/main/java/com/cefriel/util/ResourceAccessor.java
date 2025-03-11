package com.cefriel.util;

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
    public static InputStream open(ChimeraResourceBean resource, Exchange exchange) throws Exception {
        ChimeraResource chimeraResource = resource.specialize();

        if (chimeraResource instanceof ChimeraResource.FileResource fileResource)
            return open(fileResource);
        else if (chimeraResource instanceof ChimeraResource.HttpResource httpResource)
            return open(httpResource, exchange.getContext());
        else if (chimeraResource instanceof ChimeraResource.ClassPathResource classPathResource)
            return open(classPathResource);
        else if (chimeraResource instanceof ChimeraResource.HeaderResource headerResource)
            return open(headerResource, exchange);
        else if (chimeraResource instanceof ChimeraResource.PropertyResource propertyResource)
            return open(propertyResource, exchange);
        else if (chimeraResource instanceof ChimeraResource.VariableResource variableResource)
            return open(variableResource, exchange);
        else
            throw new InvalidParameterException("Resource: " + chimeraResource + " is not of a supported type.");
    }

    private static InputStream open(ChimeraResource.FileResource resource) throws FileNotFoundException {
        String fileURI = resource.url();
        fileURI = fileURI.replace(ChimeraResourceConstants.FILE_PREFIX, "");
        Path filePath = Paths.get(fileURI);
        return new FileInputStream(filePath.toString());
    }

    private static InputStream open(ChimeraResource.HttpResource resource, CamelContext context) throws Exception {
        ProducerTemplate producer = context.createProducerTemplate();
        ExchangeBuilder exchangeRequestTemp =  ExchangeBuilder.anExchange(context).withHeader(Exchange.HTTP_METHOD, "GET");

        String callUrl;
        TypeAuthConfig authConfig = resource.authConfig();
        // ugly syntax that is fixed with switch pattern matching (requires bump to java 19)
        if (authConfig instanceof AuthTokenConfigBean authTokenConfigBean) {
            exchangeRequestTemp.withHeader("Authorization", "Bearer " + authTokenConfigBean.getAuthToken());
            callUrl = resource.url();
        } else if (authConfig instanceof AuthConfigBean authConfigBean) {
            callUrl = resource.url() + "?" + authConfigBean.toString();
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

    private static InputStream open(ChimeraResource.HeaderResource resource, Exchange exchange) throws NoSuchHeaderException {
        // assume that a resource can only specify one header
        String headerName = resource.url().replace(ChimeraResourceConstants.HEADER_PREFIX, "");

        if (exchange.getMessage().getHeader(headerName) != null) {
            return exchange.getMessage().getHeader(headerName, InputStream.class);
        }
        else {
            throw new NoSuchHeaderException(exchange, headerName, String.class);
        }
    }
    private static InputStream open(ChimeraResource.PropertyResource resource, Exchange exchange) throws NoSuchPropertyException {
        String propertyName = resource.url().replace(ChimeraResourceConstants.PROPERTY_PREFIX, "");
        if (exchange.getProperty(propertyName) != null)
            return exchange.getProperty(propertyName, InputStream.class);
        else
            throw new NoSuchPropertyException(exchange, propertyName, String.class);
    }

    private static InputStream open(ChimeraResource.VariableResource resource, Exchange exchange) throws NoSuchVariableException {
        String variableName = resource.url().replace(ChimeraResourceConstants.VARIABLE_PREFIX, "");
        if (exchange.getVariable(variableName) != null)
            return exchange.getVariable(variableName, InputStream.class);
        else
            throw new NoSuchVariableException(exchange, variableName, String.class);
    }

    private static InputStream open(ChimeraResource.ClassPathResource resource) {
        String res = resource.url();
        res = res.replace(ChimeraResourceConstants.CLASSPATH_PREFIX, "");
        return UniLoader.class.getClassLoader().getResourceAsStream(res);
    }
}
