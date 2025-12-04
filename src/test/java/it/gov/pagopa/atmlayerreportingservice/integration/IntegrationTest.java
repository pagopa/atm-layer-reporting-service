package it.gov.pagopa.atmlayerreportingservice.integration;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.Testcontainers;

// @QuarkusTest
// @QuarkusTestResource(value = EnvironmentTestServicesResource.DockerCompose.class, restrictToAnnotatedClass = true)
public class IntegrationTest {

    @BeforeAll
    static void exposeTestPort() {
        Testcontainers.exposeHostPorts(8086);
    }

}
