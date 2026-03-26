package com.parzivail.toolchain.project;

import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.path.ModulePaths;

/**
 * Base class for a module definition that contributes a passive module spec.
 */
public abstract class ModuleDefinition
{
	/**
	 * Materializes this definition into a passive module specification.
	 *
	 * @return the materialized module specification
	 */
	public final ModuleSpec define()
	{
		ModuleSpec spec = new ModuleSpec(getId(), createPaths());
		applyDefaults(spec);
		configure(spec);
		return spec;
	}

	/**
	 * Gets the unique module identifier.
	 *
	 * @return the module identifier
	 */
	public abstract String getId();

	/**
	 * Creates the path helper for this module.
	 *
	 * @return the module path helper
	 */
	protected ModulePaths createPaths()
	{
		// TODO: make configurable
		return new ModulePaths("projects/" + getId());
	}

	/**
	 * Applies inherited defaults to the module specification.
	 *
	 * @param spec the mutable module specification
	 */
	protected void applyDefaults(ModuleSpec spec)
	{
	}

	/**
	 * Applies module-specific declarations to the specification.
	 *
	 * @param spec the mutable module specification
	 */
	protected abstract void configure(ModuleSpec spec);
}
