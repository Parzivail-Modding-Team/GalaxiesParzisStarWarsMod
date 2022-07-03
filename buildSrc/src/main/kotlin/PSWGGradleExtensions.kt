import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.getByName

fun Project.loom(configure: Action<LoomGradleExtensionAPI>) =
	extensions.configure("loom", configure)

val ExtensionAware.ext
	get() = extensions.getByName<ExtraPropertiesExtension>("ext")