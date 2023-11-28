package net.chrisrichardson.liveprojects.servicechassis.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ZipkinContainer extends DefaultPropertyProvidingContainer<GenericContainer<?>> {

    private static final String CONTAINER_ALIAS = "zipkin";

    @Override
    public void consumeProperties(PropertyConsumer registry) {
        int port = getPort();
        registry.forNameAndPorts(getContainerAlias(), port, 9411);
        registry.add("spring.zipkin.baseUrl", "http://localhost:" + port);
    }

    @Override
    GenericContainer<?> initContainer() {
        return new GenericContainer<>("openzipkin/zipkin:2.23")
                .withReuse(true)
                .withExposedPorts(9411)
                .waitingFor(Wait.forHttp("/api/v2/spans?serviceName=anything"));

    }

    public String getContainerAlias() {
        return CONTAINER_ALIAS;
    }

    public int getPort() {
        return container.getMappedPort(9411);
    }

    public String getTracesUrl(String applicationName) {
        return "http://localhost:" + getPort() + "/api/v2/traces?" + applicationName;
    }
}