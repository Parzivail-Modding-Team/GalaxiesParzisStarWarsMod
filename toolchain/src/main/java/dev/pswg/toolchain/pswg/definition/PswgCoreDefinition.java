package dev.pswg.toolchain.pswg.definition;

import dev.pswg.toolchain.definition.ClientCommonFabricModuleDefinition;
import dev.pswg.toolchain.maven.ToolchainMavenRepositories;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Definition for the first modeled PSWG runtime module.
 */
public final class PswgCoreDefinition extends ClientCommonFabricModuleDefinition
{
	/**
	 * Gets the module identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getId()
	{
		return "pswg_core";
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
		spec.annotationProcessor("framework-generator");
		spec.runtimeDependency("net.fabricmc.fabric-api:fabric-api:${fabric_version}", ToolchainMavenRepositories.FABRIC);
		spec.generatedSources(spec.paths().generatedAnnotationProcessorMain());
		spec.datagenOutput(spec.paths().generatedDatagen());
		spec.mixin(spec.paths().mainResource("pswg.mixins.json"));
		spec.mixin(spec.paths().mainResource("errorman.mixins.json"));
		spec.mixin(spec.paths().clientResource("pswg.client.mixins.json"));
	}
}
