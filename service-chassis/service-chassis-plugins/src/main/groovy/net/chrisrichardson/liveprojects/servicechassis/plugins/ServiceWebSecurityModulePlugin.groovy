package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project


class ServiceWebSecurityModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        ["net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin", "net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin"].collect {
            p.apply plugin: getClass().getClassLoader().loadClass(it)
        }

        p.dependencies {

            implementation(project(path: ":domain"))

            testImplementation(project(path: ":web"))
            testImplementation(project(path: ":metrics"))

            testImplementation(project(path: ":config"))
            testImplementation(project(path: ":test-data"))


            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-util")

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-web-security")

            testImplementation("org.testcontainers:testcontainers")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-health-check")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-domain-security")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-keycloak")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-util")

            testImplementation("org.springframework.security:spring-security-test")
        }

        p.tasks.findByName("check").shouldRunAfter(":domain:check")

    }
}
