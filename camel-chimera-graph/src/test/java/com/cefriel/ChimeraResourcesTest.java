package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ChimeraResourcesTest extends CamelTestSupport {

    @Test
    public void testPropertyResource() throws Exception {
        ChimeraResourceBean r1 = new ChimeraResourceBean("property://test", null);
        Exchange exchange = new DefaultExchange(context());
        exchange.setProperty("test", "ciao");
        InputStream result = ResourceAccessor.open(r1, exchange);
        String x = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assert(x.equals("ciao"));
    }

    @Test
    public void testHeaderResource() throws Exception {
        ChimeraResourceBean r1 = new ChimeraResourceBean("header://test", null);
        Exchange exchange = new DefaultExchange(context());
        exchange.getMessage().setHeader("test", "ciao");
        InputStream result = ResourceAccessor.open(r1, exchange);
        String x = new String(result.readAllBytes(), StandardCharsets.UTF_8);
        assert(x.equals("ciao"));
    }





}
