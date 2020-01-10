package com.cefriel.chimera.processor.rdf4j.rml;

import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.functions.lib.GrelProcessor;
import be.ugent.rml.functions.lib.IDLabFunctions;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.RDF4JRemoteStore;
import be.ugent.rml.store.RDF4JStore;
import ch.qos.logback.classic.Level;
import com.cefriel.chimera.context.RDFGraph;
import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RMLConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(RMLConfigurator.class);

    static Executor configure(RDFGraph graph, String context, Map<String, InputStream> streams, String[] args) {
        Options options = new Options();
        Option mappingdocOption = Option.builder("m")
                .longOpt("mappingfile")
                .hasArg()
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("one or more mapping file paths and/or strings (multiple values are concatenated). " +
                        "r2rml is converted to rml if needed using the r2rml arguments.")
                .build();
        Option functionfileOption = Option.builder("f")
                .longOpt("functionfile")
                .hasArg()
                .desc("path to functions.ttl file (dynamic functions are found relative to functions.ttl)")
                .build();
        Option contextOption = Option.builder("ctx")
                .longOpt("contextIRI")
                .desc("Specify a context for querying the triple store.")
                .hasArg()
                .build();
        Option batchSizeOption = Option.builder("b")
                .longOpt("batchSize")
                .desc("Batch size, i.e., number of statements for each update to the triples store. " +
                        "If -inc is set it is used as batch size also for incremental updates.")
                .hasArg()
                .build();
        Option incrementalUpdateOption = Option.builder("inc")
                .longOpt("incrementalUpdate")
                .desc("Incremental update option to incrementally load triples in the database.")
                .build();
        Option noCacheOption = Option.builder("n")
                .longOpt("noCache")
                .desc("Do not use records and subject cache in the executor.")
                .build();
        Option orderedOption = Option.builder("ord")
                .longOpt("ordered")
                .desc("Mapping execution is ordered by logical source and caches are cleaned after each logical source." +
                        "This option improves memory consumption and it is advisable if no join condition exist among mappings.")
                .build();
        Option baseIRIOption = Option.builder("iri")
                .longOpt("baseIRI")
                .desc("Specify a base IRI for relative IRIs.")
                .hasArg()
                .build();
        Option baseIRIPrefixOption = Option.builder("pb")
                .longOpt("prefixBaseIRI")
                .desc("Specify a prefix for the base IRI used for relative IRIs.")
                .hasArg()
                .build();
        options.addOption(mappingdocOption);
        options.addOption(functionfileOption);
        options.addOption(contextOption);
        options.addOption(batchSizeOption);
        options.addOption(incrementalUpdateOption);
        options.addOption(noCacheOption);
        options.addOption(orderedOption);
        options.addOption(baseIRIOption);
        options.addOption(baseIRIPrefixOption);

        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            String basePath = System.getProperty("user.dir");
            CommandLine lineArgs = parser.parse(options, args);

            String[] mOptionValue = getOptionValues(mappingdocOption, lineArgs);

            // Concatenate all mapping files
            List<InputStream> lis = Arrays.stream(mOptionValue)
                    .map(Utils::getInputStreamFromMOptionValue)
                    .collect(Collectors.toList());
            InputStream is = new SequenceInputStream(Collections.enumeration(lis));

            // Read mapping file.
            RDF4JStore rmlStore = new RDF4JStore();
            rmlStore.read(is, null, RDFFormat.TURTLE);

            RecordsFactory factory = new RecordsFactory(basePath, streams);

            ValueFactory vf = SimpleValueFactory.getInstance();
            IRI contextIRI = vf.createIRI(context);

            RDF4JRemoteStore outputStore;
            if (graph.isRemote() && checkOptionPresence(batchSizeOption, lineArgs)) {
                    outputStore = new RDF4JRemoteStore(graph.getRepository(), contextIRI,
                            Integer.parseInt(getPriorityOptionValue(batchSizeOption, lineArgs)),
                            checkOptionPresence(incrementalUpdateOption, lineArgs));
            } else {
                // Covers both the cases: HTTPRepository not incremental / In-memory repository
                outputStore = new RDF4JRemoteStore(graph.getRepository(), contextIRI, 0, false);
            }

            String baseIRI;
            if (checkOptionPresence(baseIRIOption, lineArgs)) {
                baseIRI = getPriorityOptionValue(baseIRIOption, lineArgs);
                logger.debug("Base IRI set to value: " + lineArgs.getOptionValue("iri"));
            }
            else
                baseIRI = Utils.getBaseDirectiveTurtle(is);

            outputStore.copyNameSpaces(rmlStore);

            if (checkOptionPresence(baseIRIOption, lineArgs))
                if (checkOptionPresence(baseIRIPrefixOption, lineArgs))
                    outputStore.addNamespace(getPriorityOptionValue(baseIRIPrefixOption, lineArgs), baseIRI);
                else
                    outputStore.addNamespace("base", baseIRI);

            Executor executor;

            String fOptionValue = getPriorityOptionValue(functionfileOption, lineArgs);
            FunctionLoader functionLoader;

            Map<String, Class> libraryMap = new HashMap<>();
            libraryMap.put("GrelFunctions", GrelProcessor.class);
            libraryMap.put("IDLabFunctions", IDLabFunctions.class);

            if (fOptionValue == null) {
                functionLoader = new FunctionLoader(null, null, libraryMap);
            } else {
                functionLoader = new FunctionLoader(Utils.getFile(fOptionValue), null, libraryMap);
            }

            // We have to get the InputStreams of the RML documents again,
            // because we can only use an InputStream once.
            lis = Arrays.stream(mOptionValue)
                    .map(Utils::getInputStreamFromMOptionValue)
                    .collect(Collectors.toList());
            is = new SequenceInputStream(Collections.enumeration(lis));

            executor = new Executor(rmlStore, factory, functionLoader, outputStore, baseIRI);

            if (checkOptionPresence(noCacheOption, lineArgs))
                executor.setNoCache(true);
            if (checkOptionPresence(orderedOption, lineArgs))
                executor.setOrdered(true);

            return executor;

        } catch (ParseException exp) {
            // oops, something went wrong
            logger.error("Parsing failed. Reason: " + exp.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private static boolean checkOptionPresence(Option option, CommandLine lineArgs) {
        return lineArgs.hasOption(option.getOpt());
    }

    private static String getPriorityOptionValue(Option option, CommandLine lineArgs) {
        if (lineArgs.hasOption(option.getOpt())) {
            return lineArgs.getOptionValue(option.getOpt());
        } else {
            return null;
        }
    }

    private static String[] getOptionValues(Option option, CommandLine lineArgs) {
        if (lineArgs.hasOption(option.getOpt())) {
            return lineArgs.getOptionValues(option.getOpt());
        } else {
            return null;
        }
    }

    private static void setLoggerLevel(Level level) {
        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ((ch.qos.logback.classic.Logger) root).setLevel(level);
    }

}
