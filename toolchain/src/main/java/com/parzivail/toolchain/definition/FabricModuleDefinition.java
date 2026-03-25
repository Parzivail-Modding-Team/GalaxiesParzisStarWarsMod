package com.parzivail.toolchain.definition;

import com.parzivail.toolchain.maven.ToolchainMavenCoordinates;
import com.parzivail.toolchain.maven.ToolchainMavenRepositories;
import com.parzivail.toolchain.model.ModuleSpec;

/**
 * Base class for Fabric module conventions.
 */
public abstract class FabricModuleDefinition extends JavaModuleDefinition
{
	/**
	 * Applies Fabric defaults.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void applyDefaults(ModuleSpec spec)
	{
		super.applyDefaults(spec);
		spec.fabricModJson(spec.paths().mainResource("fabric.mod.json"));
		spec.fabricModId(spec.id());
		spec.compileDependency(
			ToolchainMavenCoordinates.FABRIC_API,
			ToolchainMavenRepositories.FABRIC
		);
		spec.runtimeDependency(
			ToolchainMavenCoordinates.FABRIC_API,
			ToolchainMavenRepositories.FABRIC
		);
		spec.compileDependency(
			ToolchainMavenCoordinates.JETBRAINS_ANNOTATIONS,
			ToolchainMavenRepositories.MAVEN_CENTRAL
		);
	}
}
