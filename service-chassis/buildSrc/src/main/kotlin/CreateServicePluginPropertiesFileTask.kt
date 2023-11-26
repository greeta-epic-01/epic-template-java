import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class CreateServicePluginPropertiesFileTask : DefaultTask() {

    @OutputFile
    val propFile = project.file("${project.buildDir}/generated/service.plugin.properties")

    @Input
    var pluginVersion: String = ""

    @Input
    var chassisRepo: String = ""

    @TaskAction
    fun generateFile() {
        project.mkdir(propFile.parent)
        propFile.writeText("""
version=$pluginVersion
chassisRepo=$chassisRepo
        """)
    }
}
