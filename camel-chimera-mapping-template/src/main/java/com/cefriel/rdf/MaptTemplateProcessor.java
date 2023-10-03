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
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                                    ChimeraResourceBean templateFunctions) {}
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

            TemplateFunctions usedTemplateFunctions;

            if (params.templateFunctions() != null) {
                InputStream x = ResourceAccessor.open(params.templateFunctions(),exchange);
                Path tempFile = Files.createFile(Path.of("CustomFunctions.java"));
                FileUtils.copyInputStreamToFile(x, tempFile.toFile());
                usedTemplateFunctions = getCustomTemplateFunctions(tempFile.toAbsolutePath().toString());
                Files.deleteIfExists(tempFile);
            }

            else {
                usedTemplateFunctions = new TemplateFunctions();
                usedTemplateFunctions.setPrefix(params.baseIRI());
            }


            TemplateMap templateMap = null;
            if(params.templateMapKV() != null) {
                templateMap = new TemplateMap(ResourceAccessor.open(params.templateMapKV(), exchange),
                        false);
            }
            if(params.templateMapKVCsv() != null) {
                templateMap = new TemplateMap(ResourceAccessor.open(params.templateMapKVCsv(), exchange),
                        true);
            }

            Formatter formatter = null;
            if(params.formatterFormat() != null){
                formatter = Util.createFormatter(params.formatterFormat());
            }

            // if filename is specified then save to file
            if(params.outputFileName() != null) {
                new File(params.basePath()).mkdirs();
                Path outputFilePath = Paths.get(params.basePath() + params.outputFileName());
                if(params.query() != null) {
                    List<Path> resultFilesPaths =
                            templateExecutor.executeMappingParametric(reader, ResourceAccessor.open(params.template(), exchange),
                                    ResourceAccessor.open(params.query(), exchange), outputFilePath, templateMap, formatter, usedTemplateFunctions);
                    exchange.getMessage().setBody(resultFilesPaths, List.class);
                } else {
                    Path resultFilePath =
                            templateExecutor.executeMapping(reader, ResourceAccessor.open(params.template(), exchange), outputFilePath, templateMap, formatter, usedTemplateFunctions);
                    exchange.getMessage().setBody(resultFilePath, String.class);
                }
            }
            // if filename is not specified then the result of applying template is stored in the exchange body as string
            else {
                if (params.query() != null) {
                    Map<String,String> result = templateExecutor.executeMappingParametric(reader, ResourceAccessor.open(params.template(), exchange), ResourceAccessor.open(params.query(), exchange), templateMap, formatter, usedTemplateFunctions);
                    exchange.getMessage().setBody(result, Map.class);

                } else {
                    String result = templateExecutor.executeMapping(reader, ResourceAccessor.open(params.template(), exchange), templateMap, formatter, usedTemplateFunctions);
                    exchange.getMessage().setBody(result, String.class);
                }
            }
        }
    }
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

    private static TemplateFunctions getCustomTemplateFunctions(String functionsPath) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileNotFoundException {
        if (functionsPath != null) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            File utilsFile = new File(functionsPath);
            compiler.run(null, null, null, utilsFile.getPath());

            File classDir = new File(utilsFile.getParent());
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{(classDir).toURI().toURL()});

            // List all the files in the directory and identify the class file
            File[] classFiles = classDir.listFiles((dir, name) -> name.endsWith(".class"));
            if (classFiles != null) {
                for (File classFile : classFiles) {
                    String className = classFile.getName().replace(".class", "");
                    Class<?> loadedClass = Class.forName(className, true, classLoader);

                    if ((TemplateFunctions.class).isAssignableFrom(loadedClass)) {
                        return (TemplateFunctions) loadedClass.getDeclaredConstructor().newInstance();
                    }
                }
            }
        }
        throw new FileNotFoundException("File: " + functionsPath + " not found");
    }
}