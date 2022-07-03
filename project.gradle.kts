dependencies {
	"api"(project(":core", configuration = "namedElements"))
	"include"(project(":core"))
}

open class CITask : DefaultTask()

tasks.register<CITask>("ci") {
	dependsOn(tasks["build"])
}