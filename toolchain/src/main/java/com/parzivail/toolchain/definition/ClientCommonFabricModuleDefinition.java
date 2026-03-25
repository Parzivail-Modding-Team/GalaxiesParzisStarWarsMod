package com.parzivail.toolchain.definition;

import com.parzivail.toolchain.model.ModuleSpec;

/**
 * Base class for Fabric modules that use shared main and client source roots.
 */
public abstract class ClientCommonFabricModuleDefinition extends FabricModuleDefinition
{
	/**
	 * Applies client/common module defaults.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void applyDefaults(ModuleSpec spec)
	{
		super.applyDefaults(spec);
		spec.clientSources(spec.paths().clientJava());
		spec.clientResources(spec.paths().clientResources());
	}
}
