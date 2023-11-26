package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*

class ComponentTestsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val sourceSets = project.the<SourceSetContainer>()
        val configurations = project.configurations
        val tasks = project.tasks

        val main = sourceSets.findByName("main")!!
        val test = sourceSets.findByName("test")!!

        val integrationTestSourceSet = sourceSets.findByName("integrationTest")!!

        val componentTestSourceSet = sourceSets.create("componentTest") {
            compileClasspath += main.output + test.output + integrationTestSourceSet.output
            runtimeClasspath += main.output + test.output + integrationTestSourceSet.output
        }

        configurations["componentTestImplementation"].extendsFrom(configurations.findByName("testImplementation"))
        configurations["componentTestRuntimeOnly"].extendsFrom(configurations.findByName("runtimeOnly"))

        val componentTest = project.task<Test>("componentTest") {
            description = "Runs component tests."
            group = "verification"

            testClassesDirs = componentTestSourceSet.output.classesDirs
            classpath = componentTestSourceSet.runtimeClasspath

            dependsOn(tasks.findByPath("assemble"))
            shouldRunAfter("integrationTest")
        }

        tasks.findByName("check")!!.dependsOn(componentTest)

    }

}
