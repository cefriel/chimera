/*
 * Copyright (c) 2019-2021 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
    private static Logger logger = LoggerFactory.getLogger(UniLoader.class);

    public static InputStream open(String resource) throws MalformedURLException, IOException {
        return open(resource, null);
    }

    public static InputStream open(String resource, String token) throws MalformedURLException, IOException {
    	String res;
        InputStream is;
        logger.info("Loading resource " + resource);
        if (resource.startsWith("classpath://")) {
        	res = resource.replace("classpath://", "");
            is = UniLoader.class.getClassLoader().getResourceAsStream(res);
            return is;
        }
        if (resource.startsWith("file://")) {
        	res = resource.replace("file://", "");
            is = new FileInputStream(res);
            return is;
        }

        java.net.URL documentUrl = new URL(resource);
        HttpURLConnection con = (HttpURLConnection) documentUrl.openConnection();

        // Set up URL connection to get retrieve information back
        con.setRequestMethod("GET");
        if (token != null)
            con.setRequestProperty("Authorization", "Bearer " + token);
        // Pull the information back from the URL
        try {
            is = con.getInputStream();
        } catch (Exception e) {
            logger.warn("Connection failed. Resource: " + resource);
            return null;
        }

        return is;
    }
}