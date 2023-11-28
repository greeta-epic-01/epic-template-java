package net.chrisrichardson.liveprojects.servicetemplate;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

public class DockerComposeHelper implements BeforeAllCallback, AfterAllCallback {

    private final DockerComposeContainer<?> environment;

    public DockerComposeHelper(String... services) {
        this.environment = new DockerComposeContainer<>(new File("../docker-compose.yml"))
                .withLocalCompose(false)
                .withBuild(true)
                .withServices(services);

        if (containsService(services, "keycloak")) {
            environment.withExposedService("keycloak_1", 8091, Wait.forHttp("/"));
        }

        environment.withTailChildContainers(true);
    }

    private static boolean containsService(String[] services, String service) {
        for (String s : services) {
            if (s.equals(service)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        environment.start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        environment.stop();
    }
}