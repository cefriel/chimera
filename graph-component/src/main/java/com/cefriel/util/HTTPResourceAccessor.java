package com.cefriel.util;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;

import static org.apache.camel.builder.Builder.constant;

public class HTTPResourceAccessor {
    public static Exchange getHTTPResource(ChimeraResource resource, Exchange exchange) {
        return getHTTPResource(resource, exchange.getContext());
    }
    public static Exchange getHTTPResource(ChimeraResource resource, CamelContext context) {
        ProducerTemplate producer = context.createProducerTemplate();
        ExchangeBuilder exchangeRequestTemp =  ExchangeBuilder.anExchange(context).withHeader(Exchange.HTTP_METHOD, "GET");

        String callUrl;
        TypeAuthConfig authConfig = resource.getAuthConfig();
        // ugly syntax that is fixed with switch pattern matching (requires bump to java 19)
        if (authConfig instanceof AuthTokenConfigBean) {
            exchangeRequestTemp.withHeader("Authorization", "Bearer " + ((AuthTokenConfigBean) authConfig).getAuthToken());
            callUrl = resource.getUrl();
        } else if (authConfig instanceof AuthConfigBean) {
            callUrl = resource.getUrl() + "?" + authConfig.toString();
        } else {
            // if no AuthConfig is passed, might be because it is not needed or because of a mistake
            callUrl = resource.getUrl();
        }
        Exchange exchangeRequest = exchangeRequestTemp.build();

        return producer.send(callUrl, exchangeRequest);
    }


}
