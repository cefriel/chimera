# Graph Component

The Graph component (`graph://`) provides operations for creating and manipulating RDF graphs within Apache Camel routes.

## URI Format

```
graph://{operation}
```

## Operations

| Operation | Description |
|-----------|-------------|
| [`get`](graph-get.md) | Create an RDFGraph (consumer or producer). |
| [`add`](graph-add.md) | Add RDF triples from a [ChimeraResource](chimera-resources.md). |
| [`construct`](graph-construct.md) | Execute a SPARQL CONSTRUCT query. |
| [`select`](graph-sparql-select.md) | Execute a SPARQL SELECT query. |
| [`ask`](graph-sparql-ask.md) | Execute a SPARQL ASK query. |
| [`dump`](graph-dump.md) | Serialize the graph to a file or string. |
| [`inference`](graph-inference.md) | Apply RDFS inference rules. |
| [`shacl`](graph-shacl.md) | Validate with SHACL shapes. |
| [`detach`](graph-detach.md) | Clean up and optionally stop the route. |

## Consumer vs Producer

When used as a **consumer** (`from("graph://get")`), the `get` operation creates an empty RDFGraph and starts the route. This is the only operation that can act as a consumer.

When used as a **producer** (`to("graph://...")`), each operation processes the RDFGraph already present in the exchange body. The `get` operation can additionally load RDF data from an `InputStream` in the body.

For more on Camel consumers and producers, see the [Apache Camel Endpoint documentation](https://camel.apache.org/manual/endpoint.html).

## Header Overrides

Some parameters can be overridden per-exchange via Camel message headers:

| Header | Constant | Overrides |
|--------|----------|-----------|
| `context_graph` | `ChimeraConstants.CONTEXT_GRAPH` | `namedGraph` |
| `base_iri` | `ChimeraConstants.BASE_IRI` | `baseIri` |
| `rdfFormat` | `ChimeraConstants.RDF_FORMAT` | `rdfFormat` |
| `graph_id` | `ChimeraConstants.GRAPH_ID` | Auto-generated named graph suffix |

