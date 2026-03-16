# Getting Started

## Apache Camel in 60 Seconds

[Apache Camel](https://camel.apache.org/) is an integration framework that moves and transforms data using **routes**. A route has:

- A **consumer** (`from(...)`) — the starting point that creates messages.
- One or more **producers** (`to(...)`) — steps that process or send messages.
- An **exchange** — the envelope carrying a message body and headers through the route.
- **Beans** — objects registered in the Camel context and referenced by name (e.g., `#bean:myBean`).

Routes can be written in Java DSL or YAML DSL. For full details see the [Apache Camel documentation](https://camel.apache.org/manual/).

## A Minimal Pipeline

This example loads RDF triples into a graph and lowers them to a CSV file using a Velocity template.

=== "Java DSL"

    ```java
    ChimeraResourceBean triples = new ChimeraResourceBean(
        "file://./data/input.ttl", "turtle");
    ChimeraResourceBean template = new ChimeraResourceBean(
        "file://./data/template.vm", "");
    getCamelContext().getRegistry().bind("triples", triples);
    getCamelContext().getRegistry().bind("template", template);

    from("graph://get")
        .to("graph://add?chimeraResource=#bean:triples")
        .to("mapt://rdf?template=#bean:template&basePath=./output&filename=result.csv");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: triples
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/input.ttl"
            serializationFormat: "turtle"
        - name: template
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./data/template.vm"
            serializationFormat: ""

    - route:
        from:
          uri: "graph://get"
        steps:
          - to:
              uri: "graph://add"
              parameters:
                chimeraResource: "#triples"
          - to:
              uri: "mapt://rdf"
              parameters:
                template: "#template"
                basePath: "./output"
                filename: "result.csv"
    ```

**What happens:**

1. `graph://get` creates an empty in-memory RDF graph and places it in the exchange body.
2. `graph://add` loads `input.ttl` into the graph.
3. `mapt://rdf` executes the Velocity template against the graph and writes the output to `./output/result.csv`.

## What to Read Next

| If you want to... | Read |
|--------------------|------|
| Understand how RDF graphs are created and stored | [RDF Graphs](rdf-graphs.md) |
| Learn how external files, URLs, and classpath resources are referenced | [ChimeraResource](chimera-resources.md) |
| See all graph operations (add, query, dump, etc.) | [Graph Component](graph-component.md) |
| Use templates to transform data to/from RDF | [Mapping Template Component](mapt-component.md) |

