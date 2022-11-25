package com.cefriel;

import com.cefriel.util.AuthConfigBean;
import com.cefriel.util.AuthTokenConfigBean;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.HTTPResourceAccessor;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class HTTPResourceAccessorTest {

    private final String testNoAuthEndpoint = "https://httpbin.org/get";
    private final String endpointUser = "foo";
    private final String endpointPwd = "bar";
    // this user and password will have to be matched by the auth info in the header of the request
    private final String authBasicEndpoint = "https://httpbin.org/basic-auth/" + endpointUser + "/" + endpointPwd;

    private final String authBearerEndpoint = "https://httpbin.org/bearer";
    @Test
    public void testGetUnprotectedHttpResource() {
        var context = new DefaultCamelContext();
        context.start();

        ChimeraResourceBean resource = new ChimeraResourceBean(testNoAuthEndpoint, null, null);
        Optional<Exchange> response = HTTPResourceAccessor.getHTTPResource(resource, context);

        context.stop();

        if(response.isPresent()) {
            Exchange r = response.get();
            assert(!r.isFailed());
            int responseCode = r.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
            assert(responseCode == 200);
            assert(r.getMessage().getBody(String.class) != null);
        }
    }

    @Test
    public void testGetProtectedHttpResource() throws InterruptedException {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();
        AuthConfigBean authConfig = new AuthConfigBean(endpointUser, endpointPwd, "Basic");
        ChimeraResourceBean resource = new ChimeraResourceBean(authBasicEndpoint, null, authConfig);
        Optional<Exchange> response = null;
        response = HTTPResourceAccessor.getHTTPResource(resource, context);

        context.stop();

        assert (response != null);
        assert(!response.get().isFailed());
        int responseCode = response.get().getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(responseCode == 200);
        // todo parse body from json and check that authenticated value is true (it already is, the call would fail otherwise but do it for completeness)
        assert(response.get().getMessage().getBody(String.class) != null);
    }
    @Test
    public void testAuthBearerEndpoint () {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();
        AuthTokenConfigBean authTokenConfig = new AuthTokenConfigBean("test");
        ChimeraResourceBean resource = new ChimeraResourceBean(authBearerEndpoint, null, authTokenConfig);
        Optional<Exchange> response = null;
        response = HTTPResourceAccessor.getHTTPResource(resource, context);

        context.stop();

        assert (response != null);
        assert(!response.get().isFailed());
        int responseCode = response.get().getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
        assert(responseCode == 200);
    }
}
