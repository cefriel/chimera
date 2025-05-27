package com.cefriel.aggregationStrategy;

import com.cefriel.template.io.Reader;
import com.cefriel.template.io.csv.CSVReader;
import com.cefriel.template.io.json.JSONReader;
import com.cefriel.template.io.rdf.RDFReader;
import com.cefriel.template.io.sql.SQLReader;
import com.cefriel.template.io.xml.XMLReader;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReadersAggregation implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        try {
            Map<String, Reader> readers = new HashMap<>();
            if (oldExchange != null) {
                Object body = oldExchange.getMessage().getBody();
                if (body instanceof Map<?, ?> map) {
                    map.forEach((k, v) -> {
                        if (k instanceof String key && v instanceof Reader value) {
                            readers.put(key, value);
                        }
                    });
                }
            }
            readers.putAll(getReaders(newExchange));
            Exchange result = (oldExchange != null) ? oldExchange : newExchange;
            result.getMessage().setBody(readers);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Reader> getReaders(Exchange exchange) throws Exception {
        String readerContent = exchange.getMessage().getBody(String.class);
        String readerFormat = exchange.getVariable("readerFormat", String.class);
        String readerName = exchange.getVariable("readerName", String.class);

        Reader reader = switch (readerFormat) {
            case "json" -> new JSONReader(readerContent);
            case "sql" -> new SQLReader(
                    exchange.getVariable("jdbcDSN", String.class),
                    exchange.getVariable("username", String.class),
                    exchange.getVariable("password", String.class)
            );
            case "xml" -> new XMLReader(readerContent);
            case "csv" -> new CSVReader(readerContent);
            case "rdf" -> {
                String sparqlEndpoint = exchange.getVariable("sparqlEndpoint", String.class);
                if (sparqlEndpoint != null) {
                    yield new RDFReader(new SPARQLRepository(sparqlEndpoint));
                }
                RDFReader rdfReader = new RDFReader();
                String rmlPath = exchange.getVariable("readerInputFile", String.class);
                Optional<RDFFormat> rdfFormat = Rio.getParserFormatForFileName(rmlPath);
                if (rdfFormat.isPresent()) {
                    rdfReader.addString(readerContent, rdfFormat.get());
                }
                yield rdfReader;
            }
            default -> throw new InvalidParameterException("Cannot create Reader for format: " + readerFormat);
        };

        return Map.of(readerName, reader);
    }
}