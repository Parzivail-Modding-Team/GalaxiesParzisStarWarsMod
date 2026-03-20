package dev.pswg.toolchain.definition;

import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Definition for the framework annotation module.
 */
public final class FrameworkDefinition extends JavaModuleDefinition
{
	/**
	 * Gets the module identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getId()
	{
		return "framework";
	}

	/**
	 * Applies module-specific configuration.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void configure(ModuleSpec spec)
	{
	}
}
