# graph://get

Creates an RDFGraph and places it in the exchange body. The graph type is selected automatically based on the parameters provided (see [RDF Graphs — Graph Types](rdf-graphs.md#graph-types)).

## Behavior

- **As consumer** (`from("graph://get?...")`): creates an empty RDFGraph and starts the route. This is the typical way to begin a graph-processing pipeline — the empty graph is then populated by downstream operations like [`graph://add`](graph-add.md).
- **As producer** (`to("graph://get?...")`): creates an RDFGraph. If the exchange body contains an `InputStream`, the RDF data is parsed and loaded into the graph using the `rdfFormat` parameter. This is useful when receiving RDF data from an upstream Camel component (e.g., a file consumer or HTTP endpoint).

## Parameters

### Common

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `namedGraph` | `String` | No | Auto-generated | Named graph URI(s), separated by `;`. When multiple are provided, all are used as read contexts but only the first is used for inserts. |
| `baseIri` | `String` | No | `http://www.cefriel.com/data/` | Base IRI for relative URI resolution and named graph generation. |
| `defaultGraph` | `boolean` | No | `false` | When `true`, operations target the default (unnamed) graph instead of an auto-generated named graph. |
| `rdfFormat` | `String` | No | — | RDF format for parsing an InputStream body (producer mode only). Values: `turtle`, `rdfxml`, `ntriples`, `jsonld`, `n3`, `nquads`, `binary`, `rdfa`. |

### Graph-Type Specific

These parameters determine which type of RDFGraph is created. The first matching condition in priority order wins (see [RDF Graphs — Graph Types](rdf-graphs.md#graph-types)).

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `chimeraResource` | `ChimeraResourceBean` | No | — | Ontology/schema for RDFS inference. When set, creates an InferenceRDFGraph. See [ChimeraResource](chimera-resources.md). |
| `allRules` | `boolean` | No | `true` | Controls RDFS inference rules (InferenceRDFGraph only). `true`: all RDFS rules. `false`: conservative subset for better performance. |
| `ontologyFormat` | `String` | No | — | RDF format of the ontology resource (InferenceRDFGraph only). |
| `serverUrl` | `String` | No | — | Base URL of the RDF4J Server. When set together with `repositoryID`, creates an HTTPRDFGraph. |
| `repositoryID` | `String` | No | — | Repository name on the RDF4J Server. Used together with `serverUrl`. |
| `sparqlEndpoint` | `String` | No | — | SPARQL endpoint URL. When set, creates a SPARQLEndpointGraph. |
| `pathDataDir` | `String` | No | — | Directory for persistent on-disk storage. When set alone, creates a NativeRDFGraph. When set with `chimeraResource`, provides persistent backing for InferenceRDFGraph. |

## Example

=== "Java DSL"

    ```java
    ChimeraResourceBean triples = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");
    getCamelContext().getRegistry().bind("triples", triples);

    // Consumer: empty in-memory graph
    from("graph://get")
        .to("graph://add?chimeraResource=#bean:triples")
        .to("graph://dump?dumpFormat=turtle");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: triples
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
                chimeraResource: "#triples"
          - to:
              uri: "graph://dump"
              parameters:
                dumpFormat: "turtle"
    ```
