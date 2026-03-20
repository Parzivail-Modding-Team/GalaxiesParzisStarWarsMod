package dev.pswg.toolchain.definition;

import dev.pswg.toolchain.model.ModuleSpec;

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
	}
}
