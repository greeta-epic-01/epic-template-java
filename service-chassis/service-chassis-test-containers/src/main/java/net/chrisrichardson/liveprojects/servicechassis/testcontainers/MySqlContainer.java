package net.chrisrichardson.liveprojects.servicechassis.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySqlContainer extends DefaultPropertyProvidingContainer<MySQLContainer<?>> {

    private static final String CONTAINER_ALIAS = "mysql";

    @Override
    public void consumeProperties(PropertyConsumer registry) {
        registry.forNameAndPorts(getContainerAlias(), getPort(), 3306);

        registry.add("spring.datasource.url", container.getJdbcUrl());
        registry.add("spring.datasource.password", container.getPassword());
        registry.add("spring.datasource.username", container.getUsername());
    }

    @Override
    MySQLContainer<?> initContainer() {
        return new MySQLContainer<>(DockerImageName.parse("mysql/mysql-server:8.0.27-1.2.6-server").asCompatibleSubstituteFor("mysql"))
                .withEnv("MYSQL_ROOT_HOST", "%")
                .withDatabaseName("dbname")
                .withReuse(true);
    }

    public String getContainerAlias() {
        return CONTAINER_ALIAS;
    }

    public int getPort() {
        return container.getMappedPort(3306);
    }
}