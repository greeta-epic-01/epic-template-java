package net.chrisrichardson.liveprojects.servicechassis.testcontainers;

import com.github.dockerjava.api.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.Map;
import java.util.function.Supplier;

public interface PropertyProvidingContainer {

    void addProperties(DynamicPropertyRegistry registry);

    void consumeProperties(DefaultPropertyProvidingContainer.PropertyConsumer registry);

    void start();

    void pause();

    void unpause();

    void startAndAddProperties(DynamicPropertyRegistry registry);

}

class ContainerNetwork {

    private static final Network network = null; // by lazy { Network.SHARED }

    // No networks for now
    static void withNetwork(GenericContainer<?> container) {
        if (network != null)
            container.withNetwork(network);
    }
}

abstract class DefaultPropertyProvidingContainer<T extends GenericContainer<?>> implements PropertyProvidingContainer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final T container;

    public DefaultPropertyProvidingContainer() {
        this.container = initContainer();
    }

    abstract T initContainer();

    @Override
    public void startAndAddProperties(DynamicPropertyRegistry registry) {
        logger.info("Starting {}", this);
        start();
        addProperties(registry);
    }

    static void startAllAndAddProperties(DynamicPropertyRegistry registry, PropertyProvidingContainer... containers) {
        for (PropertyProvidingContainer container : containers) {
            container.startAndAddProperties(registry);
        }
    }

    static void startAll(PropertyProvidingContainer... containers) {
        for (PropertyProvidingContainer container : containers) {
            container.start();
        }
    }

    static Map<String, String> getPropertiesForClientContainer(PropertyProvidingContainer... containers) {
        Map<String, String> properties = new java.util.HashMap<>();
        for (PropertyProvidingContainer container : containers) {
            container.consumeProperties(new PropertyConsumer() {

                String containerName;
                int hostPort;
                int servicePort;

                @Override
                public void forNameAndPorts(String containerName, int hostPort, int servicePort) {
                    this.containerName = containerName;
                    this.hostPort = hostPort;
                    this.servicePort = servicePort;
                }

                @Override
                public void add(String name, String value) {
                    properties.put(name, value.replaceAll("localhost:[0-9]+", containerName + ":" + servicePort).replace("localhost", containerName));
                }
            });
        }
        return properties;
    }

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void pause() {
        container.getDockerClient().pauseContainerCmd(container.getContainerId()).exec();
    }

    @Override
    public void unpause() {
        try {
            container.getDockerClient().unpauseContainerCmd(container.getContainerId()).exec();
        } catch (InternalServerErrorException e) {
            if (!e.getMessage().contains(" is not paused")) {
                throw e;
            }
        }
    }


    interface PropertyConsumer {

        void forNameAndPorts(String containerName, int hostPort, int servicePort);

        void add(String name, String value);

    }

    private class RegistryConfigurer implements PropertyConsumer {

        private final DynamicPropertyRegistry registry;

        RegistryConfigurer(DynamicPropertyRegistry registry) {
            this.registry = registry;
        }

        @Override
        public void forNameAndPorts(String containerName, int hostPort, int servicePort) {
            // Do nothing
        }

        @Override
        public void add(String name, String value) {
            registry.add(name, () -> value);
        }
    }

    @Override
    public void addProperties(DynamicPropertyRegistry registry) {
        consumeProperties(new RegistryConfigurer(registry));
    }

    @Override
    public void consumeProperties(PropertyConsumer registry) {
        // Do nothing
    }

    protected String maybeReplaceLocalhost(String url, boolean forLocalhost, String containerAlias) {
        return forLocalhost ? url : url.replaceAll("localhost:[0-9]+", containerAlias);
    }
}