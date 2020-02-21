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
package com.cefriel.chimera.processor.collect;

import com.cefriel.chimera.util.RecordCollector;
import com.cefriel.chimera.util.RecordProcessorConstants;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordCollectorProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(RecordCollectorProcessor.class);

    private String collectorId;
    private String filepath = "./records.txt";
    private int bufferSize = -1;
    private boolean append = true;

    @Override
    public void process(Exchange exchange) throws Exception {
        String collectorId = exchange.getMessage().getHeader(RecordProcessorConstants.COLLECTOR_ID, String.class);
        if (collectorId == null)
            collectorId = this.collectorId;
        if (collectorId == null) {
            logger.info("Collector ID not found. Attach it to header using as key " + RecordProcessorConstants.COLLECTOR_ID);
            return;
        }

        RecordCollector collector = exchange.getProperty(collectorId, RecordCollector.class);
        if (collector == null) {
            String filepath;
            int bufferSize;
            filepath = exchange.getMessage().getHeader(RecordProcessorConstants.COLLECTOR_FILEPATH, String.class);
            String s_bufferSize = exchange.getMessage().getHeader(RecordProcessorConstants.COLLECTOR_BUFFER_SIZE, String.class);


            if (filepath == null)
                filepath = this.filepath;
            if(s_bufferSize != null)
                bufferSize = Integer.parseInt(s_bufferSize);
            else
                bufferSize = this.bufferSize;

            collector = new RecordCollector(filepath, bufferSize, append);
            exchange.setProperty(collectorId, collector);
        } else
            collector.saveData(true);
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }
}
