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

import com.cefriel.component.MaptTemplateBean;
import com.cefriel.graph.RDFGraph;
import com.cefriel.template.TemplateExecutor;
import com.cefriel.template.TemplateMap;
import com.cefriel.template.io.Formatter;
import com.cefriel.template.io.Reader;
import com.cefriel.template.io.rdf.RDFReader;
import com.cefriel.template.utils.TemplateFunctions;
import com.cefriel.template.utils.Util;
import com.cefriel.util.*;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class MaptTemplateProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MaptTemplateProcessor.class);
    private record OperationParams (ChimeraResourceBean template,
                                    ChimeraResourceBean query,
                                    String formatterFormat,
                                    String basePath,
                                    String outputFileName,
                                    boolean trimTemplate,
                                    boolean verboseReader,
                                    ChimeraResourceBean templateMapKV,
                                    ChimeraResourceBean templateMapKVCsv,
                                    String baseIRI,
                                    boolean isStream,
                                    TemplateFunctions templateFunctions) {} // todo add other options which are not currently handled (time, ts adress maybe others)
    private static OperationParams getOperationParams(Exchange exchange, MaptTemplateBean operationConfig) {
        String baseIri = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
        return new OperationParams(
                operationConfig.getTemplate(),
                operationConfig.getQuery(),
                operationConfig.getFormat(),
                Utils.trailingSlash(operationConfig.getBasePath()),
                operationConfig.getFilename(),
                operationConfig.isTrimTemplate(),
                operationConfig.isVerboseQueries(),
                operationConfig.getKeyValuePairs(),
                operationConfig.getKeyValuePairsCSV(),
                baseIri == null ? ChimeraConstants.DEFAULT_BASE_IRI : baseIri,
                operationConfig.isStream(),
                operationConfig.getTemplateFunctions());
    }

    private static boolean validateParams(OperationParams params) {
        // only allow file resources
        return true;
    }
    public static void execute(Exchange exchange, MaptTemplateBean operationConfig, String inputFormat) throws Exception {
        OperationParams params = getOperationParams(exchange, operationConfig);
        execute(params, exchange, inputFormat);
    }
    private static void execute(OperationParams params, Exchange exchange, String inputFormat) throws Exception {

        if (validateParams(params)) {
            TemplateExecutor templateExecutor = new TemplateExecutor();
            Reader reader = getReaderFromExchange(exchange, inputFormat, params.verboseReader());

            TemplateFunctions templateFunctions = params.templateFunctions();
            if (templateFunctions != null)
                templateFunctions = templateFunctions.getClass().getDeclaredConstructor().newInstance();
            else
                templateFunctions = new TemplateFunctions();

            templateFunctions.setPrefix(params.baseIRI());

            TemplateMap templateMap = null;
            if(params.templateMapKV() != null) {
                templateMap = new TemplateMap(ResourceAccessor.open(params.templateMapKV(), exchange.getContext()),
                        false);
            }
            if(params.templateMapKVCsv() != null) {
                templateMap = new TemplateMap(ResourceAccessor.open(params.templateMapKVCsv(), exchange.getContext()),
                        true);
            }

            Formatter formatter = null;
            if(params.formatterFormat() != null){
                formatter = Util.createFormatter(params.formatterFormat());
            }

            // if filename is specified then save to file
            if(params.outputFileName() != null) {
                new File(params.basePath()).mkdirs();
                String outputFilePath = params.basePath() + params.outputFileName();
                String templatePath = FileResourceAccessor.getFilePath(params.template());
                if(params.query() != null) {
                    String queryPath = FileResourceAccessor.getFilePath(params.query());
                    List<String> resultFilesPaths =
                            templateExecutor.executeMappingParametric(reader, templatePath, false,
                                    params.trimTemplate(), queryPath, outputFilePath, templateMap, formatter, templateFunctions);
                    exchange.getMessage().setBody(resultFilesPaths, List.class);
                } else {
                    String resultFilePath =
                            templateExecutor.executeMapping(reader, templatePath, false, params.trimTemplate(), outputFilePath, templateMap, formatter, templateFunctions);
                    exchange.getMessage().setBody(resultFilePath, String.class);
                }
            }
            // if filename is not specified then the result of applying template is stored in the exchange body as string
            else {
                String templatePath = FileResourceAccessor.getFilePath(params.template());
                if (params.query() != null) {
                    String queryPath = FileResourceAccessor.getFilePath(params.query());
                    Map<String,String> result = templateExecutor.executeMappingParametric(reader, templatePath, false, params.trimTemplate(), queryPath, templateMap, formatter, templateFunctions);
                    exchange.getMessage().setBody(result, Map.class);

                } else {
                    String result = templateExecutor.executeMapping(reader, templatePath, false, params.trimTemplate(), templateMap, formatter, templateFunctions);
                    exchange.getMessage().setBody(result, String.class);
                }
            }
        }
    }
    /*
    private static TemplateExecutor configureTemplateOptions(OperationParams params, CamelContext context, Reader reader, Exchange exchange) throws Exception {
        TemplateUtils templateUtils = new TemplateUtils();
        templateUtils.setPrefix(params.baseIRI());

        new File(params.basePath()).mkdirs();
        String filepath = params.basePath() + params.outputFileName();
        exchange.getMessage().setHeader("filepath", filepath);

        return templateExec;
    }

     */
    private static Reader getReaderFromExchange(Exchange exchange, String inputFormat, boolean verbose) throws Exception {
        if (inputFormat == null) {
            // case when no reader is specified as input but are declared directly in the template file
            return null;
        }
        else if (inputFormat.equals("rdf")) {

            return configureRDFReader(exchange, verbose);
        }
        else {
            String input = exchange.getMessage().getBody(String.class);
            return Util.createNonRdfReaderFromInput(input, inputFormat, verbose);
        }
    }

    private static RDFReader configureRDFReader(Exchange exchange, boolean verbose) throws Exception {
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);

        if(graph != null) {
            String graphName = graph.getNamedGraph() != null ? graph.getNamedGraph().toString() : null;
            String baseIri = graph.getBaseIRI().toString();

            return Util.createRDFReader(graphName, baseIri, graph.getRepository(), verbose);
        }

        else {
            RDFReader reader = new RDFReader();
            RDFFormat format = Utils.getExchangeRdfFormat(exchange, Exchange.CONTENT_TYPE);
            reader.addString(exchange.getMessage().getBody(String.class), format);
            return reader;
        }
    }

    private static String handleOutputFileName(String outputFileName) {
        return Objects.requireNonNullElseGet(outputFileName, () -> "mapt-" + UUID.randomUUID() + ".txt");
    }
}