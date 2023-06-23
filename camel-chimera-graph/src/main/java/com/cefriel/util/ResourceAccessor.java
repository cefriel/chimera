package com.cefriel.util;

import org.apache.camel.CamelContext;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceAccessor {
    private final static String filePrefix = "file://";
    private final static String httpPrefix = "http://";
    private final static String httpsPrefix = "https://";
    private final static String classPathPrefix = "classpath://";

    public static InputStream open(ChimeraResourceBean resource, CamelContext context) throws FileNotFoundException {
        if (isFileResource(resource)) {
            // return FileResourceAccessor.getFileResourceInputStream(resource, context);
            return FileResourceAccessor.getFileResource(resource);
        }
        else if (isHTTPResource(resource)) {
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

    public static boolean isFileResource(ChimeraResourceBean resource) {
        return resource.getUrl().startsWith(filePrefix);
    }

    public static boolean isHTTPResource(ChimeraResourceBean resource) {
        return resource.getUrl().startsWith(httpPrefix) || resource.getUrl().startsWith(httpsPrefix);
    }
}
