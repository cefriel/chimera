package com.cefriel;

import com.cefriel.util.ChimeraResource;
import com.cefriel.util.FileResourceAccessor;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

public class FileResourceAccessorTest {
    private final String windowsFilePath = "C:\\Users\\grassi\\Projects\\chimera\\graph-component\\src\\test\\resources\\file\\base\\test.ttl";

    @Test
    public void testReadFileFromPath() {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();

        ChimeraResource resource = new ChimeraResource(windowsFilePath, null, null);
        Exchange response = FileResourceAccessor.getFileResource(resource, context);
        context.stop();

        assert(!response.isFailed());
        assert(response.getMessage().getBody(String.class) != null);
    }
}
