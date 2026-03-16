# Input Formats

The input format in the `mapt://` URI determines the type of reader that is created from the exchange body. All [Mapping Template parameters](mapt-component.md#parameters) apply regardless of the input format chosen. The output behavior is always determined by the `filename` and `query` parameters — see [Output Behavior](mapt-component.md#output-behavior).

---

## RDF

**URI**: `mapt://rdf`

Executes a Velocity template against the RDFGraph in the exchange body using an `RDFReader`. This is the primary **lowering** operation — extracting data from a knowledge graph into a structured format (CSV, XML, JSON, etc.) defined by the template. Inside the template, SPARQL queries are used (via the `RDFReader` API from the [mapping-template](https://github.com/cefriel/mapping-template) library) to select and iterate over the data to be lowered.

**Exchange body (input)**: `RDFGraph` — typically produced by [`graph://get`](graph-get.md) followed by [`graph://add`](graph-add.md). If the exchange body is a `String` instead of an `RDFGraph`, the component creates an `RDFReader` from the string content, using the `Content-Type` header to determine the RDF format.

**Notable parameters**: `verboseQueries` is especially useful with this input format — when set to `true`, it logs the SPARQL queries executed by the reader, which helps with debugging templates.

### Example

Load an RDF graph and lower it to a CSV file:

=== "Java DSL"

    ```java
    ChimeraResourceBean triples = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("triples", triples);
    getCamelContext().getRegistry().bind("template", template);

    from("graph://get")
        .to("graph://add?chimeraResource=#bean:triples")
        .to("mapt://rdf?template=#bean:template&basePath=./output&filename=result.csv");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: triples
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/input.ttl"
            serializationFormat: "turtle"
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from:
          uri: "graph://get"
        steps:
          - to:
              uri: "graph://add"
              parameters:
                chimeraResource: "#triples"
          - to:
              uri: "mapt://rdf"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.csv"
    ```

---

## XML

**URI**: `mapt://xml`

Executes a Velocity template against an XML document in the exchange body using an `XMLReader`. This is typically used for **lifting** — transforming XML data into RDF by applying a template that produces Turtle, N-Triples, or another RDF format.

**Exchange body (input)**: `String` containing the XML content. You can load the XML string from a file using Camel's [File component](https://camel.apache.org/components/latest/file-component.html), an HTTP endpoint, or any other source that produces a string.

### Example

=== "Java DSL"

    ```java
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("template", template);

    from("direct:start")
        .to("mapt://xml?template=#bean:template&basePath=./output&filename=result.ttl");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://xml"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.ttl"
    ```

---

## JSON

**URI**: `mapt://json`

Executes a Velocity template against a JSON document in the exchange body using a `JSONReader`. Like `mapt://xml`, this is commonly used for lifting JSON data into RDF.

**Exchange body (input)**: `String` containing the JSON content.

### Example

=== "Java DSL"

    ```java
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("template", template);

    from("direct:start")
        .to("mapt://json?template=#bean:template&basePath=./output&filename=result.ttl");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://json"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.ttl"
    ```

---

## CSV

**URI**: `mapt://csv`

Executes a Velocity template against CSV data in the exchange body using a `CSVReader`. This is commonly used for lifting tabular data into RDF.

**Exchange body (input)**: `String` containing the CSV content (with a header row).

### Example

=== "Java DSL"

    ```java
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("template", template);

    from("direct:start")
        .to("mapt://csv?template=#bean:template&basePath=./output&filename=result.ttl");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://csv"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.ttl"
    ```

---

## SQL

**URI**: `mapt://sql`

Executes a Velocity template against a relational database using a `SQLReader` over JDBC. The SQL queries used to extract data are defined inside the Velocity template itself (via the `SQLReader` API).

**Exchange body (input)**: a `JdbcConnectionDetails` object containing the JDBC URL, username, and password:

```java
public class JdbcConnectionDetails {
    public final String jdbcUrl;
    public final String username;
    public final String password;
}
```

!!! note "JDBC Driver"
    The appropriate JDBC driver must be on the classpath (e.g., `org.postgresql:postgresql` for PostgreSQL, `com.mysql:mysql-connector-j` for MySQL).

### Example

=== "Java DSL"

    ```java
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("template", template);

    from("direct:start")
        .setBody(constant(new JdbcConnectionDetails(
            "jdbc:postgresql://localhost:5432/mydb", "user", "pass")))
        .to("mapt://sql?template=#bean:template&basePath=./output&filename=result.csv");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://sql"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.csv"
    ```

In the YAML example, the `JdbcConnectionDetails` must be set as the exchange body before the `mapt://sql` step — for instance by an upstream producer or a bean method call.

---

## Multiple Readers

**URI**: `mapt://readers`

Executes a Velocity template against multiple heterogeneous data readers simultaneously. This allows a single template to access data from different sources — for example, one CSV file and one JSON file, or two different databases. Each reader is referenced by name inside the template.

**Exchange body (input)**: `Map<String, Reader>` — a map where each key is the name used to reference that reader in the template, and each value is a `Reader` instance (`CSVReader`, `JSONReader`, `XMLReader`, `RDFReader`, or `SQLReader`).

This input format also supports the `rml` parameter as an alternative to `template` — see [RML Compilation](mapt-rml-compilation.md).

### Building the Reader Map

You can build the `Map<String, Reader>` in two ways:

**1. Manually in Java:**

```java
Map<String, Reader> readers = Map.of(
    "csvData", new CSVReader(csvString),
    "jsonData", new JSONReader(jsonString));
```

**2. Using ReadersAggregation:**

The [`ReadersAggregation`](aggregation-strategies.md#readersaggregation) strategy accumulates readers from multiple routes into a single map. Each contributing route must set two exchange variables:

- `readerFormat` — one of `csv`, `json`, `xml`, `rdf`, `sql`.
- `readerName` — the key for this reader in the resulting map.

### Example

Using `ReadersAggregation` to combine two CSV readers:

=== "Java DSL"

    ```java
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("template", template);

    from("direct:reader1")
        .setVariable("readerFormat", constant("csv"))
        .setVariable("readerName", constant("reader1"))
        .setBody(constant("a,b,c\n1,2,3\n"))
        .to("direct:aggregate");

    from("direct:reader2")
        .setVariable("readerFormat", constant("csv"))
        .setVariable("readerName", constant("reader2"))
        .setBody(constant("d,e,f\n4,5,6\n"))
        .to("direct:aggregate");

    from("direct:aggregate")
        .aggregate(constant(true), new ReadersAggregation())
        .completionSize(2)
        .to("mapt://readers?template=#bean:template");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        id: reader1
        from: "direct:reader1"
        steps:
          - setVariable:
              name: readerFormat
              constant: "csv"
          - setVariable:
              name: readerName
              constant: "reader1"
          - setBody:
              constant: "a,b,c\n1,2,3\n"
          - to: "direct:aggregate"

    - route:
        id: reader2
        from: "direct:reader2"
        steps:
          - setVariable:
              name: readerFormat
              constant: "csv"
          - setVariable:
              name: readerName
              constant: "reader2"
          - setBody:
              constant: "d,e,f\n4,5,6\n"
          - to: "direct:aggregate"

    - route:
        id: aggregate
        from: "direct:aggregate"
        steps:
          - aggregate:
              constant: true
              aggregationStrategy: "#class:com.cefriel.aggregationStrategy.ReadersAggregation"
              completionSize: 2
              steps:
                - to:
                    uri: "mapt://readers"
                    parameters:
                      template: "#template"
    ```

---

## No Input

**URI**: `mapt://` _(empty input format)_

When the input format is empty, no reader is created from the exchange body. The template is executed with access only to the `templateMap`, `customFunctions`, and any key-value pairs — but without a data reader. This is useful for templates that generate output from static configuration or key-value lookups rather than from a data source.

**Exchange body (input)**: ignored (no reader is created).

The template variables are typically supplied via one of:

- `templateMap` — a `TemplateMap` bean registered in the Camel context.
- `keyValuePairs` — a [ChimeraResource](chimera-resources.md) pointing to a `.properties` file.
- `keyValuePairsCSV` — a [ChimeraResource](chimera-resources.md) pointing to a CSV file of key-value pairs.

### Example

Using a `TemplateMap` to provide variables to a template:

=== "Java DSL"

    ```java
    TemplateMap templateMap = new TemplateMap(
        Map.of("key1", "1", "key2", "2"));
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("templateMap", templateMap);
    getCamelContext().getRegistry().bind("template", template);

    from("direct:start")
        .to("mapt://?template=#bean:template&templateMap=#bean:templateMap");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""
        - name: templateMap
          type: com.cefriel.template.TemplateMap
          properties:
            map:
              key1: "1"
              key2: "2"

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://"
              parameters:
                template: "#template"
                templateMap: "#templateMap"
    ```

A template like `$map.get("key1"),$map.get("key2")` would produce `1,2`.

