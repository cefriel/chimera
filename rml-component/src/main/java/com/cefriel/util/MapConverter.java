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

package com.cefriel.util;

import com.cefriel.component.RmlBean;
import org.apache.camel.Exchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapConverter {

    public static void inputStreamConvert(Exchange exchange) throws Exception {

        RmlBean configuration = exchange.getMessage().getHeader(ChimeraRmlConstants.RML_CONFIG, RmlBean.class);
        String prefix = configuration.getPrefixLogicalSource();
        if(prefix == null)
            prefix = "is://";
        InputStream is = exchange.getMessage().getBody(InputStream.class);
        String streamName = exchange.getMessage().getHeader(Exchange.FILE_NAME, String.class);
        if (streamName == null)
            streamName = configuration.getStreamName();
        if (is != null && streamName != null) {
            Map<String, InputStream> map = new HashMap<>();
            map.put(prefix + streamName, is);
            exchange.getMessage().setBody(map, Map.class);
        }
    }

    public static void fileConvert(Exchange exchange, List<String> files) throws IOException {

        RmlBean configuration = exchange.getMessage().getHeader(ChimeraRmlConstants.RML_CONFIG, RmlBean.class);
        String prefix = configuration.getPrefixLogicalSource();
        if(prefix == null)
            prefix = "is://";
        Map<String, InputStream> map = new HashMap<>();
        for (String file : files) {
            InputStream is = UniLoader.open(file);
            Path path = Path.of(file);
            map.put(prefix + path.getFileName(), is);
        }
        exchange.getMessage().setBody(map);
    }
}
