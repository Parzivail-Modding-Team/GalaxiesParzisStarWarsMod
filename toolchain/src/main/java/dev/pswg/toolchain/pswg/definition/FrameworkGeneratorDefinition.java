package dev.pswg.toolchain.pswg.definition;

import dev.pswg.toolchain.definition.JavaModuleDefinition;
import dev.pswg.toolchain.maven.ToolchainMavenCoordinates;
import dev.pswg.toolchain.maven.ToolchainMavenRepositories;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Definition for the framework annotation processor module.
 */
public final class FrameworkGeneratorDefinition extends JavaModuleDefinition
{
	/**
	 * Gets the module identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getId()
	{
		return "framework-generator";
	}

	/**
	 * Applies module-specific configuration.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void configure(ModuleSpec spec)
	{
		spec.dependency("framework");
		spec.compileDependency(ToolchainMavenCoordinates.JAVAPOET, ToolchainMavenRepositories.MAVEN_CENTRAL);
		spec.compileDependency(ToolchainMavenCoordinates.AUTO_SERVICE_ANNOTATIONS, ToolchainMavenRepositories.MAVEN_CENTRAL);
		for (String notation : ToolchainMavenCoordinates.AUTO_SERVICE_PROCESSOR_PATH)
		{
			spec.annotationProcessorDependency(notation, ToolchainMavenRepositories.MAVEN_CENTRAL);
		}
		spec.generatedSources(spec.paths().generatedAnnotationProcessorMain());
	}
}
