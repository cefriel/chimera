# Chimera Example

The `chimera-example` project offers a complete example to easily understand how to develop a Converter using the Chimera framework.

## Deployment

Basic deployment alternatives to run a Converter made with Chimera, exemplified using the `chimera-example`.

#### Executable Jar

The executable jar can be obtained using Maven. Assuming Maven is installed you can run:
```bash
mvn clean install
```
After finishing the execution, the executable jar is in the _target_ folder.

#### Build the Converter with Docker

Build the converter and push it to a local or remote Docker registry. Note that the provided Dockerfile assumes the executable jar is available in the _target_ folder.
```bash
docker build -t <repository>/chimera-example .
docker push <repository>/chimera-example
```
You can find the demo Docker image already built on Docker Hub  _marioscrock/chimera-example_

#### Running the Converter with docker-compose

Once built the image, you can run the converter using the docker-compose file provided. If you want to use the  _marioscrock/chimera-example_ image available on Docker Hub you need to change the image for the container in the  _docker-compose.yml_ file.

To run the converter execute the following command:
```bash
docker-compose up
```

A different Chimera pipeline can be mounted changing the volumes section in the  _docker-compose.yml_ file.

## Try it

The  `chimera-example`  defines a conversion pipeline, exposed through an API, and considering a sample [GTFS](https://developers.google.com/transit/gtfs) feed as input and the [Linked GTFS](https://github.com/OpenTransport/linked-gtfs) vocabulary as the reference ontology.

- Use the _RML lifter_ block to obtain a Linked GTFS representation of the `stops.txt` file in the sample GTFS feed.
    ```
    POST http://localhost:8888/chimera-demo/lift/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back a GTFS representation of the `stops.txt` file in the sample GTFS feed after a roundtrip through a Linked GTFS representation.
    ```
    POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back an _enriched_ GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation. In this example, we use the example data in `chimera-example/src/main/resources/enrich.ttl`.
    ```
    POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    Add as header additional_source:enrich.ttl
    ```
    You can also use a different additional source using two steps:

    1. Load an additional source
        ```
        POST http://localhost:8888/chimera-demo/load/ 
        For example, attach the file chimera-example/inbox/mysource.ttl
        Add as header filename:my-source.ttl
        ```
    2. Perform the enriched conversion
        ```
        POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
        Attach the file chimera-example/inbox/sample-gtfs-feed.zip
        Add as header additional_source:my-source.ttl
        ```
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back an _enriched_ GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation and enabling RDFS inference. In this example, we use an example ontology (`chimera-example/src/main/resources/ontology.owl`) defining an axiom for the definition of a `range` on the `gtfs:parentStation` property. Using the enricher block with data in the additional source, and enabling inference with that ontology, we can retrieve an additional `gtfs:Stop` in the lowering of the `stops.txt` file.
    ```
    POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
    Attach the file chimera-example/inbox/sample-gtfs-feed.zip
    Add as header 
        additional_source:enrich.ttl
        inference:true
    ```
    **Note** The `InferenceEnricher` block enabled in this pipeline performs a one-time inference evaluation against the schema adding the resulting triples to the graph. To improve performances and guarantee inference throughout the entire pipeline, it is recommended to configure the `AttachGraph` block (a _commented_ example can be found in the `.xml` file of the route).
- Use the _RML lifter_ block and the _rdf-lowerer_ block to obtain back an _enriched_ GTFS representation of a sample GTFS feed after a roundtrip through a Linked GTFS representation and downloading the additional source from a server requiring _JWT based authentication_. To run this example you need to configure the authorization server URL in the chimera pipeline (`chimera-example/src/main/resources/routes/camel-context.xml`)
    - Perform the enriched conversion
        ```
        POST http://localhost:8888/chimera-demo/roundtrip/gtfs/ 
        Attach the file chimera-example/inbox/sample-gtfs-feed.zip
        Add as header 
            additional_source:<url_server_source>
            username:<server_username>
            password:<server_password>
        ```
- Enrichment and Inference can also be applied using the same headers to the lifting route (http://localhost:8888/chimera-demo/lift/gtfs/)

## Monitoring
The `chimera-example` provides an alternative Camel Context (`src\main\resources\routes\camel-context-monitor.xml`) that showcase how to use the [camel-micrometer](https://camel.apache.org/components/latest/micrometer-component.html) component to expose default and custom metrics as an endpoint compliant with the [Prometheus](https://prometheus.io/) format.

The Camel Context can be enabled by changing the `volume` directive in the docker-compose file as follows: 
```
- ./src/main/resources/routes/camel-context-monitor.xml:/home/routes/camel-context.xml
```

The endpoint is available at `http://localhost:8888/chimera-demo/metrics` and can be easily configured with Prometheus for scraping. The endpoint exposes default metrics on the JVM and on Camel Routes and Messages. Moreover, the pipeline it's configured to expose also custom metrics: number of lift/roundtrip conversions executed (`counter`), lift/roundtrip conversion timer (`timer`), metrics on the `seda` queues enabled for the different endpoints (`summary`).

The Micrometer registry configuration can be modified in class `MicrometerConfig`. The class `SedaMetricsProcessor` showcases how to define a custom Camel Processor and it is used in the pipeline to feed the related custom metrics.


## Advanced deployments

Advanced deployment alternatives to run a Converter made with Chimera, exemplified using the `chimera-example`.

#### Running the Converter as a Service with docker-compose

You can also run a scalable converter on a Swarm (for a local single-node Swarm run `docker swarm init`)  and the _docker-compose-converter-service.yml_ file. You can provide a different config changing the  _docker-compose-converter-service.yml_ and _nginx.conf_ files.
```bash
docker-compose -f docker-compose-converter-service.yml up
```
This command exploits an Nginx server configured as a reverse proxy to enable a multi-replicas converter with Docker services load balancing (round-robin). 
To increase the number of replicas run:
```bash
docker-compose -f docker-compose-converter-service.yml up -d --scale chimera-example=3
```

#### Running the Converter on Kubernetes

If you used a different image modify the file chimera-converter.yml (exposed port, resources needed/limits, Docker image, labels, etc.), otherwise you can directly use the provided file with the  _marioscrock/chimera-example_ image available on Docker Hub. The file creates a Deployment using the converter image for the Pod, and a related Service.
```
kubectl apply -f chimera-converter.yml
```
If everything is fine, you can run `kubectl get pods` and `kubectl get services` to visualize the running pods. Example:
```
$ kubectl get pods
NAME                             READY   STATUS    RESTARTS   AGE
chimera-example-c6b446c8-7mbg6   1/1     Running   0          33m
```
You can try the converter locally using the node port 30042, for example:
```
POST http://localhost:30042/chimera-demo/lift/gtfs/ 
Attach the file chimera-example/inbox/sample-gtfs-feed.zip
```
To manually scale the Service you can run
```
$ kubectl scale --replicas=3 deployments/chimera-example
deployment.extensions/chimera-example scaled
$ kubectl get pods
NAME                             READY   STATUS              RESTARTS   AGE
chimera-example-c6b446c8-gb9rs   0/1     ContainerCreating   0          2s
chimera-example-c6b446c8-nnvwc   0/1     ContainerCreating   0          2s
chimera-example-c6b446c8-whnp9   1/1     Running             0          50m
```
To automate the scaling you can use an Horizontal Pod Autoscaler (HPA), for example referring to the current CPU usage. You need `metrics-server` deployed to provide metrics via the resource metrics API to the HPA (instructions for deploying it are on the GitHub repository of [metrics-server](https://github.com/kubernetes-incubator/metrics-server/)). The following command enables autoscaling from 1 up to 5 replicas if CPU usage is greater than 80 percent.
```
$ kubectl autoscale deployments/chimera-example --min=1 --max=5 --cpu-percent=80
```
Different and custom metrics can be used to set the scaling logic. More info and yaml configuration for HPA can be found here: https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale-walkthrough/ 

To check the HPA status run `kubectl get hpa` or `kubectl describe hpa chimera-example`. Example:
```
$ kubectl get hpa
NAME              REFERENCE                    TARGETS   MINPODS   MAXPODS   REPLICAS   AGE
chimera-example   Deployment/chimera-example   9%/80%    1         5         3          31s
```
