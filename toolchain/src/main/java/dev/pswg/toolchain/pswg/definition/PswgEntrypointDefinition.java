package dev.pswg.toolchain.pswg.definition;

import dev.pswg.toolchain.definition.ResourceOnlyFabricModuleDefinition;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Definition for the PSWG bundle entrypoint module.
 */
public final class PswgEntrypointDefinition extends ResourceOnlyFabricModuleDefinition
{
	/**
	 * Gets the module identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getId()
	{
		return "pswg_entrypoint";
	}

	/**
	 * Applies module-specific configuration.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void configure(ModuleSpec spec)
	{
		spec.artifactId("pswg");
		spec.fabricModId("pswg_bundle");
		spec.aggregateMember("pswg_blasters");
		spec.aggregateMember("pswg_gadgets");
	}
}
