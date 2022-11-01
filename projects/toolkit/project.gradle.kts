repositories {
	maven(url = "https://maven.wispforest.io/") {
		name = "WispForest"
	}
}

dependencies {
	"api"(project(":projects:pswg", configuration = "namedElements"))

	"modImplementation"("com.formdev:flatlaf:2.6")
	"include"("com.formdev:flatlaf:2.6")

	"modImplementation"("com.jetbrains.intellij.java:java-gui-forms-rt:222.4345.14")
	"include"("com.jetbrains.intellij.java:java-gui-forms-rt:222.4345.14")

	"modImplementation"("io.wispforest:worldmesher:0.2.14+1.19")
	"include"("io.wispforest:worldmesher:0.2.14+1.19")
	//	"modImplementation"(files("/home/cnewman/IdeaProjects/worldmesher/build/libs/worldmesher-0.2.13+build.11+1.19.jar"))
	//	"include"(files("/home/cnewman/IdeaProjects/worldmesher/build/libs/worldmesher-0.2.13+build.11+1.19.jar"))
}
