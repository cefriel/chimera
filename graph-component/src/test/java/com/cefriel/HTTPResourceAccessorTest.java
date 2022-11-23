package com.cefriel;

import com.cefriel.util.AuthConfigBean;
import com.cefriel.util.AuthTokenConfigBean;
import com.cefriel.util.ChimeraResource;
import com.cefriel.util.HTTPResourceAccessor;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;

public class HTTPResourceAccessorTest {

    private final String testNoAuthEndpoint = "https://httpbin.org/get";
    private final String endpointUser = "foo";
    private final String endpointPwd = "bar";
    // this user and password will have to be matched by the auth info in the header of the request
    private final String authBasicEndpoint = "https://httpbin.org/basic-auth/" + endpointUser + "/" + endpointPwd;

    private final String authBearerEndpoint = "https://httpbin.org/bearer";
    @Test
    public void testGetUnprotectedHttpResource() throws InterruptedException {
        var context = new DefaultCamelContext();
        context.start();

        ChimeraResource resource = new ChimeraResource(testNoAuthEndpoint, null, null);
        Exchange response = HTTPResourceAccessor.getHTTPResource(resource, context);
        context.stop();
        int responseCode = response.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(!response.isFailed());
        assert(responseCode == 200);
        assert(response.getMessage().getBody(String.class) != null);
    }

    @Test
    public void testGetProtectedHttpResource() throws InterruptedException {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();
        AuthConfigBean authConfig = new AuthConfigBean(endpointUser, endpointPwd, "Basic");
        ChimeraResource resource = new ChimeraResource(authBasicEndpoint, null, authConfig);
        Exchange response = HTTPResourceAccessor.getHTTPResource(resource, context);
        context.stop();

        int responseCode = response.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(!response.isFailed());
        assert(responseCode == 200);
        // todo parse body from json and check that authenticated value is true (it already is, the call would fail otherwise but do it for completeness)
        assert(response.getMessage().getBody(String.class) != null);
    }
    @Test
    public void testAuthBearerEndpoint () {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();
        AuthTokenConfigBean authTokenConfig = new AuthTokenConfigBean("test");
        ChimeraResource resource = new ChimeraResource(authBearerEndpoint, null, authTokenConfig);
        Exchange response = HTTPResourceAccessor.getHTTPResource(resource, context);
        context.stop();

        int responseCode = response.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(!response.isFailed());
        assert(responseCode == 200);
    }
}
