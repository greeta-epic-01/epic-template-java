buildscript {
    extra.apply {
        set("kotlin_version", "1.6.10")
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
      classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10") // JVM
      classpath("org.jetbrains.kotlin:kotlin-noarg:1.6.10")   // JPA
      classpath("org.jetbrains.kotlin:kotlin-allopen:1.6.10") // Spring/All open
    }
}

plugins {
    id("org.springframework.boot") version springBootVersion apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE" apply false
    kotlin("plugin.allopen") version "1.6.10"
    kotlin("jvm") version "1.6.10"
}


repositories {
    mavenCentral()
}


allprojects {

    group = "net.chrisrichardson.liveprojects.servicechassis"
    version = "0.0.1-SNAPSHOT"


}


subprojects {

    if (name.endsWith("-bom")) {
        apply(plugin = "java-platform")
        return@subprojects
    } else if (name.endsWith("-plugins")) {
       return@subprojects
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    repositories {
        mavenCentral()
        maven(url = "https://repo.spring.io/milestone")
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

    configure<JavaPluginExtension> {

        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }

    }


    dependencies {
        implementation(platform(project(":service-chassis-dependencies-bom")))

        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

}

subprojects {
    apply(plugin = "maven-publish")

    configure<PublishingExtension> {

        if (!name.endsWith("-plugins"))
          publications {
              create<MavenPublication>("myLibrary") {
                  if (project.name.endsWith("-bom"))
                      from(components["javaPlatform"])
                  else
                      from(components["java"])
              }
          }

        repositories {
            maven {
                name = "myRepo"
                url = uri(if (project.hasProperty("chassisRepo")) project.property("chassisRepo") as String else File(rootDir, "../build/repository"))
                if (System.getenv("MAVEN_REPO_USERNAME") != null)
                  credentials {
                      username = System.getenv("MAVEN_REPO_USERNAME")
                      password = System.getenv("MAVEN_REPO_PASSWORD")
                  }
            }

        }
    }

}

tasks.register("validateEnvironment", DefaultTask::class) {
    dependsOn("composePull")
}

val testReportData by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("test-report-data"))
    }
}

val liveProject : String by project

val todoModules =
        mapOf(
                "series-01-live-project-01-configuring-observability" to listOf("service-chassis-health-check", "service-chassis-distributed-tracing", "service-template-metrics")
                ,"series-01-live-project-02-implementing-security" to listOf("service-template-web-security", "service-template-main")
        )

dependencies {
    todoModules[liveProject]?.forEach { testReportData(project(":$it")) }
}

val todoBuildTask = tasks.register("todoBuild", GradleBuild::class) {
    tasks = todoModules[liveProject]?.map { "${it}:check" } ?: listOf()
    startParameter.projectProperties.put("ignoreTestFailures", "true")
}.get()

tasks.register<TestReport>("todo") {
    destinationDir = file("$buildDir/reports/todoTestReport")
    // Use test results from testReportData configuration
    (getTestResultDirs() as ConfigurableFileCollection).from(testReportData)
    dependsOn(todoBuildTask)
    doLast {
        print("\n\nSee ${destinationDir.toURI()}/index.html\n\n")
    }
}


val compileAllTask = tasks.register("compileAll", DefaultTask::class).get()

gradle.projectsEvaluated {

    project.subprojects.forEach { p ->
        p.tasks.forEach { t ->
            if (t.name.contains("testClasses", ignoreCase = true)) {
                compileAllTask.dependsOn(t)
            }
        }
    }
}
