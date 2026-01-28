package com.cefriel;

import com.cefriel.util.ChimeraResourceBean;
import com.cefriel.util.JdbcConnectionDetails;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MaptPostgresTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("file/sql/postgres-init.sql");

    @BeforeAll
    static void beforeAll() {
        template = new ChimeraResourceBean("file://./src/test/resources/file/sql/template.vm", "");
        if (DockerTestConditions.isDockerAvailable()) {
            postgres.start();
        }
    }

    @AfterAll
    static void afterAll() {
        if (DockerTestConditions.isDockerAvailable() && postgres.isRunning()) {
            postgres.stop();
        }
    }

    private static ChimeraResourceBean template;

    @Test
    @EnabledIf("com.cefriel.DockerTestConditions#isDockerAvailable")
    public void testPostgresTemplateJson() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:postgres");
        mock.expectedMessageCount(1);

        JdbcConnectionDetails jdbcConnectionDetails = new JdbcConnectionDetails(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        start.sendBody(jdbcConnectionDetails);

        mock.assertIsSatisfied();

        String result = mock.getExchanges().get(0).getMessage().getBody(String.class);
        String correctOutput = Files.readString(Paths.get(("./src/test/resources/file/sql/output-correct.ttl")));
        assert TestUtils.isIsomorphicGraph(correctOutput, "turtle", result, "turtle");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                getCamelContext().getRegistry().bind("template", template);

                from("direct:start")
                        .to("mapt://sql?template=#bean:template")
                        .to("mock:postgres");
            }
        };
    }

}
