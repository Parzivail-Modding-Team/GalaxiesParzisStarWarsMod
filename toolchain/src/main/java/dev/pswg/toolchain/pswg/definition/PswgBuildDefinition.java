package dev.pswg.toolchain.pswg.definition;

import dev.pswg.toolchain.definition.BuildDefinition;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.ModuleSpec;

import java.util.List;

/**
 * Top-level PSWG build graph definition.
 */
public final class PswgBuildDefinition implements BuildDefinition
{
	/**
	 * Materializes the initial PSWG graph.
	 *
	 * @return the materialized graph
	 */
	@Override
	public BuildGraph define()
	{
		List<ModuleSpec> modules = List.of(
			new FrameworkDefinition().define(),
			new FrameworkGeneratorDefinition().define(),
			new PswgCoreDefinition().define(),
			new PswgBlastersDefinition().define()
		);

		return new BuildGraph(
			"pswg",
			"26.1-rc-1",
			"pswg_core",
			modules
		);
	}
}
