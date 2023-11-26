package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project


class ServiceWebModulePlugin implements Plugin<Project> {
    void apply(Project p) {

        ["net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin", "net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin"].collect {
            p.apply plugin: getClass().getClassLoader().loadClass(it)
        }

        p.dependencies {

            implementation(project(path: ":domain"))
            testImplementation(project(path: ":test-data"))

            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-util")
            implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-web")


            testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")

        }


        p.tasks.findByName("check").shouldRunAfter(":domain:check")
    }
}
