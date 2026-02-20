# graph://inference

Applies RDFS inference rules to the RDFGraph in the exchange body, deriving new triples based on the existing data and an optional ontology schema. If an ontology is provided via `chimeraResource`, its class and property hierarchies drive the inference — for example, if the ontology defines `ex:Employee rdfs:subClassOf ex:Person`, then every instance of `ex:Employee` will also be inferred to be an instance of `ex:Person`. Without an ontology, basic RDFS inference still runs using the relationships already present in the graph data.

The result is a new InferenceRDFGraph that replaces the exchange body. All triples from the original graph are preserved; the inferred triples are added on top.

## Parameters

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `chimeraResource` | `ChimeraResourceBean` | No | — | Ontology/schema resource whose class and property hierarchies are used for inference. See [ChimeraResource](chimera-resources.md). |
| `allRules` | `boolean` | No | `true` | When `true`, the full set of RDFS entailment rules is applied (including domain/range inference, which can significantly increase the number of inferred triples). When `false`, a more conservative subset is used, focusing on subclass and subproperty reasoning, which offers better performance. |

## Example

=== "Java DSL"

    ```java
    ChimeraResourceBean ontology = new ChimeraResourceBean(
        "file://./ontologies/schema.owl", "rdfxml");
    getCamelContext().getRegistry().bind("ontology", ontology);

    from("direct:inference")
        .to("graph://inference?chimeraResource=#bean:ontology&allRules=false");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: ontology
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./ontologies/schema.owl"
            serializationFormat: "rdfxml"

    - route:
        from: "direct:inference"
        steps:
          - to:
              uri: "graph://inference"
              parameters:
                chimeraResource: "#ontology"
                allRules: false
    ```
