_Chimera_: Composable Semantic Transformation Pipelines
===
[![Maven Central](https://img.shields.io/maven-central/v/com.cefriel/chimera.svg?label=Maven%20Central)](https://search.maven.org/artifact/com.cefriel/chimera
)

Chimera is a framework implemented on top of [Apache Camel](https://camel.apache.org/) offering components to define schema and data transformation pipelines based on Semantic Web solutions.

:arrow_forward: The [`chimera-tutorial`](https://github.com/cefriel/chimera-tutorial) repository provides a complete example of a Chimera conversion pipeline.

### Goals
The motivating scenario is about many stakeholders with an interoperability need. To avoid the definition of point-to-point conversions, an **any-to-one centralized mapping approach** based on Semantic Web technologies (and a reference ontology used as global conceptual model) offers the following advantages:

- Let stakeholders keep using their current legacy systems
- To obtain interoperability with other actors in the ecosystem, a stakeholder only needs to define **lifting** mappings from the adopted standard to the reference ontology, and **lowering** mappings from the reference ontology to the standard
- **Knowledge graph** as an additional valuable product of the conversion

The main goal of Chimera is to facilitate the definition of conversion pipelines in the described scenario. In particular, the main objectives are:

* to enable the definition of so-called _semantic conversion pipelines_ (using Semantic Web-based solutions for data transformation) to obtain message-to-message mediators or batch converters;
* to minimise the amount of code to be written, in principle, the aim is to completely avoid coding by just configuring the various components provided.

### Architecture
The main assumption of Chimera is that it is possible to break down a converter (or mediator, using the naming conventions specific to Enterprise Service Buses) into smaller, composable and reusable entities. The inspiration to this approach is taken from the [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/), breaking a data-based process into blocks to be composed, that are implemented in Apache Camel. Chimera provides additional blocks for the Apache Camel framework enabling the reuse of production-ready Camel components already defined (e.g., integration with input/sink data sources) and/or the implementation of additional blocks (e.g., custom pre-processing).

The RDF graph can be interpreted as a variable which is shared among all the blocks in a conversion process. A basic conversion process based on Semantic Web technologies generates triples from the incoming message/dataset (lifting) and uses the resulting graph to extract data which is used to populate the structure of the destination message/dataset (lowering). Furthermore, the conversion can require enrichment with some background knowledge (being either a set of ontologies or a set of master/lookup data).

With this high-level process in mind, we defined a core set of blocks:

* _Lifting or Graph Construction_: this block takes a structured message as input, and enriches the RDF graph with the triples obtained by applying a "mapping" to the input.
* _Graph Transformations_: this block loads a set of RDF files, or generates a set of triples (e.g. CONSTRUCT queries or ontology enabled inferences) and loads them into the RDF graph.
* _Graph Validation_: this block loads a set of ontology files into the RDF graph, inference rules can generate additional triples enriching the graph.
* _Lowering or Graph Exploitation_: this block applies a “mapping” to data extracted from the RDF graph, and produces a structured message as output.

<p align="left"><img src="pipeline.png" alt="Generic pipeline" width="800"></p>

### Use Chimera
The project has a parent POM that can be used to trigger Maven goals and compile the project locally. The different modules can be imported as dependencies from Maven Central.
Chimera is currently composed of three Apache Camel Components, available as sub-projects in this repository. All the components rely on the [rdf4j](https://rdf4j.org/) library to handle the RDF graph.

- `camel-chimera-graph` Camel component used to create and manipulate RDF knowledge graphs.
- `camel-chimera-rmlmapper` Camel component used to lifting using the [rmlmapper-cefriel](https://github.com/cefriel/rmlmapper-cefriel) library 
- `camel-chimera-mapping-template` Camel component able to implement both lifting and lowering steps using the [mapping-template](https://github.com/cefriel/mapping-template) library

### Chimera Pipeline Configuration

Apache Camel provides support for multiple domain-specific languages
(([DSL](https://camel.apache.org/manual/dsl.html))) to define routes,
with the primary options being the Java, XML, and YAML DSLs. When
utilizing the YAML DSL, routes can be configured graphically through
Camel Karavan, as detailed in the approach outlined
[here](./karavan/).

### References

Projects using Chimera:

- SmartEdge: Semantic Low-code Programming Tools for Edge Intelligence https://www.smart-edge.eu/
- TANGENT: Enhanced Data Processing Techniques for Dynamic Management of Multimodal Traffic https://tangent-h2020.eu/
- SNAP: Seamless exchange of multi-modal transport data for transition to National Access Points https://snap-project.eu/
- SPRINT: Semantics for PerfoRmant and scalable INteroperability of multimodal Transport http://sprint-transport.eu/

Publications:
- `Grassi M., Scrocca M., Comerio M., Carenini A., Celino I. (2023) Composable Semantic Data Transformation Pipelines with Chimera. In: 4th International Workshop On Knowledge Graph Construction co-located with the ESWC 2023. CEUR-WS. https://ceur-ws.org/Vol-3471/paper9.pdf`
- `Scrocca M., Comerio M., Carenini A., Celino I. (2020) Turning Transport Data to Comply with EU Standards While Enabling a Multimodal Transport Knowledge Graph. In: The Semantic Web – ISWC 2020. Springer. https://doi.org/10.1007/978-3-030-62466-8_26`

### Commercial Support

If you need commercial support for Chimera contact us at [chimera-dev@cefriel.com](mailto:chimera-dev@cefriel.com).

### License

_Copyright (c) 2019-2024 Cefriel._

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
