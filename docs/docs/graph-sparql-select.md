# graph://select

Executes a SPARQL SELECT query against the RDFGraph in the exchange body and returns the query results. By default, results are returned as an in-memory `List<BindingSet>` (RDF4J's native representation). You can also serialize the results to JSON, CSV, XML, or TSV by setting the `dumpFormat` parameter.

The query can be provided either inline as a string or loaded from an external file via a [ChimeraResource](chimera-resources.md).

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | `String` | No* | — | Inline SPARQL SELECT query string. |
| `chimeraResource` | `ChimeraResourceBean` | No* | — | Resource pointing to a file containing the SPARQL SELECT query. See [ChimeraResource](chimera-resources.md). |
| `dumpFormat` | `String` | No | `memory` | Output format for the query results. `memory`: returns `List<BindingSet>`. `json`, `csv`, `xml`, `tsv`: returns a serialized string. |

*At least one of `query` or `chimeraResource` must be provided.

## Example

=== "Java DSL"

    ```java
    from("direct:select")
        .setVariable("q", constant("SELECT ?s ?p ?o WHERE { ?s ?p ?o }"))
        .toD("graph://select?query=${variable.q}&dumpFormat=json");
    ```

=== "YAML"

    ```yaml
    - route:
        from: "direct:select"
        steps:
          - setVariable:
              name: q
              constant: "SELECT ?s ?p ?o WHERE { ?s ?p ?o }"
          - toD: "graph://select?query=${variable.q}&dumpFormat=json"
    ```
