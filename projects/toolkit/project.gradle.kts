repositories {
	maven(url = "https://maven.wispforest.io/") {
		name = "WispForest"
	}
}

dependencies {
	"api"(project(":projects:pswg", configuration = "namedElements"))

	"modImplementation"("com.formdev:flatlaf:2.6")
	"include"("com.formdev:flatlaf:2.6")

	"modImplementation"("io.wispforest:worldmesher:0.2.13+1.19")
	"include"("io.wispforest:worldmesher:0.2.13+1.19")
}
