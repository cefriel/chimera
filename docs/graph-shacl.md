# graph://shacl

Validates the RDFGraph in the exchange body against [SHACL](https://www.w3.org/TR/shacl/) (Shapes Constraint Language) shape definitions. The operation returns a SHACL validation report as an RDF Model in the exchange body. The report indicates whether the data conforms to all shapes and, if not, provides details about each constraint violation including the focus node, property path, and the specific constraint that was violated.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `chimeraResource` | `ChimeraResourceBean` | Yes | — | Resource pointing to the SHACL shapes file used for validation. See [ChimeraResource](chimera-resources.md). |

## Example

=== "Java DSL"

    ```java
    ChimeraResourceBean shapes = new ChimeraResourceBean(
        "file://./shapes/person.ttl", "turtle");
    getCamelContext().getRegistry().bind("shapes", shapes);

    from("direct:validate")
        .to("graph://shacl?chimeraResource=#bean:shapes");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: shapes
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./shapes/person.ttl"
            serializationFormat: "turtle"

    - route:
        from: "direct:validate"
        steps:
          - to:
              uri: "graph://shacl"
              parameters:
                chimeraResource: "#shapes"
    ```
