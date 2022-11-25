package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.FileResourceAccessor;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;

public class FileResourceAccessorTest {
    private final String filePathAbsolute = "C:/Users/grassi/Projects/chimera/graph-component/src/test/resources/file/base/test.ttl";
    @Test
    public void testReadFileFromAbsolutePath() {
        DefaultCamelContext context = new DefaultCamelContext();
        context.start();

        ChimeraResourceBean resource = new ChimeraResourceBean(filePathAbsolute, null);
        Exchange response = FileResourceAccessor.getFileResource(resource, context);
        context.stop();

        assert(!response.isFailed());
        assert(response.getMessage().getBody(String.class) != null);
        assert(!response.getMessage().getBody(String.class).equals(""));
    }
    // todo try file parsing with rdf format
}
