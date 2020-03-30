_Chimera_: Composable Semantic Data Transformation
===
Chimera is a tool to build conversion pipelines and to integrate them inside Java applications or to expose them as services. It is implemented on top of Apache Camel.

### Goals
The main goals of Chimera are:
* to define so-called _semantic conversion pipelines_, transformations using Semantic Web technologies to obtain message-to-message mediators or batch converters.
* to minimise the amount of code to be written. In principle the aim is completely avoid coding by just configuring the various components.

### Architecture
The main assumption of Chimera is that it is possible to break down a converter (or mediator, using the naming conventions specific to Enterprise Service Buses) into smaller, composable and reusable entities. The insipiration to this approach is taken from both the ETL applications like Talend and the Enterprise Integration Patterns, since they both break a data-based process into blocks to be composed.
The architecture of Chimera is heavily inspired by the Enterprise Integration Pattern "Data enricher". A conversion process based on Semantic Web technologies can be seen as an RDF graph which is initially enriched with some background knowledge (being either a set of ontologies or a set of master/lookup data), and then enriched with triples coming from the incoming message/dataset. The resulting graph can then be exploited to extract data which is used to populate the structure of the destination message/dataset. The RDF graph can be interpreted as a variable which is shared by default among all the blocks in a conversion process.
With this high-level process in mind, we can start defining a first set of “conversion blocks”:
* _Lifting_: this block takes a structured message as input, and enriches the RDF graph with the triples obtained by applying a "mapping" to the input.
* _Data enricher_: this block loads a set of RDF files and loads them into the RDF graph.
* _Inference enricher_: this block loads a set of ontology files (in either RDFS or OWL format) and loads them into the RDF graph. The RDF graph becomes a “inference graph” after using this block, since the ontologies are used to drive inference.
* _Lowering_: this block applies a “mapping” to the RDF graph, and produces a structured message as output.

### How to run it
- Clone the repository
    ```
    git clone --recurse-submodules https://github.com/cefriel/chimera.git
    ```
- Remove comments from `pom.xml` if required libraries are not installed in the local Maven repository
- Run `mvn:install` in the root folder to build all the sub-projects and libraries
- Build the chimera-example image
    ```
    cd chimera-example && docker build --no-cache -t chimera-example .
    ```
- Run the `chimera-example` container
    ```
    docker run -p 8888:8888 chimera-example
    ```
### How to test it
- Use the _RML lifter_ block to obtain a Linked GTFS representation of a sample GTFS feed.
    ```
    POST http://localhost:8888/chimera-demo/lift/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back a GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation.
    ```
    POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back an _enriched_ GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation.
    - Load an additional source
        ```
        POST http://localhost:8888/chimera-demo/load/ 
        Attach the file chimera-example/inbox/enrich.ttl
        Add as header filename:enrich.ttl
        ```
    - Perform the enriched conversion
        ```
        POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
        Attach the file chimera-example/inbox/sample-gtfs-feed.zip
        Add as header additional_source:enrich.ttl
        ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back an _enriched_ GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation and downloading the additional source from a server requiring _JWT based authentication_.
    - Perform the enriched conversion
        ```
        POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
        Attach the file chimera-example/inbox/sample-gtfs-feed.zip
        Add as header 
            additional_source:<url_server_source>
            username:<server_username>
            password:<server_password>
        ```

### License

_Copyright 2020 Cefriel._

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
