# ChimeraResource

A `ChimeraResourceBean` is a uniform way to reference external data (RDF files, SPARQL queries, ontologies, templates, etc.) from any Chimera operation. It wraps a URL with a serialization format and optional authentication.

## Properties

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `url` | `String` | Yes | Resource location with a prefix indicating the source type (see below). |
| `serializationFormat` | `String` | Yes | Content format (e.g., `turtle`, `rdfxml`, `txt`, `csv`). |
| `authToken` | `String` | No | Bearer token for HTTP resources. |
| `username` | `String` | No | Username for HTTP basic auth. |
| `password` | `String` | No | Password for HTTP basic auth. |
| `authMethod` | `String` | No | Authentication method (e.g., `basic`). |

## Resource Types

| Prefix | Source |
|--------|--------|
| `file://` | Local filesystem path |
| `http://` / `https://` | Remote HTTP endpoint |
| `classpath://` | Java classpath |
| `header://` | Camel message header |
| `property://` | Camel exchange property |
| `variable://` | Camel exchange variable |

## Serialization Formats

| Format | Extensions | Usage |
|--------|-----------|-------|
| `turtle` | `.ttl` | RDF data |
| `rdfxml` | `.rdf`, `.owl` | RDF data |
| `ntriples` | `.nt` | RDF data |
| `jsonld` | `.jsonld` | RDF data |
| `n3` | `.n3` | RDF data |
| `nquads` | `.nq` | RDF data |
| `trig` | `.trig` | RDF data |
| `txt` | `.txt`, `.rq` | SPARQL queries, plain text |

## Example

=== "Java DSL"

    ```java
    // File resource
    ChimeraResourceBean triples = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");

    // HTTP resource with bearer token
    ChimeraResourceBean remote = new ChimeraResourceBean(
        "https://example.org/data.ttl", "turtle", "my-token");

    // Classpath resource
    ChimeraResourceBean ontology = new ChimeraResourceBean(
        "classpath://ontologies/schema.owl", "rdfxml");

    // Dynamic resource from a Camel header
    ChimeraResourceBean dynamic = new ChimeraResourceBean(
        "header://sparqlQuery", "txt");

    // Register beans
    getCamelContext().getRegistry().bind("triples", triples);
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: triples
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/input.ttl"
            serializationFormat: "turtle"
        - name: remote
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "https://example.org/data.ttl"
            serializationFormat: "turtle"
            authToken: "my-token"
        - name: ontology
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "classpath://ontologies/schema.owl"
            serializationFormat: "rdfxml"
    ```

Beans are referenced in endpoint URIs with `#bean:name` (Java DSL) or `#name` (YAML).
