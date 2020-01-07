/*
 * Copyright 2020 Cefriel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cefriel.chimera.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.dataformat.bindy.BindyCsvFactory;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BindyEmptyCsvAwareDataFormat extends BindyCsvDataFormat {

    private static final Logger LOG = LoggerFactory.getLogger(BindyCsvDataFormat.class);

    /**
     * check emptyStream and if CVSRecord is allow to process emptyStreams
     * avoid IllegalArgumentException and return empty list when unmarshalling
     */
    private boolean checkEmptyStream(BindyCsvFactory factory, InputStream inputStream) throws IOException {
        boolean allowEmptyStream = factory.isAllowEmptyStream();
        boolean isStreamEmpty = false;
        boolean canReturnEmptyListOfModels = false;
        
        if (inputStream == null || inputStream.available() == 0) {
            isStreamEmpty = true;
        }
        
        if (isStreamEmpty && allowEmptyStream) {
            canReturnEmptyListOfModels = true;
        }
        
        return canReturnEmptyListOfModels;
    }

    public Object unmarshal(Exchange exchange, InputStream inputStream) throws Exception {
        BindyCsvFactory factory = (BindyCsvFactory)getFactory();
        ObjectHelper.notNull(factory, "not instantiated");

        // List of Pojos
        List<Map<String, Object>> models = new ArrayList<Map<String, Object>>();

        // Pojos of the model
        Map<String, Object> model;
        InputStreamReader in = null;
        Scanner scanner = null;
        try {
            if (checkEmptyStream(factory, inputStream)) {
                return models;
            }
    
            in = new InputStreamReader(inputStream);
    
            // Scanner is used to read big file
            scanner = new Scanner(in);
    
            // Retrieve the separator defined to split the record
            String separator = factory.getSeparator();
            String quote = factory.getQuote();
            ObjectHelper.notNull(separator, "The separator has not been defined in the annotation @CsvRecord or not instantiated during initModel.");
    
            int count = 0;
            
            // If the first line of the CSV file contains columns name, then we
            // skip this line
            if (factory.getSkipFirstLine()) {
                // Check if scanner is empty
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
    
            while (scanner.hasNextLine()) {
    
                // Read the line
                String line = scanner.nextLine().trim();
    
                if (ObjectHelper.isEmpty(line)) {
                    // skip if line is empty
                    continue;
                }
    
                // Increment counter
                count++;
    
                // Create POJO where CSV data will be stored
                model = factory.factory();
    
                // Split the CSV record according to the separator defined in
                // annotated class @CSVRecord
                Pattern pattern = Pattern.compile(separator);
                Matcher matcher = pattern.matcher(line);
                List<String> separators = new ArrayList<String>();
                
                // Retrieve separators for each match
                while (matcher.find()) {
                    separators.add(matcher.group());
                }
                // Add terminal separator
                if (separators.size() > 0) {
                    separators.add(separators.get(separators.size() - 1));
                }
                
                String[] tokens = pattern.split(line, factory.getAutospanLine() ? factory.getMaxpos() : -1);
                List<String> result = Arrays.asList(tokens);
                // must unquote tokens before use
                result = unquoteTokens(result, separators, quote);
    
                if (result.size() == 0 || result.isEmpty()) {
                    throw new java.lang.IllegalArgumentException("No records have been defined in the CSV");
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Size of the record splitted : {}", result.size());
                    }
    
                    // Bind data from CSV record with model classes
                    factory.bind(getCamelContext(), result, model, count);
    
                    // Link objects together
                    factory.link(model);
    
                    // Add objects graph to the list
                    models.add(model);
    
                    LOG.debug("Graph of objects created: {}", model);
                }
            }
    
            // BigIntegerFormatFactory if models list is empty or not
            // If this is the case (correspond to an empty stream, ...)
            if (models.size() == 0) {
                throw new java.lang.IllegalArgumentException("No records have been defined in the CSV");
            } else {
                return extractUnmarshalResult(models);
            }

        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (in != null) {
                IOHelper.close(in, "in", LOG);
            }
        }

    }
    
    /**
     * Unquote the tokens, by removing leading and trailing quote chars,
     * as will handling fixing broken tokens which may have been split
     * by a separator inside a quote.
     */
    private List<String> unquoteTokens(List<String> result, List<String> separators, String quote) {
        // a current quoted token which we assemble from the broken pieces
        // we need to do this as we use the split method on the String class
        // to split the line using regular expression, and it does not handle
        // if the separator char is also inside a quoted token, therefore we need
        // to fix this afterwards
        StringBuilder current = new StringBuilder();
        boolean inProgress = false;
        List<String> answer = new ArrayList<String>();
        int idxSeparator = 0;

        //parsing assumes matching close and end quotes
        for (String s : result) {
            boolean canStart = false;
            boolean canClose = false;
            boolean cutStart = false;
            boolean cutEnd = false;
            if (s.startsWith(quote)) {
                //token is just a quote
                if (s.length() == 1) {
                    s = "";
                    //if token is a quote then it can only close processing if it has begun
                    if (inProgress) {
                        canClose = true;
                    } else {
                        canStart = true;
                    }
                } else {
                    //quote+"not empty"
                    cutStart = true;
                    canStart = true;
                }
            }

            //"not empty"+quote
            if (s.endsWith(quote)) {
                cutEnd = true;
                canClose = true;
            }

            //optimize to only substring once
            if (cutEnd || cutStart) {
                s = s.substring(cutStart ? 1 : 0, cutEnd ? s.length() - 1 : s.length());
            }

            // are we in progress of rebuilding a broken token
            if (inProgress) {
                current.append(separators.get(idxSeparator));
                current.append(s);

                if (canClose) {
                    answer.add(current.toString());
                    current.setLength(0);
                    inProgress = false;
                }
            } else {
                if (canStart && !canClose) {
                    current.append(s);
                    inProgress = true;
                } else {
                    //case where no quotes
                    answer.add(s);
                }
            }
            idxSeparator++;
        }

        // any left over from current?
        if (current.length() > 0) {
            answer.add(current.toString());
            current.setLength(0);
        }

        return answer;
    }

    
}
