package com.cefriel.util;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Optional;

import static org.apache.camel.builder.Builder.constant;

public class HTTPResourceAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPResourceAccessor.class);

    public static Optional<Exchange> getHTTPResource(ChimeraResource.HttpResource resource, CamelContext context) {
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
            LOG.warn(response.getException().toString());
            response = null;
        }

        return Optional.ofNullable(response);
    }
    public static Optional<InputStream> getHTTPResourceInputStream(ChimeraResource.HttpResource resource, CamelContext context) {
        Optional<Exchange> response = getHTTPResource(resource, context);
        InputStream inputStream = null;

        if (response.isPresent()) {
            inputStream = response.get().getMessage().getBody(InputStream.class);
        }
        return Optional.ofNullable(inputStream);
    }


}
