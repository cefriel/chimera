package com.cefriel.util;

import org.apache.camel.CamelContext;

import java.io.InputStream;

public class ResourceAccessor {
    private final static String filePrefix = "file://";
    private final static String httpPrefix = "http://";
    private final static String httpsPrefix = "https://";
    private final static String classPathPrefix = "classpath://";

    public static InputStream open(ChimeraResourceBean resource, CamelContext context) {
        if (resource.getUrl().startsWith(filePrefix)) {
            return FileResourceAccessor.getFileResourceInputStream(resource, context);
        }
        else if (resource.getUrl().startsWith(httpPrefix) || resource.getUrl().startsWith(httpsPrefix)) {
            var response = HTTPResourceAccessor.getHTTPResourceInputStream(resource, context);
            if (response.isPresent())
                return response.get();
        }
        else if (resource.getUrl().startsWith(classPathPrefix)){
            String res = resource.getUrl();
            res = res.replace(classPathPrefix, "");
            return UniLoader.class.getClassLoader().getResourceAsStream(res);
        }
        else {
            throw new UnsupportedOperationException("Can not open resource" + resource.getUrl());
        }
        return null;
    }
}
