# graph://ask

Executes a SPARQL ASK query against the RDFGraph in the exchange body. The result is a boolean — `true` if the graph contains at least one match for the query pattern, `false` otherwise. This is useful for validation checks and conditional routing.

The query can be provided either inline as a string or loaded from an external file via a [ChimeraResource](chimera-resources.md).

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `query` | `String` | No* | — | Inline SPARQL ASK query string. |
| `chimeraResource` | `ChimeraResourceBean` | No* | — | Resource pointing to a file containing the SPARQL ASK query. See [ChimeraResource](chimera-resources.md). |

*At least one of `query` or `chimeraResource` must be provided.

## Example

=== "Java DSL"

    ```java
    from("direct:check")
        .setVariable("q", constant("ASK WHERE { ?s a <http://example.org/Person> }"))
        .toD("graph://ask?query=${variable.q}")
        .choice()
            .when(body().isEqualTo(true))
                .to("direct:found")
            .otherwise()
                .to("direct:notFound");
    ```

=== "YAML"

    ```yaml
    - route:
        from: "direct:check"
        steps:
          - setVariable:
              name: q
              constant: "ASK WHERE { ?s a <http://example.org/Person> }"
          - toD: "graph://ask?query=${variable.q}"
          - choice:
              when:
                - simple: "${body} == true"
                  steps:
                    - to: "direct:found"
              otherwise:
                steps:
                  - to: "direct:notFound"
    ```
