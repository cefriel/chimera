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
package com.cefriel.chimera.processor.rml.aggregate;

import be.ugent.rml.Initializer;
import com.cefriel.chimera.processor.rml.RMLProcessor;
import com.cefriel.chimera.rml.CamelAccessFactory;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMLLiftingAggregationStrategy extends RMLProcessor implements AggregationStrategy {

    private Logger logger = LoggerFactory.getLogger(RMLLiftingAggregationStrategy.class);

    private boolean message;

    public Exchange aggregate(Exchange exchange, Exchange rmlInitializer) {
        if (rmlInitializer != null) {
            Initializer initializer = rmlInitializer.getIn().getBody(Initializer.class);
            if (initializer != null) {
                logger.info("RML Initialization extract");
                setRmlInitializer(initializer);
            }
        }
        try {
            processRML(exchange, new CamelAccessFactory(exchange, message));
        } catch (Exception e) {
            logger.error("Error in RML lifting process");
            e.printStackTrace();
        }
        exchange.getMessage().removeHeaders("rml_*");
        return exchange;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

}
