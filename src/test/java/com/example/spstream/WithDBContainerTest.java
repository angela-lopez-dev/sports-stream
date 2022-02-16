package com.example.spstream;

import org.junit.Before;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class WithDBContainerTest {
    private static final String LOCAL_JDBC_URL_TEMPLATE = "jdbc:postgresql://localhost:%s/%s";
    private static final String TEST_USER = "test";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_DB_NAME = TEST_USER;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName(TEST_DB_NAME)
            .withUsername(TEST_USER)
            .withPassword(TEST_PASSWORD)
            .withExposedPorts(5432);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> String.format(LOCAL_JDBC_URL_TEMPLATE, postgres.getFirstMappedPort(),TEST_DB_NAME));
        registry.add("spring.datasource.username", () -> TEST_USER);
        registry.add("spring.datasource.password", () -> TEST_PASSWORD);
    }





}
