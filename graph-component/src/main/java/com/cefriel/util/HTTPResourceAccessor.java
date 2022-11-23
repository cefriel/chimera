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

        String callUrl = resource.getAuthConfig() == null ?
                resource.getUrl() :
                resource.getUrl() + "?" + resource.getAuthConfig().toString();

        Exchange exchangeRequest = exchangeRequestTemp.build();

        return producer.send(callUrl, exchangeRequest);
    }


}
