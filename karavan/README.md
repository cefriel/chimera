# Integration with Camel Karavan

[Camel Karavan](https://camel.apache.org/categories/Karavan/) is a visual editor for Apache Camel routes, facilitating the design, testing, and monitoring of integration flows. It provides a user-friendly interface for managing Camel routes and their associated components. Chimera components can be integrated into the [Visual Studio Code plugin for Camel Karavan](https://marketplace.visualstudio.com/items?itemName=camel-karavan.karavan) by following these steps.

1. Install Visual Studio Code.
2. Install the Karavan extension for Visual Studio Code.
3. Navigate to the Karavan directory within the Visual Studio Code installation directory and locate the _components.json_ file.
4. Copy the contents of the [graph.json](./camel-chimera-graph/src/generated/resources/com/cefriel/component/graph.json), [mapt.json](./camel-chimera-mapping-template/src/generated/resources/com/cefriel/component/mapt.json) and [rml.json](./camel-chimera-rmlmapper/src/generated/resources/com/cefriel/component/rml.json) files to the _components.json_ file. An example [components.json](./karavan/components.json) is provided, note however that this file has been only been tested with Karavan v4.4.0.
5. Chimera components are now selectable in the Karavan Visual Studio Code UI.

An example YAML route built using this tool is available
[here](https://github.com/cefriel/chimera-tutorial/tree/yaml-tutorial). Note
however that because Chimera is not an official Apache Camel component
slight manual editing to the resulting YAML route is
required. Additionally, as of version
[4.1.0](https://camel.apache.org/blog/2023/11/camel-karavan-4.1.0/)
custom kamelets can now also be defined using Karavan.
