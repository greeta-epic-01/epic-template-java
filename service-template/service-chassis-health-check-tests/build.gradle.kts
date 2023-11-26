plugins {
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin")
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin")
}

dependencies {
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-health-check")

    testImplementation(project(":domain"))
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-util")

    testImplementation(project(":persistence"))
    testImplementation(project(":test-data"))
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-util")

    runtimeOnly("mysql:mysql-connector-java")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.findByName("check")!!.shouldRunAfter(":domain:check")
