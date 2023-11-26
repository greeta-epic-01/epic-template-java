buildscript {
    extra.apply {
        set("kotlin_version", "1.6.10")
    }

    repositories {
        mavenCentral()
    }

}

plugins {
    id("net.chrisrichardson.liveprojects.servicechassis.plugins.ServiceProjectPlugin") version "0.0.1-SNAPSHOT"
}

allprojects {
    group = "net.chrisrichardson.liveprojects.servicetemplate"
    version = "0.0.1-SNAPSHOT"
}
