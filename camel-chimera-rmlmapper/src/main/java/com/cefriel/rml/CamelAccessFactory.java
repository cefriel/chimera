/*
 * Copyright (c) 2019-2022 Cefriel.
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

package com.cefriel.rml;

import be.ugent.rml.NAMESPACES;
import be.ugent.rml.Utils;
import be.ugent.rml.access.*;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class creates Access instances for Camel Exchanges.
 */
public class CamelAccessFactory extends AccessFactory {

    private final Logger logger = LoggerFactory.getLogger(CamelAccessFactory.class);

    private static final String KEY_MESSAGE = "key_message";
    // Map used to directly access input streams
    private Map<String, InputStream> inputStreamsMap;

    /**
     * The constructor of the CamelAccessFactory.
     */
    public CamelAccessFactory(Exchange exchange) {
        this(exchange, false);
    }

    public CamelAccessFactory(Exchange exchange, boolean isMessage) {
        super(null);
        inputStreamsMap = new ConcurrentHashMap<>();
        if (isMessage) {
            InputStream is = exchange.getMessage().getBody(InputStream.class);
            inputStreamsMap.put(KEY_MESSAGE, is);
        } else {
            Map<String, InputStream> map = exchange.getMessage().getBody(Map.class);
            if (map != null) {
                logger.info("Removing null InputStreams in the given Map");
                map.values().removeIf(Objects::isNull);
                inputStreamsMap.putAll(map);
            }
        }
        if(isMessage)
            logger.info("Camel Message used as Logical Source");
        else
            logger.info("CamelAccessFactory Map: " + inputStreamsMap.keySet());
    }

    /**
     * This method returns an Access instance from a Camel Exchange based on the RML rules in rmlStore.
     * @param logicalSource the Logical Source for which the Access needs to be created.
     * @param rmlStore a QuadStore with RML rules.
     * @return an Access instance from a Camel Exchange based on the RML rules in rmlStore. If the RML Mapper is processing a message
     * the entire Exchange is cast as an InputStream accessible through the Access. Otherwise, the Exchange is cast as a Map String, InputStream
     * and the literal, referred through the RML property rml:source, is used as a key to access the Map and identify the InputStream
     * accessible through the Access. 
     */
     public Access getAccess(Term logicalSource, QuadStore rmlStore) {

        try {
            if (inputStreamsMap.containsKey(KEY_MESSAGE))
                return new InputStreamAccess(KEY_MESSAGE, inputStreamsMap);

            List<Term> sources = Utils.getObjectsFromQuads(rmlStore.getQuads(logicalSource, new NamedNode(NAMESPACES.RML + "source"), null));

            if (!sources.isEmpty()) {
                Term source = sources.get(0);
                if (source != null) {
                    String value = source.getValue();
                    if (value !=  null)
                        return new InputStreamAccess(value, inputStreamsMap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        throw new Error("The Logical Source does not have a source.");
    }
}
