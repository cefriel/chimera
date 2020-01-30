FROM openjdk:11-jdk

WORKDIR /home/
COPY ./target/chimera-example-1.0.0.jar /home/
COPY src/main/resources /home/

EXPOSE 8888

CMD [ "java",  "-cp",  "/home/chimera-example-1.0.0.jar",  "ConverterApplication"]

