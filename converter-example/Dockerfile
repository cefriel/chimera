FROM openjdk:8-jdk

LABEL maintainer "Alessio Carenini <alessio.carenini@cefriel.com>"
WORKDIR /
# Copy the installation file recieved after registration
COPY ./target/converter-example-1.0.0.jar /

CMD [ "java",  "-cp",  "/converter-example-1.0.0.jar",  "ConverterApplication"]
