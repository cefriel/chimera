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

package com.cefriel.rdf;

import com.cefriel.component.RdfTemplateBean;
import com.cefriel.template.TemplateExecutor;
import com.cefriel.template.TemplateMap;
import com.cefriel.template.io.Reader;
import com.cefriel.template.utils.TemplateUtils;
import com.cefriel.util.*;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RdfTemplateProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RdfTemplateProcessor.class);
    private record OperationParams (InputStream inputStream,
                                    ChimeraResourceBean template,
                                    ChimeraResourceBean query,
                                    String format,
                                    String basePath,
                                    String outFileName,
                                    boolean trim,
                                    boolean verboseQuery,
                                    ChimeraResourceBean kv,
                                    ChimeraResourceBean kvCSV,
                                    String baseIRI,
                                    boolean isStream,
                                    String timingFileName) {} // todo add other options which are not currently handled (time, ts adress maybe others)
    private static OperationParams getOperationParams(Exchange exchange, RdfTemplateBean operationConfig) {
        String baseIri = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
        return new OperationParams(
                exchange.getProperty(RdfTemplateConstants.TEMPLATE_STREAM, InputStream.class),
                operationConfig.getTemplate(),
                operationConfig.getQuery(),
                operationConfig.getFormat(),
                Utils.trailingSlash(operationConfig.getBasePath()),
                handleOutputFileName(operationConfig.getFilename()),
                operationConfig.isTrimTemplate(),
                operationConfig.isVerboseQueries(),
                operationConfig.getKeyValuePairs(),
                operationConfig.getKeyValuePairsCSV(),
                baseIri == null ? ChimeraConstants.DEFAULT_BASE_IRI : baseIri,
                operationConfig.isStream(),
                operationConfig.getTimingFileName());
    }

    private static boolean validateParams(OperationParams params) {
        if (params.isStream()) {
            if(params.inputStream() == null)
                throw new IllegalArgumentException("The template stream cannot be null when processing as stream");
            if(params.query() != null)
                throw new IllegalArgumentException("Parametric queries not supported when processing as stream");
        }
        return true;
    }
    public static void execute(Exchange exchange, RdfTemplateBean configuration, Reader reader) throws Exception {
        OperationParams params = getOperationParams(exchange, configuration);
        execute(params, exchange, reader);
    }

    private static void execute(OperationParams params, Exchange exchange, Reader reader) throws Exception {
        if (validateParams(params)) {
            TemplateExecutor templateExec = configureTemplateOptions(params, exchange.getContext(), reader, exchange);
            if(params.isStream()) {
                templateExec.lower(params.inputStream());
            }
            else {
                if (params.query() == null) {
                    String outPath = params.basePath() + params.outFileName();
                    templateExec.lower(FileResourceAccessor.getFilePath(params.template()), outPath, null);
                }
                if (params.query() != null) {
                    String outPath = params.basePath() + params.outFileName();
                    templateExec.lower(FileResourceAccessor.getFilePath(params.template()), outPath, FileResourceAccessor.getFilePath(params.query()));
                    String filename = params.outFileName().replaceFirst("[.][^.]+$", "");
                    List<String> result;
                    try (Stream<Path> walk = Files.walk(Paths.get(params.basePath()))) {
                        result = walk.map(x -> x.getFileName().toString())
                                .filter(f -> f.contains(filename))
                                .collect(Collectors.toList());
                    }
                    List<String> outputs = new ArrayList<>();
                    for (String f : result)
                        outputs.add(params.basePath() + f);
                    exchange.getMessage().setBody(outputs, List.class);
                }
                else {
                    exchange.getMessage().setBody(params.outFileName());
                }
            }
        }
    }
    private static TemplateExecutor configureTemplateOptions(OperationParams params, CamelContext context, Reader reader, Exchange exchange) throws Exception {
        TemplateUtils templateUtils = new TemplateUtils();
        templateUtils.setPrefix(params.baseIRI());
        TemplateExecutor templateExec = new TemplateExecutor(reader, templateUtils);
        if(reader != null)
            reader.setVerbose(params.verboseQuery());

        TemplateMap templateMap = new TemplateMap();
        if (params.kvCSV() != null)
            templateMap.parseMap(ResourceAccessor.open(params.kvCSV(), context), true);
        else if (params.kv() != null)
            templateMap.parseMap(ResourceAccessor.open(params.kv(), context), false);
        if(!templateMap.isEmpty())
            logger.info("Map parsed containing " + templateMap.size() + " keys");
        templateExec.setMap(templateMap);

        if (params.format() != null)
            templateExec.setFormatter(MapTComponentUtils.getFormatter(params.format()));
        templateExec.setTrimTemplate(params.trim());
        new File(params.basePath()).mkdirs();
        String filepath = params.basePath() + params.outFileName();
        exchange.getMessage().setHeader("filepath", filepath);

        return templateExec;
    }
    private static String handleOutputFileName(String outputFileName) {
        return Objects.requireNonNullElseGet(outputFileName, () -> "mapt-" + UUID.randomUUID() + ".txt");
    }
}