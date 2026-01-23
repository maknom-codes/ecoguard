package com.maknom.eco.guard;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;


@SpringBootTest
public abstract class PostgresContainerConfig {

   protected static final PostgreSQLContainer<?> postgresSQLContainer  = new PostgreSQLContainer<>(DockerImageName
           .parse("postgis/postgis:17-3.5-alpine")
              .asCompatibleSubstituteFor("postgres"))
              .withDatabaseName("db_ecoguard")
              .withUsername("test")
              .withPassword("test");

   @Container
   @ServiceConnection
   protected static final GenericContainer<?> redisContainer  = new GenericContainer<>(DockerImageName
           .parse("redis:7-alpine").asCompatibleSubstituteFor("postgres"))
           .withExposedPorts(6379);

   static {
      postgresSQLContainer.start();
      redisContainer.start();
   }

   @DynamicPropertySource
   static void configureProperties(DynamicPropertyRegistry registry) {
      registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
      registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
      registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
      registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
      registry.add("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", () -> "false");
      registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

      registry.add("spring.data.redis.password", () -> "");
   }
}
