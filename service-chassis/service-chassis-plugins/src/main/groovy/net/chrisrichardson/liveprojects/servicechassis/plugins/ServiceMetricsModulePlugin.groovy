package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ServiceMetricsModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        ["net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin", "net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin"].collect {
            p.apply plugin: getClass().getClassLoader().loadClass(it)
        }


        p.dependencies {

            implementation(project(path: ":domain"))

            testImplementation(project(path: ":config"))

            testImplementation(project(path: ":test-data"))

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-metrics")

            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-util")

            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")

            testImplementation("org.springframework.boot:spring-boot-starter-web")

            testImplementation("org.testcontainers:testcontainers")
            testImplementation("org.testcontainers:junit-jupiter")

            p.tasks.findByName("check").shouldRunAfter(":domain:check")
        }

    }
}
