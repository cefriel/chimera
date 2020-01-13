package com.cefriel.chimera.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniLoader {
    private static Logger log = LoggerFactory.getLogger(UniLoader.class);

    public static InputStream open(String resource) throws MalformedURLException, IOException {
    	String res;
        log.warn("Loading resource " + resource);
        if (resource.startsWith("classpath://")) {
        	res = resource.replace("classpath://", "");
            log.warn("Loading classpath resource "+res);
            InputStream is = UniLoader.class.getClassLoader().getResourceAsStream(res);
            return is;
        }
        if (resource.startsWith("file://")) {
        	res = resource.replace("file://", "");
            log.warn("Loading file resource " + res);
            InputStream is = new FileInputStream(res);
            return is;
        }
        else {
            log.warn("Loading resource " + resource);
            return new URL(resource).openStream();
        }
    }
}