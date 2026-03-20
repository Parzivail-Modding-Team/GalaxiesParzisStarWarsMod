package dev.pswg.toolchain.definition;

import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Base class for Java-only module conventions.
 */
public abstract class JavaModuleDefinition extends ModuleDefinition
{
	/**
	 * Applies Java module defaults.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void applyDefaults(ModuleSpec spec)
	{
		spec.javaVersion(25);
		spec.mainSources(spec.paths().mainJava());
		spec.mainResources(spec.paths().mainResources());
	}
}
