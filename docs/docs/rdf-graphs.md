# RDF Graphs

Chimera wraps [Eclipse RDF4J](https://rdf4j.org/) repositories as `RDFGraph` objects. An `RDFGraph` is the primary data container flowing through a Camel route — most graph operations expect one in the exchange body.

## Graph Types

The [`graph://get`](graph-get.md) operation selects the graph type automatically based on the endpoint parameters you provide, following this priority:

| Priority | Type | Condition |
|----------|------|-----------|
| 1 | **InferenceRDFGraph** | `chimeraResource` is set (pointing to an ontology) |
| 2 | **HTTPRDFGraph** | Both `serverUrl` and `repositoryID` are set |
| 3 | **SPARQLEndpointGraph** | `sparqlEndpoint` is set |
| 4 | **NativeRDFGraph** | `pathDataDir` is set |
| 5 | **MemoryRDFGraph** | Default — no storage parameters |

### MemoryRDFGraph

In-memory store using RDF4J's `MemoryStore`. Data exists only for the lifetime of the exchange. This is the default.

### NativeRDFGraph

Persistent on-disk store using RDF4J's `NativeStore`. Requires `pathDataDir` — the directory where index and data files are written. Do not share the same directory across concurrent processes.

### HTTPRDFGraph

Connects to a remote RDF4J Server via HTTP. Requires `serverUrl` (server base URL) and `repositoryID` (the repository name on that server). The repository must already exist.

### SPARQLEndpointGraph

Connects to any standard SPARQL 1.1 endpoint. Requires `sparqlEndpoint` (the query URL). Use this for generic triplestores like Virtuoso, Fuseki, or public endpoints.

### InferenceRDFGraph

Wraps an in-memory or native store with RDFS inference using RDF4J's `SchemaCachingRDFSInferencer`. Requires a `chimeraResource` pointing to an ontology file. Optionally set `pathDataDir` for persistent storage and `allRules` to control the inference rule set.

## Common Parameters

These parameters are available on all graph types and are set on the [`graph://get`](graph-get.md) endpoint.

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `namedGraph` | `String` | No | Auto-generated | One or more named graph URIs separated by `;`. All are used as read contexts; only the first is used for inserts. |
| `baseIri` | `String` | No | `http://www.cefriel.com/data/` | Base IRI for resolving relative URIs and auto-generating named graph URIs. |
| `defaultGraph` | `boolean` | No | `false` | When `true`, operations target the default (unnamed) graph instead of a named graph. |

When `defaultGraph=false` and no `namedGraph` is specified, Chimera auto-generates a named graph URI using the pattern `{baseIri}{exchangeId}` (or `{baseIri}{graph_id}` if the `graph_id` header is set).
