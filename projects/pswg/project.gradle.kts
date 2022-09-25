loom {
	accessWidenerPath.set(file("src/main/resources/pswg.accesswidener"))
}

val cca_version: String by project.ext
val cloth_config_version: String by project.ext
val modmenu_version: String by project.ext
val rei_version: String by project.ext
val libzoomer_version: String by project.ext
val trinkets_version: String by project.ext
val gravity_changer_version: String by project.ext
val iris_version: String by project.ext

dependencies {
	// CCA Base
	"modImplementation"("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
	"include"("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${cca_version}")
	// CCA Entity
	"modImplementation"("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")
	"include"("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${cca_version}")

	// Cloth Config
	"modImplementation"("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}") {
		exclude(group = "net.fabricmc.fabric-api")
	}
	"include"("me.shedaniel.cloth:cloth-config-fabric:${cloth_config_version}")

	// Mod Menu
	"modImplementation"("com.terraformersmc:modmenu:${modmenu_version}")

	// Roughly Enough Items
	"modCompileOnly"("me.shedaniel:RoughlyEnoughItems-api-fabric:${rei_version}")
	"modRuntimeOnly"("me.shedaniel:RoughlyEnoughItems-fabric:${rei_version}")
	"modRuntimeOnly"("dev.architectury:architectury-fabric:6.2.46") // TODO: remove once REI gets their stuff together

	// LibZoomer
	"modImplementation"("com.parzivail.internal:LibZoomer:${libzoomer_version}")
	"include"("com.parzivail.internal:LibZoomer:${libzoomer_version}")

	// Trinkets
	"modImplementation"("dev.emi:trinkets:${trinkets_version}")
	"include"("dev.emi:trinkets:${trinkets_version}")

	// Gravity Changer
	"modCompileOnly"("maven.modrinth:gravity-api:${gravity_changer_version}")
	//include("maven.modrinth:fusions-gravity-api:${gravity_changer_version}")

	// Iris
	//	"modImplementation"("maven.modrinth:iris:${iris_version}")
}
