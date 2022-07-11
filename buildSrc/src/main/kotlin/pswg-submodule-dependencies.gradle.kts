import java.util.Collections
import java.util.EnumMap
import java.util.IdentityHashMap

plugins {
	id("java-library")
}

enum class ConfigurationType(val loomConfig: String, val gradleConfig: String) {
	API("modApi", "api"),
	COMPILE_ONLY("modCompileOnly", "compileOnly"),
	COMPILE_ONLY_API("modCompileOnlyApi", "compileOnlyApi"),
	IMPLEMENTATION("modImplementation", "implementation"),
	RUNTIME_ONLY("modRuntimeOnly", "runtimeOnly"),
}

fun computeType(consumerType: ConfigurationType, producerType: ConfigurationType) =
	when (producerType) {
		ConfigurationType.API -> consumerType
		ConfigurationType.IMPLEMENTATION, ConfigurationType.RUNTIME_ONLY -> when (consumerType) {
			ConfigurationType.COMPILE_ONLY, ConfigurationType.COMPILE_ONLY_API -> null
			else -> ConfigurationType.RUNTIME_ONLY
		}

		ConfigurationType.COMPILE_ONLY_API -> when (consumerType) {
			ConfigurationType.API, ConfigurationType.COMPILE_ONLY_API -> ConfigurationType.COMPILE_ONLY_API
			ConfigurationType.COMPILE_ONLY, ConfigurationType.IMPLEMENTATION -> ConfigurationType.COMPILE_ONLY
			else -> null
		}

		else -> null
	}

val visitedProjects: Map<ConfigurationType, MutableSet<Project>> = ConfigurationType.values()
	.associateWithTo(EnumMap(ConfigurationType::class.java)) { Collections.newSetFromMap(IdentityHashMap()) }

fun importFrom(dependencyProject: Project, relation: ConfigurationType): Boolean {
	if (dependencyProject in visitedProjects[relation]!! && dependencyProject !== project) return false
	visitedProjects[relation]!!.add(dependencyProject)
	dependencies {
		for (configType in ConfigurationType.values()) {
			val innerRelation = computeType(relation, configType)
			if (innerRelation != null) {
				for (dependency in dependencyProject.configurations[configType.gradleConfig]?.dependencies ?: setOf())
					if (dependency !is ProjectDependency || (dependency.targetConfiguration != "namedElements" || importFrom(
							dependency.dependencyProject,
							innerRelation
						))
					)
						innerRelation.gradleConfig(dependency.copy())
				for (dependency in dependencyProject.configurations[configType.loomConfig]?.dependencies ?: setOf())
					innerRelation.loomConfig(dependency.copy())
			}
		}
	}
	return true
}

afterEvaluate {
	for ((configType, dependencies) in ConfigurationType.values()
		.associateWithTo(EnumMap(ConfigurationType::class.java)) {
			project.configurations[it.gradleConfig]?.dependencies?.toSet() ?: setOf()
		})
		for (dependency in dependencies)
			if (dependency is ProjectDependency && dependency.targetConfiguration == "namedElements") {
				importFrom(dependency.dependencyProject, configType)
			}
}

tasks.assemble {
	this.dependsOn()
}
