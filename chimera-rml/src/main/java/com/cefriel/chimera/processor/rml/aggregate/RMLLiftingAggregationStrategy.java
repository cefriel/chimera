/*
 * Copyright 2020 Cefriel.
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
package com.cefriel.chimera.processor.rml.aggregate;

import be.ugent.rml.Initializer;
import com.cefriel.chimera.processor.rml.RMLOptions;
import com.cefriel.chimera.processor.rml.RMLProcessor;
import com.cefriel.chimera.rml.CamelAccessFactory;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMLLiftingAggregationStrategy implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(RMLLiftingAggregationStrategy.class);

    private RMLOptions rmlOptions;
    private boolean message;

    public Exchange aggregate(Exchange exchange, Exchange rmlInitializer) {
        RMLProcessor processor = new RMLProcessor();
        if (rmlOptions != null)
            processor.setDefaultRmlOptions(rmlOptions);
        if (rmlInitializer != null) {
            Initializer initializer = rmlInitializer.getIn().getBody(Initializer.class);
            if (initializer != null) {
                logger.info("RML Initialization extract");
                processor.setRmlInitializer(initializer);
            }
        }
        try {
            processor.processRML(exchange, new CamelAccessFactory(exchange, message));
        } catch (Exception e) {
            logger.error("Error in RML lifting process");
            e.printStackTrace();
        }
        return exchange;
    }

    public RMLOptions getRmlOptions() {
        return rmlOptions;
    }

    public void setRmlOptions(RMLOptions rmlOptions) {
        this.rmlOptions = rmlOptions;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

}
