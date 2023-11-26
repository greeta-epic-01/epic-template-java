
plugins {
    `kotlin-dsl`
    `groovy`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.avast.gradle:gradle-docker-compose-plugin:0.14.12")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.jetbrains.kotlin:kotlin-noarg:1.6.10")   // JPA
    implementation("org.jetbrains.kotlin:kotlin-allopen:1.6.10") // Spring/All open
    implementation("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")

}

gradlePlugin {
    plugins {

        listOf("ServiceDomainModulePlugin", "ServiceMainModulePlugin", "ServiceMetricsModulePlugin", "ServicePersistenceModulePlugin", "ServiceWebModulePlugin",
                "ServiceProjectPlugin", "ServiceWebSecurityModulePlugin", "ComponentTestsPlugin", "IntegrationTestsPlugin", "RestAssuredTestDependenciesPlugin", "RestAssuredDependenciesPlugin"
        ).forEach {
            create(it) {
                id = "net.chrisrichardson.liveprojects.servicechassis.plugins.$it"
                implementationClass = "net.chrisrichardson.liveprojects.servicechassis.plugins.$it"
            }

        }

    }
}

val createPropsFileTask = tasks.register<CreateServicePluginPropertiesFileTask>("createServicePluginPropertiesFile") {
    pluginVersion  = project.version as String
    chassisRepo = if (project.hasProperty("chassisRepo")) project.property("chassisRepo") as String else "local"
}

val processResourcesTask : Copy = tasks.findByPath("processResources") as Copy

processResourcesTask.from(files(createPropsFileTask))

