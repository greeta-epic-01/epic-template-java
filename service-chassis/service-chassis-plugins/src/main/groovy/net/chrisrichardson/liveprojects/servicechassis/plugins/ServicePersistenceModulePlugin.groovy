package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class ServicePersistenceModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        ["net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin"].collect {
            p.apply plugin: getClass().getClassLoader().loadClass(it)
        }


        p.dependencies {
            implementation(project(path: ":domain"))
            implementation(project(path: ":config"))
            testImplementation(project(path: ":test-data"))

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-persistence")

            runtimeOnly("mysql:mysql-connector-java")

            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")

        }

        p.tasks.findByName("check").shouldRunAfter(":domain:check")

    }
}
