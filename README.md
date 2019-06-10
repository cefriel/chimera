Chimera Composable Semantic Data Transformation
===

### Dependencies

* Apache Jena
* RDF4j
* Pinto (https://github.com/carenini/pinto)

### Building Chimera 

To build Chimera, simply run:

    mvn clean package
    
### Available blocks

#### Lifting
* Pinto: mappingless lifting (RDF4j)
* RML: declarative approach based on https://github.com/RMLio/rmlmapper-java
* ST4RT lifting (RDF4j) 

#### Lowering
* Pinto: mappingless lowering (RDF4j)
* ST4RT lowering (RDF4j)

IN PROGRESS: Template-based lowering based on Apache Velocity

#### Data Enrichment
* RDF4j enrichment
* Jena enrichment 

#### Ontology loading
* RDF4j RDFS in-memory reasoning
* Jena RDFS in-memory reasoning

#### RDF transformation
* SHACL processor (Jena)

#### Validation
* RDF4j SHACL (RDF4j)
TODO TopBraid SHACL (Jena)


### License

Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
