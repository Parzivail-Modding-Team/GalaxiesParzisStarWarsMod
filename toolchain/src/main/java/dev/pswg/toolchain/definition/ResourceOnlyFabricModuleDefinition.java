package dev.pswg.toolchain.definition;

import dev.pswg.toolchain.maven.ToolchainMavenCoordinates;
import dev.pswg.toolchain.maven.ToolchainMavenRepositories;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Base class for Fabric modules that only contribute resource roots.
 */
public abstract class ResourceOnlyFabricModuleDefinition extends ModuleDefinition
{
	/**
	 * Applies resource-only Fabric defaults.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void applyDefaults(ModuleSpec spec)
	{
		spec.javaVersion(25);
		spec.mainResources(spec.paths().mainResources());
		spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
		spec.compileDependency(
			ToolchainMavenCoordinates.FABRIC_API,
			ToolchainMavenRepositories.FABRIC
		);
		spec.runtimeDependency(
			ToolchainMavenCoordinates.FABRIC_API,
			ToolchainMavenRepositories.FABRIC
		);
	}
}
