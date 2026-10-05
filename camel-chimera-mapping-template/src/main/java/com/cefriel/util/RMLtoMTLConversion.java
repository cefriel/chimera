package com.cefriel.util;

import com.cefriel.template.TemplateExecutor;
import com.cefriel.template.TemplateMap;
import com.cefriel.template.io.Reader;
import com.cefriel.template.io.csv.CSVReader;
import com.cefriel.template.io.json.JSONReader;
import com.cefriel.template.io.rdf.RDFReader;
import com.cefriel.template.io.sql.SQLReader;
import com.cefriel.template.io.xml.XMLReader;
import com.cefriel.template.utils.RMLCompilerUtils;
import com.cefriel.template.utils.TemplateFunctions;
import com.cefriel.template.utils.Util;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.cefriel.template.utils.Util.inputStreamToString;

public class RMLtoMTLConversion {

    public record CompiledMTLResult(Map<String, Reader> readers, Path mappingMTL) {}

    /**
     * Compiles an RML mapping into an MTL (Velocity) mapping.
     * <p>
     * The RML mapping is first validated against the RML SHACL shapes, then augmented with a stable
     * {@code rml:reader} identifier per logical source, and finally compiled with the bundled RML compiler template.
     *
     * @param mappingRML stream containing the RML mapping in Turtle syntax. Must not be null. The stream is fully
     *                   read but not closed; closing it remains the caller's responsibility.
     * @param baseIri    base IRI to use when the mapping does not declare a {@code @base}. Can be null.
     * @param basePath   base path used both to resolve the mapping's relative sources and to write the compiled
     *                   template. Must not be null.
     * @param verbose    enable verbose validation output.
     * @return a {@link CompiledMTLResult} holding the readers for the compiled mapping and its path.
     * @throws Exception if validation or compilation fails.
     */
    public static CompiledMTLResult compileMTLMapping(InputStream mappingRML, String baseIri, Path basePath, boolean verbose) throws Exception {
        Objects.requireNonNull(mappingRML, "mappingRML must not be null");
        Objects.requireNonNull(basePath, "basePath must not be null");

        String mappingRMLString = inputStreamToString(mappingRML);
        byte[] mappingRMLBytes = mappingRMLString.getBytes(StandardCharsets.UTF_8);
        Util.validateRML(new ByteArrayInputStream(mappingRMLBytes), verbose);

        // The @base declaration is read from the original mapping: it is a syntactic Turtle construct and is lost
        // when the augmented model is re-serialised.
        RMLCompilerUtils rmlCompilerUtils = new RMLCompilerUtils();
        String declaredBaseIri = rmlCompilerUtils.getBaseIRI(mappingRMLString);
        String effectiveBaseIri = declaredBaseIri != null ? declaredBaseIri : baseIri;

        String augmentedTurtle;
        Repository repository = new SailRepository(new MemoryStore());
        repository.init();
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(new ByteArrayInputStream(mappingRMLBytes), "", RDFFormat.TURTLE);
            addReaderIds(connection);

            Model augmentedModel = QueryResults.asModel(connection.getStatements(null, null, null));
            StringWriter writer = new StringWriter();
            Rio.write(augmentedModel, writer, RDFFormat.TURTLE);
            augmentedTurtle = writer.toString();
        } finally {
            repository.shutDown();
        }

        // The reader used to compile the RML mapping into an MTL mapping. It is local to this method and is disposed
        // of before returning; it must not be confused with the readers used to execute the compiled mapping.
        Reader compilerReader = TemplateFunctions.getRDFReaderFromString(augmentedTurtle, RDFFormat.TURTLE.toString());
        try {
            String compilerResource = "/rml/chimeraRMLCompiler.vm";
            Path mappingMTL;
            try (InputStream rmlCompiler = Util.class.getResourceAsStream(compilerResource)) {
                if (rmlCompiler == null)
                    throw new IllegalStateException("RML compiler template not found on the classpath: " + compilerResource);

                Map<String, String> rmlMap = new HashMap<>();
                rmlMap.put("baseIRI", effectiveBaseIri);
                rmlMap.put("basePath", toDirectoryPrefix(basePath));

                Path compiledTemplatePath = basePath.resolve("template.rml.vm");
                TemplateExecutor templateExecutor = new TemplateExecutor(false, false, true, null);
                mappingMTL = templateExecutor.executeMapping(
                        Map.of("reader", compilerReader),
                        rmlCompiler,
                        compiledTemplatePath,
                        rmlCompilerUtils,
                        new TemplateMap(rmlMap)
                );
            }

            Map<String, Reader> readers = buildDataReaders(augmentedTurtle, basePath);
            return new CompiledMTLResult(readers, mappingMTL);
        } finally {
            compilerReader.shutDown();
        }
    }

    /**
     * Query extracting, for every logical source, the reader identifier together with everything needed to open the
     * underlying data: the source description, its reference formulation and, when present, the database connection
     * details.
     */
    private static final String SOURCES_QUERY = """
            PREFIX rml: <http://w3id.org/rml/>
            PREFIX dcat: <http://www.w3.org/ns/dcat#>
            PREFIX d2rq: <http://www.wiwiss.fu-berlin.de/suhl/bizer/D2RQ/0.1#>

            SELECT DISTINCT *
            WHERE {
              ?triples_map_id rml:logicalSource ?_source .
              ?_source rml:reader ?reader_id .
              OPTIONAL {
                ?_source rml:source ?data_source .
                OPTIONAL { ?data_source dcat:accessURL ?access_url . }
                OPTIONAL { ?data_source rml:root ?root_folder . }
                OPTIONAL { ?data_source rml:path ?path . }
                OPTIONAL { ?data_source rml:compression ?compression . }
                OPTIONAL {
                  ?data_source d2rq:jdbcDSN ?jdbcDSN ;
                               d2rq:username ?user ;
                               d2rq:password ?password .
                }
              }
              OPTIONAL { ?_source rml:referenceFormulation ?ref_form . }
            }
            """;

    private static final Map<String, String> REFERENCE_FORMULATIONS = Map.of(
            "http://w3id.org/rml/JSONPath", "json",
            "http://w3id.org/rml/XPath", "xml",
            "http://w3id.org/rml/CSV", "csv",
            "http://w3id.org/rml/SQL2008Table", "sql",
            "http://w3id.org/rml/SQL2008TableName", "sql",
            "http://w3id.org/rml/SQL2008Query", "sql",
            "http://www.w3.org/ns/formats/SPARQL_Results_CSV", "rdf");

    /**
     * Builds the readers that give access to the data the compiled MTL mapping operates on.
     * <p>
     * One reader is created per {@code rml:reader} identifier: logical sources that share the same source definition
     * and reference formulation therefore share a single reader. The keys of the returned map are the names the
     * compiled mapping uses to look readers up, i.e. {@code literalHash(reader_id)}.
     *
     * @param augmentedRML the RML mapping, in Turtle syntax, already augmented with {@code rml:reader} identifiers by
     *                     {@link #addReaderIds(RepositoryConnection)}. Must not be null.
     * @param basePath     directory relative source paths are resolved against. Must not be null.
     * @return the readers, keyed by the name used in the compiled mapping. The caller owns them and is responsible for
     * calling {@link Reader#shutDown()} on each of them.
     * @throws Exception if a source cannot be read or no reader exists for its reference formulation.
     */
    public static Map<String, Reader> buildDataReaders(String augmentedRML, Path basePath) throws Exception {
        Objects.requireNonNull(augmentedRML, "augmentedRML must not be null");
        Objects.requireNonNull(basePath, "basePath must not be null");

        // The results are materialised before any reader is built: opening a source can be slow and the query result
        // must not stay open while the repository is being used for something else.
        List<Map<String, String>> sources = new ArrayList<>();
        Set<String> seenReaderIds = new HashSet<>();
        Repository repository = new SailRepository(new MemoryStore());
        repository.init();
        try (RepositoryConnection connection = repository.getConnection()) {
            connection.add(new ByteArrayInputStream(augmentedRML.getBytes(StandardCharsets.UTF_8)), "", RDFFormat.TURTLE);
            try (TupleQueryResult result = connection.prepareTupleQuery(SOURCES_QUERY).evaluate()) {
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    Value readerId = bindingSet.getValue("reader_id");
                    if (readerId == null || !seenReaderIds.add(readerId.stringValue()))
                        continue;

                    Map<String, String> source = new HashMap<>();
                    for (String name : bindingSet.getBindingNames()) {
                        Value value = bindingSet.getValue(name);
                        if (value != null)
                            source.put(name, value.stringValue());
                    }
                    sources.add(source);
                }
            }
        } finally {
            repository.shutDown();
        }

        Map<String, Reader> readers = new HashMap<>();
        try {
            for (Map<String, String> source : sources)
                readers.put(TemplateFunctions.literalHash(source.get("reader_id")), buildDataReader(source, basePath));
        } catch (Exception e) {
            readers.values().forEach(Reader::shutDown);
            throw e;
        }
        return readers;
    }

    /**
     * Instantiates the reader matching the reference formulation of a single logical source.
     */
    private static Reader buildDataReader(Map<String, String> source, Path basePath) throws Exception {
        String referenceFormulation = source.get("ref_form");
        String format = referenceFormulation == null ? null : REFERENCE_FORMULATIONS.get(referenceFormulation);
        if (format == null)
            throw new IllegalArgumentException("No Reader available for reference formulation: " + referenceFormulation);

        if (format.equals("sql")) {
            String jdbcDSN = source.get("jdbcDSN");
            if (jdbcDSN == null)
                throw new IllegalArgumentException("Missing d2rq:jdbcDSN for source: " + source.get("data_source"));
            return new SQLReader(jdbcDSN, source.get("user"), source.get("password"));
        }

        String content = readSourceContent(source, basePath);
        return switch (format) {
            case "csv" -> new CSVReader(content);
            case "json" -> new JSONReader(content);
            case "xml" -> new XMLReader(content);
            case "rdf" -> {
                RDFReader rdfReader = new RDFReader();
                String path = source.get("path");
                RDFFormat rdfFormat = path == null
                        ? RDFFormat.TURTLE
                        : Rio.getParserFormatForFileName(path).orElse(RDFFormat.TURTLE);
                rdfReader.addString(content, rdfFormat);
                yield rdfReader;
            }
            default -> throw new IllegalArgumentException("No Reader available for format: " + format);
        };
    }

    /**
     * Reads an uncompressed source as UTF-8 text. Compressed sources are not supported.
     */
    private static String readSourceContent(Map<String, String> source, Path basePath) throws Exception {
        String compression = source.get("compression");
        if (compression != null)
            throw new IllegalArgumentException("Compressed sources are not supported, found rml:compression: " + compression);

        String path = source.get("path");
        String accessUrl = source.get("access_url");

        byte[] raw;
        if (path != null) {
            raw = Files.readAllBytes(resolveSourcePath(path, source.get("root_folder"), basePath));
        } else if (accessUrl != null) {
            try (InputStream inputStream = URI.create(accessUrl).toURL().openStream()) {
                raw = inputStream.readAllBytes();
            }
        } else {
            throw new IllegalArgumentException("Source has neither rml:path nor dcat:accessURL: " + source.get("data_source"));
        }

        return new String(raw, StandardCharsets.UTF_8);
    }

    /**
     * Resolves the path of a source against the directory of the mapping. Relative paths are always resolved against
     * {@code basePath}: {@code rml:MappingDirectory} is the only root currently supported, and sources without a root
     * are interpreted the same way.
     */
    private static Path resolveSourcePath(String path, String rootFolder, Path basePath) {
        if (rootFolder != null
                && !rootFolder.equals("http://w3id.org/rml/MappingDirectory")
                && !rootFolder.equals("http://w3id.org/rml/CurrentWorkingDirectory"))
            throw new IllegalArgumentException("Unsupported rml:root: " + rootFolder);

        Path sourcePath = Path.of(path);
        return sourcePath.isAbsolute() ? sourcePath : basePath.resolve(sourcePath);
    }

    /**
     * Renders a directory path as a slash-terminated prefix that templates can concatenate with relative source paths.
     */
    private static String toDirectoryPrefix(Path directory) {
        String prefix = directory.toString().replace('\\', '/');
        if (prefix.isEmpty())
            return "./";
        return prefix.endsWith("/") ? prefix : prefix + "/";
    }

    /**
     * Augments every logical source in the given connection with an {@code rml:reader} identifier derived from the
     * properties of its source. Logical sources sharing the same source definition and reference formulation get the
     * same identifier, so that a single reader can be instantiated for them.
     *
     * @param connection connection to the repository holding the RML mapping. Must not be null.
     */
    public static void addReaderIds(RepositoryConnection connection) {
        Objects.requireNonNull(connection, "connection must not be null");

        // The details string is fed to a hash, so the concatenation must be deterministic: without the ORDER BY the
        // same mapping could yield different reader ids across runs.
        String propertiesForSource = """
                PREFIX rml: <http://w3id.org/rml/>

                SELECT ?logicalSource (GROUP_CONCAT(?detail; separator=", ") AS ?details)
                WHERE {
                  SELECT ?logicalSource ?detail
                  WHERE {
                    ?triplesMap a rml:TriplesMap ;
                                rml:logicalSource ?logicalSource .

                    ?logicalSource rml:source ?source ;
                                   rml:referenceFormulation ?ref .

                    ?source ?predicate ?object .

                    BIND(CONCAT("rml:referenceFormulation=", STR(?ref), ", ", STR(?predicate), "=", STR(?object)) AS ?detail)
                  }
                  ORDER BY ?logicalSource ?detail
                }
                GROUP BY ?logicalSource
                """;

        ValueFactory vf = SimpleValueFactory.getInstance();
        TupleQuery query = connection.prepareTupleQuery(propertiesForSource);

        // The statements are collected before being written: adding to the connection while the result is still open
        // would mutate the data the query is iterating over.
        List<Statement> readerStatements = new ArrayList<>();
        try (TupleQueryResult result = query.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value logicalSource = bindingSet.getValue("logicalSource");
                Value details = bindingSet.getValue("details");
                if (!(logicalSource instanceof Resource logicalSourceResource) || details == null)
                    continue;

                readerStatements.add(vf.createStatement(
                        logicalSourceResource,
                        vf.createIRI("http://w3id.org/rml/reader"),
                        vf.createLiteral(TemplateFunctions.literalHash(details.stringValue()))));
            }
        }
        connection.add(readerStatements);
    }
}