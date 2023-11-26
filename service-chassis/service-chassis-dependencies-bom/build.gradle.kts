javaPlatform {
    allowDependencies()
}

val testContainersVersion = "1.16.2"
val restAssuredVersion = "4.3.3"

dependencies {

    api(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    api(platform("org.springframework.cloud:spring-cloud-dependencies:2021.0.0"))

    constraints {


        api("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
        api("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

        runtime("mysql:mysql-connector-java:8.0.21")

        api("org.springdoc:springdoc-openapi-ui:1.6.6")

        api("org.testcontainers:testcontainers:$testContainersVersion")
        api("org.testcontainers:junit-jupiter:$testContainersVersion")
        api("org.testcontainers:mysql:$testContainersVersion")
        api("org.testcontainers:selenium:$testContainersVersion")
        api("org.seleniumhq.selenium:selenium-remote-driver:3.141.59")
        api("org.seleniumhq.selenium:selenium-chrome-driver:3.141.59")

        api("io.micrometer:micrometer-registry-prometheus:1.6.4")

        api("org.keycloak:keycloak-admin-client:17.0.1")

        api("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")

        api("org.mockito.kotlin:mockito-kotlin:3.2.0")

        api("io.rest-assured:rest-assured:$restAssuredVersion")
        api("io.rest-assured:json-path:$restAssuredVersion")
        api("io.rest-assured:spring-mock-mvc:$restAssuredVersion")

        // The following are needed to fix some dependency issues

        api("io.rest-assured:xml-path:$restAssuredVersion")
        api("io.rest-assured:json-schema-validator:$restAssuredVersion")

        api("org.codehaus.groovy:groovy:3.0.7")
        api("org.codehaus.groovy:groovy-xml:3.0.7")

    }
}
