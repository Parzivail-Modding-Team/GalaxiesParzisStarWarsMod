package dev.pswg.toolchain;

import dev.pswg.toolchain.definition.BuildDefinition;
import dev.pswg.toolchain.definition.PswgBuildDefinition;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.ModuleSpec;

/**
 * Entrypoint for the standalone PSWG toolchain.
 */
public final class Main
{
	/**
	 * Prevents construction.
	 */
	private Main()
	{
	}

	/**
	 * Starts the toolchain process.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args)
	{
		BuildDefinition definition = new PswgBuildDefinition();
		BuildGraph graph = definition.define();

		System.out.println("PSWG Toolchain Bootstrap");
		System.out.println("Project: " + graph.projectId());
		System.out.println("Minecraft: " + graph.minecraftVersion());
		System.out.println("Modules:");

		for (ModuleSpec module : graph.modules())
		{
			System.out.println(" - " + module.id() + " @ " + module.paths().root());
		}
	}
}
