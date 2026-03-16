# Chimera

Chimera is a framework built on [Apache Camel](https://camel.apache.org/) for defining semantic data transformation pipelines. It provides Camel components for creating, manipulating, and querying RDF knowledge graphs, and for executing template-based data transformations powered by the [mapping-template](https://github.com/cefriel/mapping-template) library.

## Components

| Component | Artifact | Description |
|-----------|----------|-------------|
| **Graph** (`graph://`) | `camel-chimera-graph` | Create and manipulate RDF graphs: load triples, run SPARQL queries, apply inference, validate with SHACL, and serialize output. |
| **Mapping Template** (`mapt://`) | `camel-chimera-mapping-template` | Execute [Apache Velocity](https://velocity.apache.org/) templates against data from RDF graphs, XML, JSON, CSV, or SQL sources. Supports both lifting (structured data → RDF) and lowering (RDF → structured data). |

!!! note
    `camel-chimera-mapping-template` includes `camel-chimera-graph` as a transitive dependency. You do **not** need to add both.

## Maven Coordinates

```xml
<!-- Graph component only -->
<dependency>
    <groupId>com.cefriel</groupId>
    <artifactId>camel-chimera-graph</artifactId>
    <version>4.6.1-SNAPSHOT</version>
</dependency>

<!-- Mapping Template component (includes Graph) -->
<dependency>
    <groupId>com.cefriel</groupId>
    <artifactId>camel-chimera-mapping-template</artifactId>
    <version>4.6.1-SNAPSHOT</version>
</dependency>
```

## Next Steps

- New to Apache Camel? Start with [Getting Started](getting-started.md).
- Already familiar? Jump to the [Graph Component](graph-component.md) or [Mapping Template Component](mapt-component.md).

## License

Apache License 2.0
