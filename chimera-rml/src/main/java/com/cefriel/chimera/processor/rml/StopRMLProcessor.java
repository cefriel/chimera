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

package com.cefriel.chimera.processor.rml;

import be.ugent.rml.ConcurrentExecutor;
import be.ugent.rml.store.ConcurrentRDF4JRepository;
import com.cefriel.chimera.processor.StopProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class StopRMLProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        StopProcessor sp = new StopProcessor();
        sp.process(exchange);

        if (RMLProcessor.executorService != null)
            RMLProcessor.executorService.shutdown();
        if (ConcurrentRDF4JRepository.executorService != null)
            ConcurrentRDF4JRepository.executorService.shutdown();
        if (ConcurrentExecutor.executorService != null)
            ConcurrentExecutor.executorService.shutdown();
    }
}
