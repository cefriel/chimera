package com.cefriel.chimera.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniLoader {
    private static Logger log = LoggerFactory.getLogger(UniLoader.class);

    public static InputStream open(String resource) throws MalformedURLException, IOException {
        return open(resource, null);
    }

    public static InputStream open(String resource, String token) throws MalformedURLException, IOException {
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
        if (token != null) {
            java.net.URL documentUrl = new URL(resource);
            HttpURLConnection con = (HttpURLConnection) documentUrl.openConnection();

            // Set up URL connection to get retrieve information back
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("Accept", "application/x-turtle, application/rdf+xml");

            // Pull the information back from the URL
            return con.getInputStream();
        }
        else
            return new URL(resource).openStream();
    }
}