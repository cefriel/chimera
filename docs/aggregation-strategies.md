# Aggregation Strategies

Chimera provides [Camel Aggregation Strategies](https://camel.apache.org/manual/aggregator2.html) for combining data from multiple routes before processing. These are used with Camel's `aggregate`, `multicast`, or `split` EIPs.

## GraphAggregation

**Package**: `com.cefriel.component.GraphAggregation`

Merges two `RDFGraph` repositories. All triples from the new exchange's graph are copied into the old exchange's graph.

Use this when splitting or multicasting work across multiple graph operations and then recombining the results.

```java
from("direct:merge")
    .aggregate(constant(true), new GraphAggregation())
    .completionSize(2)
    .to("graph://dump?dumpFormat=turtle");
```

---

## ReadersAggregation

**Package**: `com.cefriel.aggregationStrategy.ReadersAggregation`

Accumulates a `Map<String, Reader>` from multiple routes. Each contributing exchange must set two exchange variables:

| Variable | Description |
|----------|-------------|
| `readerFormat` | Reader type: `csv`, `json`, `xml`, `rdf`, or `sql`. |
| `readerName` | Key for this reader in the resulting map. |

For `rdf` readers, an additional variable `readerInputFile` (file name with extension) is used to determine the RDF format. Alternatively, `sparqlEndpoint` can be set to create an RDF reader backed by a SPARQL endpoint.

For `sql` readers, the exchange body must be a `JdbcConnectionDetails` object, or the variables `jdbcDSN`, `username`, and `password` must be set.

The resulting `Map<String, Reader>` is used with [`mapt://readers`](mapt-input-formats.md#multiple-readers).

See [mapt://readers](mapt-input-formats.md#multiple-readers) for a full example.

---

## TemplateAggregation

**Package**: `com.cefriel.aggregationStrategy.TemplateAggregation`

Aggregates a template `InputStream` from a new exchange into the old exchange's property `MaptTemplateConstants.TEMPLATE_STREAM`. Use this when the Velocity template itself is produced dynamically by an upstream route.

---

## KeyValuePairsAggregation

**Package**: `com.cefriel.aggregationStrategy.KeyValuePairsAggregation`

Aggregates a key-value pairs `InputStream` into the old exchange's property `MaptTemplateConstants.KEY_VALUE_PAIRS`. Use this when the properties file for `keyValuePairs` is produced by an upstream route.

---

## KeyValueCSVAggregation

**Package**: `com.cefriel.aggregationStrategy.KeyValueCSVAggregation`

Aggregates a CSV key-value `InputStream` into the old exchange's property `MaptTemplateConstants.KEY_VALUE_CSV`. Use this when the CSV file for `keyValuePairsCSV` is produced by an upstream route.

---

## QueryFilePathAggregation

**Package**: `com.cefriel.aggregationStrategy.QueryFilePathAggregation`

Aggregates a query file `InputStream` into the old exchange's property `MaptTemplateConstants.QUERY_FILE`. Use this when the SPARQL query file for [parametric mappings](mapt-parametric.md) is produced by an upstream route.



