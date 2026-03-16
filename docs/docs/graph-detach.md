# graph://detach

Cleans up the RDFGraph in the exchange body. This operation is typically placed at the end of a processing pipeline to release resources. It can clear all content from the repository's named graphs, shut down the repository connection, and/or stop the entire Camel route.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `clear` | `boolean` | No | `false` | When `true`, removes all content from the named graphs in the RDF4J repository. |
| `repoOff` | `boolean` | No | `true` | When `true`, shuts down the RDF4J repository connection and releases associated resources. |
| `routeOff` | `boolean` | No | `false` | When `true`, stops the Camel route and context after detaching. |

## Example

=== "Java DSL"

    ```java
    from("direct:cleanup")
        .to("graph://dump?dumpFormat=turtle&basePath=./output&filename=backup")
        .to("graph://detach?clear=true");
    ```

=== "YAML"

    ```yaml
    - route:
        from: "direct:cleanup"
        steps:
          - to:
              uri: "graph://dump"
              parameters:
                dumpFormat: "turtle"
                basePath: "./output"
                filename: "backup"
          - to:
              uri: "graph://detach"
              parameters:
                clear: true
    ```
