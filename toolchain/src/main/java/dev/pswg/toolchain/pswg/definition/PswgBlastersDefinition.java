package dev.pswg.toolchain.pswg.definition;

import dev.pswg.toolchain.definition.ClientCommonFabricModuleDefinition;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Definition for the PSWG blasters module.
 */
public final class PswgBlastersDefinition extends ClientCommonFabricModuleDefinition
{
	/**
	 * Gets the module identifier.
	 *
	 * @return the identifier
	 */
	@Override
	public String getId()
	{
		return "pswg_blasters";
	}

	/**
	 * Applies module-specific configuration.
	 *
	 * @param spec the mutable module specification
	 */
	@Override
	protected void configure(ModuleSpec spec)
	{
		spec.dependency("pswg_core");
		spec.annotationProcessor("framework-generator");
		spec.generatedSources(spec.paths().generatedAnnotationProcessorMain());
		spec.generatedClientSources(spec.paths().generatedAnnotationProcessorClient());
		spec.datagenOutput(spec.paths().generatedDatagen());
		spec.mixin(spec.paths().mainResource("pswg_blasters.mixins.json"));
		spec.mixin(spec.paths().clientResource("pswg_blasters.client.mixins.json"));
	}
}
