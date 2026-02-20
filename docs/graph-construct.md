# graph://construct

Executes a SPARQL CONSTRUCT query against the RDFGraph in the exchange body to create new triples based on patterns matched in the existing data. This is useful for data transformation, restructuring, and ontology alignment tasks.

The resulting triples can either be merged into the existing graph (the default) or placed into a brand-new graph that replaces the exchange body — controlled by the `newGraph` parameter.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | `String` | No* | — | Inline SPARQL CONSTRUCT query string. |
| `chimeraResource` | `ChimeraResourceBean` | No* | — | Resource pointing to a file containing the SPARQL CONSTRUCT query. See [ChimeraResource](chimera-resources.md). |
| `newGraph` | `boolean` | No | `false` | When `true`, the exchange body is replaced with a new in-memory graph containing only the constructed triples. When `false`, the constructed triples are added to the existing graph. |
| `namedGraph` | `String` | No | — | Named graph URI(s) where the constructed triples should be inserted. Multiple URIs can be separated by `;`. |

*At least one of `query` or `chimeraResource` must be provided.

## Example

=== "Java DSL"

    ```java
    ChimeraResourceBean constructQuery = new ChimeraResourceBean(
        "file://./queries/transform.rq", "txt");
    getCamelContext().getRegistry().bind("constructQuery", constructQuery);

    from("direct:construct")
        .to("graph://construct?chimeraResource=#bean:constructQuery&newGraph=true");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: constructQuery
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./queries/transform.rq"
            serializationFormat: "txt"

    - route:
        from: "direct:construct"
        steps:
          - to:
              uri: "graph://construct"
              parameters:
                chimeraResource: "#constructQuery"
                newGraph: true
    ```
