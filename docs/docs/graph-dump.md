# graph://dump

Serializes the RDFGraph in the exchange body to a specific RDF format. If both `basePath` and `filename` are provided, the serialized output is written to a file and the file path is placed in the exchange body. If they are omitted, the serialized RDF string is returned directly in the exchange body, which is useful for piping the output into subsequent processing steps.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `dumpFormat` | `String` | Yes | — | Output RDF serialization format: `turtle`, `rdfxml`, `ntriples`, `jsonld`, `n3`, `nquads`, `binary`, `trig`. |
| `basePath` | `String` | No | — | Output directory path. The directory is created automatically if it does not exist. |
| `filename` | `String` | No | — | Output file name (the appropriate extension is added automatically based on the format). Can also be set via the `ChimeraConstants.FILE_NAME` header. |

## Example

=== "Java DSL"

    ```java
    from("direct:dump")
        .to("graph://dump?dumpFormat=turtle&basePath=./output&filename=result");
    ```

=== "YAML"

    ```yaml
    - route:
        from: "direct:dump"
        steps:
          - to:
              uri: "graph://dump"
              parameters:
                dumpFormat: "turtle"
                basePath: "./output"
                filename: "result"
    ```
