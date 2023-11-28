package net.chrisrichardson.liveprojects.servicechassis.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.FileSystems;

public class KeyCloakContainer extends DefaultPropertyProvidingContainer<GenericContainer<?>> {

    @Override
    GenericContainer<?> initContainer() {
        GenericContainer<?> container = new GenericContainer<>(new ImageFromDockerfile()
                .withDockerfile(FileSystems.getDefault().getPath("../keycloak/Dockerfile")))
                .withReuse(true)
                .withEnv("KEYCLOAK_ADMIN", "admin")
                .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
                .withEnv("DB_VENDOR", "h2")
                .withExposedPorts(8091);

        ContainerNetwork.withNetwork(container);
        container.withNetworkAliases(getContainerAlias());
        container.waitingFor(Wait.forHttp("/admin/"));
        return container;
    }

    private String getContainerAlias() {
        return "keycloak";
    }

    @Override
    public void consumeProperties(PropertyConsumer registry) {
        int port = getPort();
        String issuerUrl = "http://localhost:" + port + "/realms/" + getRealm();

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", issuerUrl);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", "http://localhost:" + port + "/realms/" + getRealm() + "/protocol/openid-connect/certs");
        registry.add("keycloak.auth-server-url", getKeyCloakUrl());

        registry.forNameAndPorts(getContainerAlias(), port, 8091);
    }

    public String getKeyCloakUrl() {
        int port = getPort();
        return "http://localhost:" + port;
    }

    public int getPort() {
        return container.getMappedPort(8091);
    }

    public String getRealm() {
        return "service-template";
    }
}