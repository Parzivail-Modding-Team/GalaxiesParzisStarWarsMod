package dev.pswg.toolchain.definition;

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
	}
}
