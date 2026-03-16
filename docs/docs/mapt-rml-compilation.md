# RML Compilation

The `rml` parameter allows you to provide an [RML](https://rml.io/specs/rml/) (RDF Mapping Language) mapping file instead of a Velocity template. At runtime, the RML mapping is compiled into an equivalent MTL (Mapping Template Language) template and then executed normally.

This is a feature of the **Mapping Template component** — it does not require the separate `camel-chimera-rmlmapper` module.

## How It Works

1. The RML mapping file is read from the `rml` [ChimeraResource](chimera-resources.md).
2. The mapping is compiled to an MTL `.vm` template file, written to the `basePath` directory.
3. The compiled template is executed against the readers in the exchange body.

## Constraints

- `rml` and `template` are **mutually exclusive** — you cannot set both.
- Typically used with the [`mapt://readers`](mapt-input-formats.md#multiple-readers) input format, since RML mappings often reference multiple logical sources.

## Parameters

All [Mapping Template parameters](mapt-component.md#parameters) apply. The key parameter is:

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `rml` | `ChimeraResourceBean` | Yes | — | Resource pointing to an RML mapping file (typically Turtle). See [ChimeraResource](chimera-resources.md). |

## Example

Compile and execute an RML mapping against two CSV readers:

=== "Java DSL"

    ```java
    ChimeraResourceBean rmlMapping = new ChimeraResourceBean(
        "file://./data/mapping.ttl", "rml");
    getCamelContext().getRegistry().bind("rmlMapping", rmlMapping);

    from("direct:reader1")
        .setVariable("readerFormat", constant("csv"))
        .setVariable("readerName", constant("reader1"))
        .to("direct:aggregate");

    from("direct:reader2")
        .setVariable("readerFormat", constant("csv"))
        .setVariable("readerName", constant("reader2"))
        .to("direct:aggregate");

    from("direct:aggregate")
        .aggregate(constant(true), new ReadersAggregation())
        .completionSize(2)
        .to("mapt://readers?rml=#bean:rmlMapping&basePath=./output");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: rmlMapping
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/mapping.ttl"
            serializationFormat: "rml"

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
                      rml: "#rmlMapping"
                      basePath: "./output"
    ```

The compiled MTL template is written to the `basePath` directory (e.g., `./output/template.rml.vm`).



