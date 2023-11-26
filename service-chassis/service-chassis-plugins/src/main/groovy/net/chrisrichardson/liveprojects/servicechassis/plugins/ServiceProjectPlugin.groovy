package net.chrisrichardson.liveprojects.servicechassis.plugins

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

class ServiceProjectPlugin implements Plugin<Project> {
    void apply(Project rootProject) {

        def props = new Properties()
        props.load(getClass().getResourceAsStream("/service.plugin.properties"))

        def thisVersion = props.getProperty("version")

        def thisChassisRepo = props.getProperty("chassisRepo")

        if (thisChassisRepo == "local")
          thisChassisRepo = new File(rootProject.rootDir, "../build/repository")

        rootProject.apply plugin: "docker-compose"

        rootProject.dockerCompose {
            def pn = null
            projectName = pn
            removeContainers = false
            stopContainers = rootProject.ext.stopContainersDefault.toBoolean()
            buildBeforePull = false

            infrastructure {
                projectName = pn
                startedServices = ["mysql", "keycloak"]
            }
        }

        rootProject.gradle.projectsEvaluated {
            def assemble = rootProject.tasks.findByPath(":main:assemble")
            for (task in rootProject.tasks) {
                if (task.name.endsWith("omposeUp")) task.dependsOn(assemble)
                // This did not seem to work
                if (task.name.endsWith("omposeBuild")) task.dependsOn(assemble)
            }
        }


        rootProject.subprojects { project ->

            tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile) {
                kotlinOptions {
                    freeCompilerArgs = ["-Xjsr305=strict"]
                    jvmTarget = "11"
                }
            }

            tasks.withType(Test) {
                useJUnitPlatform()
            }

            project.apply plugin: "org.jetbrains.kotlin.jvm"
            project.apply plugin: "org.jetbrains.kotlin.plugin.spring"
            project.apply plugin: "org.jetbrains.kotlin.plugin.jpa"
            project.apply plugin: "org.jetbrains.kotlin.plugin.allopen"

            project.repositories {
                mavenCentral()
                maven {
                  url = uri(thisChassisRepo)
                  if (System.getenv("MAVEN_REPO_USERNAME") != null)
                    credentials {
                        username = System.getenv("MAVEN_REPO_USERNAME")
                        password = System.getenv("MAVEN_REPO_PASSWORD")
                    }
                }
            }


            project.java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(11)
                }

            }

            project.dependencies {

                constraints {
                    implementation("com.google.guava:guava") {
                        version {
                            strictly("25.0-jre")
                        }
                        because("Selenium conflict")
                    }
                }

                implementation(platform("net.chrisrichardson.liveprojects.servicechassis:service-chassis-bom:$thisVersion"))

                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

                testImplementation("org.springframework.boot:spring-boot-starter-test")
                testImplementation("org.mockito.kotlin:mockito-kotlin")

            }

        }


        rootProject.gradle.projectsEvaluated {
            rootProject.subprojects.forEach { p ->
                def compileAllTask = p.task("compileAll", type: DefaultTask)
                p.tasks.forEach { t ->
                    if (t.name.contains("estClasses")) {
                        compileAllTask.dependsOn(t)
                    }
                }
            }
            // Move this to main

            rootProject.tasks.findByPath(":main:bootRun").dependsOn(":infrastructureComposeUp")

        }


    }
}
