package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ServiceDomainModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        p.apply plugin: getClass().getClassLoader().loadClass("net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin")

        p.allOpen {
            annotation("javax.persistence.Entity")
            annotation("javax.persistence.Embeddable")
            annotation("javax.persistence.MappedSuperclass")
        }

        p.dependencies {
            api("org.springframework.data:spring-data-commons")
            compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-domain-security")

            testImplementation(project(path: ":test-data"))

        }

    }
}
