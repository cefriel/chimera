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
import com.cefriel.template.io.csv.CSVReader;
import com.cefriel.template.io.json.JSONReader;
import com.cefriel.template.io.rdf.RDFReader;
import com.cefriel.template.io.xml.XMLReader;
import com.cefriel.template.utils.TemplateFunctions;
import com.cefriel.template.utils.Util;
import com.cefriel.util.ChimeraConstants;
import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.ResourceAccessor;
import com.cefriel.util.Utils;
import org.apache.camel.Exchange;
import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
                                    ChimeraResourceBean resourceCustomFunctions,
                                    TemplateFunctions customFunctions) {}
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
                operationConfig.getResourceCustomFunctions(),
                operationConfig.getCustomFunctions());
    }

    private static boolean validateParams(OperationParams params) {
        // only allow file resources (why this comment????)
        return true;
    }
    public static void execute(Exchange exchange, MaptTemplateBean operationConfig, String inputFormat) throws Exception {
        OperationParams params = getOperationParams(exchange, operationConfig);
        execute(params, exchange, inputFormat);
    }
    private static void execute(OperationParams params, Exchange exchange, String inputFormat) throws Exception {

        if (validateParams(params)) {

            // which custom template functions to use ???
            // the one that is defined, what if both are defined?
            // throw a warning

            TemplateFunctions usedTemplateFunctions;

            if ((params.resourceCustomFunctions() != null) && (params.customFunctions() != null)) {
                throw new InvalidParameterException("custom templateFunctions be passed either using resourceCustomFunctions or customFunction");
            }

            else if ((params.resourceCustomFunctions() != null) && (params.customFunctions == null)) {
                InputStream x = ResourceAccessor.open(params.resourceCustomFunctions(),exchange);
                Path tempFile = Files.createFile(Path.of("CustomFunctions.java"));
                FileUtils.copyInputStreamToFile(x, tempFile.toFile());
                usedTemplateFunctions = getCustomTemplateFunctions(tempFile.toAbsolutePath().toString());
                Files.deleteIfExists(tempFile);
            }

            else if ((params.resourceCustomFunctions() == null) && (params.customFunctions != null)) {
                usedTemplateFunctions = params.customFunctions();
                usedTemplateFunctions.setPrefix(params.baseIRI());
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

            TemplateExecutor templateExecutor;
            if (inputFormat != null && inputFormat.equals("readers")) {
                Map<String, Reader> readers = exchange.getMessage().getBody(Map.class);
                templateExecutor = new TemplateExecutor(readers, usedTemplateFunctions, true, params.trimTemplate(), false, templateMap, formatter);
            }
            else {
                Reader reader = getReaderFromExchange(exchange, inputFormat, params.formatterFormat(), params.verboseReader());
                templateExecutor = new TemplateExecutor(reader, usedTemplateFunctions, true, params.trimTemplate(), false, templateMap, formatter);
            }

            // if filename is specified then save to file
            if(params.outputFileName() != null) {
                new File(params.basePath()).mkdirs();
                Path outputFilePath = Paths.get(params.basePath() + params.outputFileName());
                if(params.query() != null) {
                    List<Path> resultFilesPaths =
                            templateExecutor.executeMappingParametric(ResourceAccessor.open(params.template(), exchange),
                                    ResourceAccessor.open(params.query(), exchange), outputFilePath);
                    exchange.getMessage().setBody(resultFilesPaths, List.class);
                } else {
                    Path resultFilePath =
                            templateExecutor.executeMapping(ResourceAccessor.open(params.template(), exchange), outputFilePath);
                    exchange.getMessage().setBody(resultFilePath, String.class);
                }
            }
            // if filename is not specified then the result of applying template is stored in the exchange body as string
            else {
                if (params.query() != null) {
                    Map<String,String> result = templateExecutor.executeMappingParametric(ResourceAccessor.open(params.template(), exchange), ResourceAccessor.open(params.query(), exchange));
                    exchange.getMessage().setBody(result, Map.class);

                } else {
                    String result = templateExecutor.executeMapping(ResourceAccessor.open(params.template(), exchange));
                    exchange.getMessage().setBody(result, String.class);
                }
            }
        }
    }

    private static Reader getReaderFromExchange(Exchange exchange, String inputFormat, String outputFormat, boolean verbose) throws Exception {
        if (inputFormat == null) {
            // case when no reader is specified as input but are declared directly in the template file
            return null;
        }

        Reader reader = switch (inputFormat) {
            case "rdf" -> configureRDFReader(exchange, verbose);
            case "json" -> new JSONReader(exchange.getMessage().getBody(String.class));
            case "xml" -> new XMLReader(exchange.getMessage().getBody(String.class));
            case "csv" -> new CSVReader(exchange.getMessage().getBody(String.class));
            default -> throw new InvalidParameterException("Cannot create Reader for inputFormat: " + inputFormat);
        };

        reader.setOutputFormat(outputFormat);
        return reader;
    }

    private static RDFReader configureRDFReader(Exchange exchange, boolean verbose) throws Exception {
        RDFGraph graph = exchange.getMessage().getBody(RDFGraph.class);

        if(graph != null) {
            RDFReader rdfReader = new RDFReader(graph.getRepository());
            rdfReader.setVerbose(verbose);
            return rdfReader;
        }

        else {
            RDFReader reader = new RDFReader();
            reader.setVerbose(verbose);
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
