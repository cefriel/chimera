# Parametric Mappings

When the `query` parameter is set, the mapping template executes in **parametric mode**: a SPARQL query is run against the data, and the template is executed once per result row, producing multiple outputs.

This is useful when you need to generate one output file per entity (e.g., one CSV per agency, one XML per record).

## How It Works

1. The SPARQL query in `query` is executed against the reader.
2. For each result row, the bindings are made available as variables in the template.
3. The template is executed once per row.

## Output Behavior

| `filename` set? | Exchange Body |
|-----------------|---------------|
| Yes | `List<Path>` — one file per result row. File names are derived from the query bindings appended to the base `filename`. |
| No | `Map<String, String>` — key per result row → template output string. |

## Parameters

All [Mapping Template parameters](mapt-component.md#parameters) apply. The key additional parameter is:

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | `ChimeraResourceBean` | Yes | — | Resource pointing to the SPARQL query file. Each result row triggers one template execution. See [ChimeraResource](chimera-resources.md). |

## Example

Generate one CSV file per agency from an RDF graph:

=== "Java DSL"

    ```java
    ChimeraResourceBean triples = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    ChimeraResourceBean query = new ChimeraResourceBean(
        "file://./data/query.txt", "");
    getCamelContext().getRegistry().bind("triples", triples);
    getCamelContext().getRegistry().bind("template", template);
    getCamelContext().getRegistry().bind("query", query);

    from("graph://get")
        .to("graph://add?chimeraResource=#bean:triples")
        .to("mapt://rdf?template=#bean:template&query=#bean:query&basePath=./output&filename=agency.csv");
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
        - name: query
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/query.txt"
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
                query: "#query"
                basePath: "./output"
                filename: "agency.csv"
    ```

If the query returns two rows (e.g., for agencies "BEST-AGENCY" and "WOW-AGENCY"), two files are created: `agency-BEST-AGENCY.csv` and `agency-WOW-AGENCY.csv`. The exchange body will contain a `List<Path>` pointing to both files.


