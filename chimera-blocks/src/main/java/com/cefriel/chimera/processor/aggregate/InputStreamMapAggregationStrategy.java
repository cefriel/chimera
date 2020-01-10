package com.cefriel.chimera.processor.aggregate;

import com.cefriel.chimera.util.ProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class InputStreamMapAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Map<String, InputStream> newBody = newExchange.getIn().getBody(Map.class);
        Map<String, InputStream> isMap;
        if (oldExchange == null) {
            isMap = new HashMap<>();
            isMap.putAll(newBody);
            newExchange.getIn().setBody(isMap);
            return newExchange;
        } else {
            isMap = oldExchange.getIn().getBody(Map.class);
            isMap.putAll(newBody);
            return oldExchange;
        }
    }

}
