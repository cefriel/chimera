Chimera Composable Semantic Data Transformation
===

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
- Use the RML lifter block to obtain a Linked GTFS representation of a sample GTFS feed.
    ```
    POST http://localhost:8888/chimera-demo/lift/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    ```
- Use the _RML lifter_ block and the rdf-lowerer block to obtain back a GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation.
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
    - Copy the url returned (let's assume it is `file://url_resource_loaded`)
    - Perform the enriched conversion
        ```
        POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
        Attach the file chimera-example/inbox/sample-gtfs-feed.zip
        Add as header additional_source:file://url_resource_loaded
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
