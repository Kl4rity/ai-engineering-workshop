package stift.io.home.infrastructure.local

import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.PostgreSQLContainer

@Configuration
@Profile("local")
class TestContainersConfig {
    companion object {
        private val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17-alpine")
                .withDatabaseName("mydatabase")
                .withUsername("myuser")
                .withPassword("secret")
                .withExposedPorts(5432)

        init {
            postgres.start()
            System.setProperty("spring.datasource.url", postgres.getJdbcUrl())
            System.setProperty("spring.datasource.username", postgres.username)
            System.setProperty("spring.datasource.password", postgres.password)
        }
    }

    @PreDestroy
    fun shutdown() {
        postgres.stop()
    }
}
