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

package com.cefriel.chimera.processor.onexception;

import com.cefriel.chimera.processor.DumpGraph;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnExceptionInspectProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(OnExceptionInspectProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception ex = exchange.getException();
        logger.info("exchange.isFailed(): " + exchange.isFailed());
        if(ex != null) {
            logger.info("Exception type " + ex.getClass().getCanonicalName());
            logger.info("Message " + ex.getMessage());
            logger.info("Exception ", ex);
            logger.info("Cause ", ex.getCause());
        }
    }
}
