rootProject.name = "service-template"


include("test-data")

include("domain")

include("config")
include("persistence")
include("web")
include("web-security")
include("service-chassis-health-check-tests")
include("metrics")
include("service-chassis-distributed-tracing-tests")

include("main")

pluginManagement {
    repositories {
        maven {
          url = uri(System.getenv("MAVEN_REPO_URL") ?: File(rootDir, "../build/repository"))
          if (System.getenv("MAVEN_REPO_USERNAME") != null)
            credentials {
                username = System.getenv("MAVEN_REPO_USERNAME")
                password = System.getenv("MAVEN_REPO_PASSWORD")
            }
        }
    }
}
