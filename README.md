_Chimera_: Composable Semantic Transformation Pipelines
===
Chimera is a framework implemented on top of [Apache Camel](https://camel.apache.org/) and offering components to compose conversion pipelines based on Semantic Web solutions.

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

* _Lifting_: this block takes a structured message as input, and enriches the RDF graph with the triples obtained by applying a "mapping" to the input.
* _Data enricher_: this block loads a set of RDF files, or generates a set of triples (e.g. CONSTRUCT queries) and loads them into the RDF graph.
* _Inference enricher_: this block loads a set of ontology files into the RDF graph, inference rules can generate additional triples enriching the graph.
* _Lowering_: this block applies a “mapping” to data extracted from the RDF graph, and produces a structured message as output.

<p align="left"><img src="pipeline.png" alt="Generic pipeline" width="800"></p>

Chimera contains a default implementation for the mentioned blocks, plus additional _utilities_ blocks to configure a semantic conversion pipeline. Each module is decoupled from the others providing high flexibility in configuring pipelines for different requirements.

### Project structure
The project has a parent POM (in the parent directory) that can be used to trigger the Maven builds of sub-projects respecting internal dependencies. You can modify the parent POM to select only sub-projects you are interested in.
The main sub-project is `chimera-core` that contains the basic blocks of the Chimera framework. The [rdf4j](https://rdf4j.org/) library is used to handle the RDF graph in all the pipeline blocks. Additional blocks can be found in the other sub-projects:

- `chimera-rml` contains the blocks to implement lifting using the [rml-mapper](https://github.com/cefriel/rmlmapper-cefriel) library 
    - This sub-project depends on the mentioned library that is imported as a git submodule in `libs/rmlmapper-cefriel`
- `chimera-rdf-lowerer` contains the blocks to implement lowering using the [rdf-lowerer](https://github.com/cefriel/rdf-lowerer) library 
    - This sub-project depends on the mentioned library that is imported as a git submodule in `libs/rdf-lowerer`
- `chimera-records` contains a set of utility blocks to gather data (timestamps, RDF graph data,...)  within a Chimera pipeline

### How to compile the project
- Clone the repository and the required git submodules
    ```
    git clone --recurse-submodules https://github.com/cefriel/chimera.git
    ```
- Remove comments from the parent `pom.xml` if required libraries (git submodules) are not already installed in the local Maven repository
- Run `mvn:install` in the root folder to build all the sub-projects and libraries

### References

Projects using Chimera:

- SNAP: Seamless exchange of multi-modal transport data for transition to National Access Points https://snap-project.eu/
- SPRINT: Semantics for PerfoRmant and scalable INteroperability of multimodal Transport http://sprint-transport.eu/

Publications:
- `Scrocca M., Comerio M., Carenini A., Celino I. (2020) Turning Transport Data to Comply with EU Standards While Enabling a Multimodal Transport Knowledge Graph. In: The Semantic Web – ISWC 2020. Springer. https://doi.org/10.1007/978-3-030-62466-8_26`

### Commercial Support

If you need commercial support for Chimera contact us at [info@cefriel.com](mailto:info@cefriel.com).

### License

_Copyright (c) 2019-2022 Cefriel._

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
