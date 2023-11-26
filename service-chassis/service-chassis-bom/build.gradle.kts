

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform(project(":service-chassis-dependencies-bom")))

    constraints {
        rootProject.subprojects.filter { !it.name.endsWith("-bom") }.forEach { api(it) }
    }
}