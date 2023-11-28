plugins {
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.ServiceDomainModulePlugin")
}

dependencies {
    testImplementation(project(":test-data"))
}
