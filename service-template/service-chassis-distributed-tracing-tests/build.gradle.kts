plugins {
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.IntegrationTestsPlugin")
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.RestAssuredTestDependenciesPlugin")
}

dependencies {

    implementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-distributed-tracing")

    testImplementation(project(":domain"))
    testImplementation(project(":web"))
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-util")
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-util")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("net.chrisrichardson.liveprojects.servicechassis:service-chassis-test-containers")

}
