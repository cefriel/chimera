# Mapping Template Component

The Mapping Template component (`mapt://`) executes [Apache Velocity](https://velocity.apache.org/) templates against data read from various sources (RDF, XML, JSON, CSV, SQL, or multiple readers). It is powered by the [mapping-template](https://github.com/cefriel/mapping-template) library.

Typical uses:

- **Lowering**: RDF graph → structured data (CSV, XML, JSON, etc.) via `mapt://rdf`.
- **Lifting**: structured data → RDF via `mapt://xml`, `mapt://json`, `mapt://csv`, etc.
- **Any-to-any**: template-driven transformation between arbitrary formats.

## URI Format

```
mapt://{inputFormat}
```

The input format determines which type of reader is created from the exchange body. See [Input Formats](mapt-input-formats.md) for details on each format and its expected exchange body content.

| Input Format | Exchange Body Expected | Description |
|--------------|-----------------------|-------------|
| [`rdf`](mapt-input-formats.md#rdf) | `RDFGraph` | Read from an RDF graph (lowering). |
| [`xml`](mapt-input-formats.md#xml) | `String` (XML) | Read from XML. |
| [`json`](mapt-input-formats.md#json) | `String` (JSON) | Read from JSON. |
| [`csv`](mapt-input-formats.md#csv) | `String` (CSV) | Read from CSV. |
| [`sql`](mapt-input-formats.md#sql) | `JdbcConnectionDetails` | Read from a database via JDBC. |
| [`readers`](mapt-input-formats.md#multiple-readers) | `Map<String, Reader>` | Multiple heterogeneous readers. |
| [_(empty)_](mapt-input-formats.md#no-input) | _(any)_ | No reader — template uses only `templateMap` or `customFunctions`. |

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `template` | `ChimeraResourceBean` | No* | — | Velocity template resource. See [ChimeraResource](chimera-resources.md). |
| `rml` | `ChimeraResourceBean` | No* | — | RML mapping compiled to an equivalent MTL template at runtime. Mutually exclusive with `template`. See [RML Compilation](mapt-rml-compilation.md). |
| `basePath` | `String` | No | `./` | Output directory when writing to file. |
| `filename` | `String` | No | — | Output file name. When set, output is written to `{basePath}/{filename}`. When absent, output is returned in the exchange body as a `String`. |
| `query` | `ChimeraResourceBean` | No | — | SPARQL query for [parametric mappings](mapt-parametric.md). The template is executed once per result row, producing multiple outputs. |
| `format` | `String` | No | — | Output formatter for escaping. Values: `json`, `xml`, or any RDF format supported by RDF4J (e.g., `turtle`, `rdfxml`, `ntriples`, `jsonld`, `n3`, `nquads`, `trig`). |
| `trimTemplate` | `boolean` | No | `false` | Trim leading/trailing whitespace from the template output. |
| `verboseQueries` | `boolean` | No | `false` | Enable verbose logging for reader queries. Useful for debugging SPARQL queries within `mapt://rdf` templates. |
| `stream` | `boolean` | No | `false` | Enable streaming mode for large outputs. |
| `fir` | `boolean` | No | `true` | Fail on invalid reference. When `true`, the mapping fails if the template references a variable that does not exist. Set to `false` to silently ignore missing references. |
| `templateMap` | `TemplateMap` | No | — | A map of key-value pairs accessible in the template as variables. See [No Input](mapt-input-formats.md#no-input). |
| `keyValuePairs` | `ChimeraResourceBean` | No | — | Resource pointing to a properties file used to build a `TemplateMap`. See [ChimeraResource](chimera-resources.md). |
| `keyValuePairsCSV` | `ChimeraResourceBean` | No | — | Resource pointing to a CSV file used to build a `TemplateMap`. See [ChimeraResource](chimera-resources.md). |
| `customFunctions` | `TemplateFunctions` | No | — | In-project class extending `TemplateFunctions`. See [Custom Functions](mapt-custom-functions.md). |
| `resourceCustomFunctions` | `ChimeraResourceBean` | No | — | External `.java` file compiled at runtime. Mutually exclusive with `customFunctions`. See [Custom Functions](mapt-custom-functions.md). |

*At least one of `template` or `rml` must be provided. They are mutually exclusive.

## Output Behavior

The combination of `filename` and `query` determines what is placed in the exchange body after execution:

| `filename` set? | `query` set? | Exchange Body |
|-----------------|--------------|---------------|
| No | No | `String` — the template output. |
| No | Yes | `Map<String, String>` — key per query result row → template output. |
| Yes | No | `String` — the output file path. |
| Yes | Yes | `List<Path>` — paths to all generated files. |

