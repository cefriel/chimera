package com.cefriel;

import com.cefriel.util.AuthBean;
import com.cefriel.util.ChimeraResource;
import com.cefriel.util.HTTPResourceAccessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class HTTPResourceAccessorTest extends CamelTestSupport {

    private final String unprotectedEndpoint = "https://loripsum.net/api";
    private final String endpointUser = "foo";
    private final String endpointPwd = "bar";
    // this user and password will have to be matched by the auth info in the header of the request
    private final String protectedEndpoint = "https://httpbin.org/basic-auth/" + endpointUser + "/" + endpointPwd;

    @Test
    public void testGetUnprotectedHttpResource() throws InterruptedException {
        var context = new DefaultCamelContext();
        context.start();

        ChimeraResource resource = new ChimeraResource(unprotectedEndpoint, null, null);
        Exchange response = HTTPResourceAccessor.getHTTPResource(resource, context);
        context.stop();
        int responseCode = response.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(!response.isFailed());
        assert(responseCode == 200);
        assert(response.getMessage().getBody(String.class) != null);
    }

    @Test
    public void testGetProtectedHttpResource() throws InterruptedException {
        var context = new DefaultCamelContext();
        context.start();
        var authConfig = new AuthBean(endpointUser, endpointPwd, "Basic");
        ChimeraResource resource = new ChimeraResource(protectedEndpoint, null, authConfig);
        Exchange response = HTTPResourceAccessor.getHTTPResource(resource, context);
        context.stop();

        int responseCode = response.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(!response.isFailed());
        assert(responseCode == 200);
        // todo parse body from json and check that authenticated value is true (it already is, the call would fail otherwise but do it for completeness)
        assert(response.getMessage().getBody(String.class) != null);
    }
}
