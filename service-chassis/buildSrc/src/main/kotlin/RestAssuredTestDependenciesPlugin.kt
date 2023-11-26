import org.gradle.api.Plugin
import org.gradle.api.Project


abstract class AbstractRestAssuredDependenciesPlugin {
    abstract val configurationName: String


    fun apply(project: Project) {

        fun add(artifact : String) {
            project.dependencies.add(configurationName, artifact)
        }

        add("io.rest-assured:rest-assured")
        add("io.rest-assured:json-path")
        add("io.rest-assured:spring-mock-mvc")

        // The following are needed to fix some dependency issues

        add("io.rest-assured:xml-path")
        add("io.rest-assured:json-schema-validator")

        add("org.codehaus.groovy:groovy")
        add("org.codehaus.groovy:groovy-xml")

    }
}

class RestAssuredTestDependenciesPlugin : Plugin<Project>, AbstractRestAssuredDependenciesPlugin() {

    override val configurationName = "testImplementation"

}

class RestAssuredDependenciesPlugin : Plugin<Project>, AbstractRestAssuredDependenciesPlugin() {

    override val configurationName = "implementation"

}

