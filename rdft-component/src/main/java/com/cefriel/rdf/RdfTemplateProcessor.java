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
import com.cefriel.template.io.rdf.RDFFormatter;
import com.cefriel.template.io.xml.XMLFormatter;
import com.cefriel.template.utils.TemplateUtils;
import com.cefriel.util.*;
import com.cefriel.util.ChimeraConstants;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.rio.RDFFormat;
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

    public static void execute(Exchange exchange, RdfTemplateBean configuration, Reader reader) throws Exception {

        TemplateExecutor tl = configureTemplateOptions(exchange, configuration, reader);

        //Execute rdft stream processing
        if (configuration.isStream()) {
            logger.info("Template processed as a stream");
            InputStream template = exchange.getProperty(RdfTemplateConstants.TEMPLATE_STREAM, InputStream.class);
            if (template == null)
                template = UniLoader.open(configuration.getTemplatePath());
            if (configuration.getQueryFilePath() != null)
                logger.warn("Parametric templates not supported for streams");
            if (template != null) {
                String result = tl.lower(template);
                exchange.getMessage().setBody(result, String.class);
            }
        } else {
            String filepath = exchange.getMessage().getHeader("filepath", String.class);
            //Execute rdft file-processing
            tl.lower(configuration.getTemplatePath(), filepath, configuration.getQueryFilePath());
            //Return list of files generated if parametric execution
            if (configuration.getQueryFilePath() != null) {
                String filename = configuration.getFilename().replaceFirst("[.][^.]+$", "");
                List<String> result;
                try (Stream<Path> walk = Files.walk(Paths.get(configuration.getBasePath()))) {
                    result = walk.map(x -> x.getFileName().toString())
                            .filter(f -> f.contains(filename))
                            .collect(Collectors.toList());
                }
                List<String> outputs = new ArrayList<>();
                for (String f : result)
                    outputs.add(configuration.getBasePath() + f);
                exchange.getMessage().setBody(outputs, List.class);
            }
            //Return name of file generated
            else {
                exchange.getMessage().setBody(filepath);
            }
        }
    }

    private static void configureTemplateLowerer(Exchange exchange, TemplateExecutor templateExecutor, RdfTemplateBean configuration) throws IOException {

        TemplateMap templateMap = new TemplateMap();

        if (exchange.getMessage().getHeader(RdfTemplateConstants.KEY_VALUE_CSV) != null) {
            templateMap.parseMap(exchange.getMessage().getHeader(RdfTemplateConstants.KEY_VALUE_CSV, InputStream.class), true);
        } else if (configuration.getKeyValueCsvPath() != null)
            templateMap.parseMap(UniLoader.open(configuration.getKeyValueCsvPath()), true);

        if (exchange.getMessage().getHeader(RdfTemplateConstants.KEY_VALUE_PAIRS) != null) {
            templateMap.parseMap(exchange.getMessage().getHeader(RdfTemplateConstants.KEY_VALUE_PAIRS, InputStream.class), false);
        } else if (configuration.getKeyValuePairsPath() != null)
            templateMap.parseMap(UniLoader.open(configuration.getKeyValuePairsPath()), false);
        if(!templateMap.isEmpty())
            logger.info("Map parsed containing " + templateMap.size() + " keys");
        templateExecutor.setMap(templateMap);


        if (configuration.getFormat() != null) {
            if (configuration.getFormat().equals("xml"))
                templateExecutor.setFormatter(new XMLFormatter());
            else {
                Utils.setConfigurationRDFHeader(exchange, configuration.getFormat());
                RDFFormat rdfFormat = Utils.getExchangeRdfFormat(exchange, ChimeraConstants.ACCEPTFORMAT);
                templateExecutor.setFormatter(new RDFFormatter(rdfFormat));
            }
        }
        if (configuration.isTrimTemplate())
            templateExecutor.setTrimTemplate(true);
        if(!configuration.isStream()) {
            if (configuration.getTemplatePath().startsWith("classpath:")) {
                configuration.setTemplatePath(configuration.getTemplatePath().replaceAll("classpath://", ""));
                templateExecutor.setResourceTemplate(true);
            } else if (configuration.getTemplatePath().startsWith("file:")) {
                configuration.setTemplatePath(configuration.getTemplatePath().replaceAll("file://", ""));
            } else {
                configuration.setStream(true);
            }
        }
    }

    private static TemplateExecutor configureTemplateOptions(Exchange exchange, RdfTemplateBean configuration, Reader reader) throws Exception {

        String filepath;
        TemplateUtils lu = configuration.getUtils();
        if (lu != null)
            lu = lu.getClass().getDeclaredConstructor().newInstance();
        else
            lu = new TemplateUtils();

        String baseIRI = exchange.getMessage().getHeader(ChimeraConstants.BASE_IRI, String.class);
        if (baseIRI == null)
            baseIRI = ChimeraConstants.DEFAULT_BASE_IRI;
        lu.setPrefix(baseIRI);

        if (configuration.isVerboseQueries())
            reader.setVerbose(true);
        TemplateExecutor tl = new TemplateExecutor(reader, lu);

        String format = exchange.getMessage().getHeader(Exchange.CONTENT_TYPE, String.class);
        if (format != null)
            configuration.setFormat(format);

        configureTemplateLowerer(exchange, tl, configuration);

        String templatePath = exchange.getMessage().getHeader(RdfTemplateConstants.TEMPLATE_PATH, String.class);
        if (templatePath != null)
            configuration.setTemplatePath(templatePath);

        String destFileName = exchange.getMessage().getHeader(ChimeraConstants.FILENAME, String.class);
        if (destFileName == null)
            destFileName = configuration.getFilename();
        if (destFileName == null) {
            String uuid = UUID.randomUUID().toString();
            destFileName = "rdft-" + uuid + ".txt";
        }
        configuration.setFilename(destFileName);

        String localDestPath = Utils.trailingSlash(configuration.getBasePath());
        configuration.setBasePath(localDestPath);
        new File(localDestPath).mkdirs();
        filepath = localDestPath + destFileName;
        exchange.getMessage().setHeader("filepath", filepath);

        return tl;

    }

}

