## Running the Converter on Kubernetes

Build the converter and push it to a Docker registry. You can find the demo Docker image already built on Docker Hub  _marioscrock/chimera-example_
```bash
docker build -t <repository>/chimera-example .
docker push <repository>/chimera-example
```
If you used a different image modify the file chimera-converter.yml (exposed port, resources needed/limits, Docker image, labels, etc.), otherwise you can directly use the provided file with the _marioscrock/chimera-example_ image. The file creates a Deployment using the converter image for the Pod, and a related Service.
```bash
kubectl apply -f chimera-converter.yaml
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
