package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project


class ServiceMainModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        p.apply plugin: "org.springframework.boot"

        ["net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin", "net.chrisrichardson.liveprojects.servicechassis.plugins.ComponentTestsPlugin", "net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin"].collect {
            p.apply plugin: getClass().getClassLoader().loadClass(it)
        }

        p.bootJar {
            layered {
                enabled = true
            }
        }

        p.dependencies {

            implementation(project(path: ":domain"))
            implementation(project(path: ":config"))
            implementation(project(path: ":persistence"))
            implementation(project(path: ":web"))
            implementation(project(path: ":web-security"))
            implementation(project(path: ":metrics"))

            testImplementation(project(path: ":test-data"))

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-util")
            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-distributed-tracing")
            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-web-swagger")

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-health-check")
            implementation("org.springframework.boot:spring-boot-starter")

            runtimeOnly("org.springframework.boot:spring-boot-devtools")

            implementation("org.springdoc:springdoc-openapi-ui")

            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-util")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")
            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-keycloak")

            testImplementation("org.springframework.boot:spring-boot-starter-test")

            testImplementation("org.testcontainers:testcontainers")
            testImplementation("org.testcontainers:junit-jupiter")
            testImplementation("org.testcontainers:selenium")
            testImplementation("org.seleniumhq.selenium:selenium-remote-driver")
            testImplementation("org.seleniumhq.selenium:selenium-chrome-driver")

        }

        p.integrationTest.exclude "**/SwaggerUITests*"

        p.task("testSwaggerUI", type: org.gradle.api.tasks.testing.Test) {
            include "**/SwaggerUITests*"
            testClassesDirs = p.integrationTest.testClassesDirs
            classpath = p.integrationTest.classpath

        }

        def checkTask = p.tasks.findByName("check")

        ["domain", "web", "web-security", "persistence", "metrics"].collect {
            checkTask.shouldRunAfter(":$it:check")
        }

    }
}
