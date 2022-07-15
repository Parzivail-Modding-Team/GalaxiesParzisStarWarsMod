dependencies {
    "api"(project(":projects:pswg", configuration = "namedElements"))
}

val cloth_config_version: String by project.ext
val trinkets_version: String by project.ext

dependencies {
    // Cloth Config
    "modImplementation"("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    "include"("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}")

    // Trinkets
    "modImplementation"("dev.emi:trinkets:${trinkets_version}")
    "include"("dev.emi:trinkets:${trinkets_version}")
}
