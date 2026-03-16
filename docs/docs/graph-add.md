# graph://add

Adds RDF triples from a [ChimeraResource](chimera-resources.md) to the RDFGraph in the exchange body. The triples are inserted into the graph's current named graph context (as configured on the graph via [`graph://get`](graph-get.md)). The updated graph remains in the exchange body, so you can chain multiple `add` operations to load data from several sources.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `chimeraResource` | `ChimeraResourceBean` | Yes | — | Resource pointing to the RDF data to add. Supports any RDF format. See [ChimeraResource](chimera-resources.md). |

## Example

=== "Java DSL"

    ```java
    ChimeraResourceBean data = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");
    getCamelContext().getRegistry().bind("data", data);

    from("graph://get")
        .to("graph://add?chimeraResource=#bean:data");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: data
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/input.ttl"
            serializationFormat: "turtle"

    - route:
        from:
          uri: "graph://get"
        steps:
          - to:
              uri: "graph://add"
              parameters:
                chimeraResource: "#data"
    ```
