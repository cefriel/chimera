# Custom Functions

Custom functions allow you to extend the Velocity template with Java methods callable from within the template. They are provided by a class that extends `com.cefriel.template.utils.TemplateFunctions` from the [mapping-template](https://github.com/cefriel/mapping-template) library.

There are two mutually exclusive ways to provide custom functions. You **cannot** use both simultaneously.

## Option 1: In-Project Class (`customFunctions`)

Use this when the custom functions class is compiled as part of your project.

Define a class extending `TemplateFunctions`:

```java
public class MyFunctions extends TemplateFunctions {
    public String greet(String name) {
        return "Hello, " + name;
    }
}
```

Reference it in the endpoint:

=== "Java DSL"

    ```java
    from("direct:start")
        .to("mapt://?template=#bean:template&customFunctions=#class:com.example.MyFunctions");
    ```

=== "YAML"

    ```yaml
    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://"
              parameters:
                template: "#template"
                customFunctions: "#class:com.example.MyFunctions"
    ```

## Option 2: External Java File (`resourceCustomFunctions`)

Use this when the custom functions are provided as an external `.java` file that is compiled at runtime.

=== "Java DSL"

    ```java
    ChimeraResourceBean functions = new ChimeraResourceBean(
        "file://./functions/CustomFunctions.java", "java");
    getCamelContext().getRegistry().bind("functions", functions);

    from("direct:start")
        .to("mapt://?template=#bean:template&resourceCustomFunctions=#bean:functions");
    ```

=== "YAML"

    ```yaml
    - beans:
        - name: functions
          type: com.cefriel.util.ChimeraResourceBean
          properties:
            url: "file://./functions/CustomFunctions.java"
            serializationFormat: "java"

    - route:
        from: "direct:start"
        steps:
          - to:
              uri: "mapt://"
              parameters:
                template: "#template"
                resourceCustomFunctions: "#functions"
    ```

!!! warning
    Runtime compilation requires a JDK (not just a JRE) to be available, since it uses `javax.tools.JavaCompiler`.

The external `.java` file must define a class named `CustomFunctions` that extends `TemplateFunctions`.

