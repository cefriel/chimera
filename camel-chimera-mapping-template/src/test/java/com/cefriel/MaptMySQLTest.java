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
import org.testcontainers.containers.MySQLContainer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MaptMySQLTest extends CamelTestSupport {

    @Produce("direct:start")
    ProducerTemplate start;

    static MySQLContainer<?> mysql = new MySQLContainer<>(
            "mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("file/sql/mysql-init.sql");

    @BeforeAll
    static void beforeAll() {
        template = new ChimeraResourceBean("file://./src/test/resources/file/sql/template.vm", "");
        if (DockerTestConditions.isDockerAvailable()) {
            mysql.start();
        }
    }

    @AfterAll
    static void afterAll() {
        if (DockerTestConditions.isDockerAvailable() && mysql.isRunning()) {
            mysql.stop();
        }
    }

    private static ChimeraResourceBean template;

    @Test
    @EnabledIf("com.cefriel.DockerTestConditions#isDockerAvailable")
    public void testMySQL() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:mysql");
        mock.expectedMessageCount(1);

        JdbcConnectionDetails jdbcConnectionDetails = new JdbcConnectionDetails(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
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
                        .to("mock:mysql");
            }
        };
    }

}
