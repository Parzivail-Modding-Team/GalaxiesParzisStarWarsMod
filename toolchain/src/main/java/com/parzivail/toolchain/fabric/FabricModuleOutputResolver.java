package com.parzivail.toolchain.fabric;

import com.parzivail.toolchain.intellij.IntelliJModuleNames;
import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.model.SourceSetNames;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.util.ToolchainLog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves IntelliJ-compiled output roots for modeled modules participating in Fabric workflows.
 */
public final class FabricModuleOutputResolver
{
	/**
	 * Prevents construction.
	 */
	private FabricModuleOutputResolver()
	{
	}

	/**
	 * Resolves the IntelliJ output roots for one module.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param module        the module specification
	 * @param includeClient whether client outputs should be included
	 * @param logCategory   the toolchain log category
	 * @param logMissing    whether missing outputs should be logged
	 *
	 * @return the ordered output roots
	 */
	public static List<Path> resolveOutputRoots(
			String projectName,
			ModuleSpec module,
			boolean includeClient,
			String logCategory,
			boolean logMissing
	)
	{
		List<Path> roots = new ArrayList<>(resolveSourceSetOutputRoots(projectName, module, SourceSetNames.MAIN, logCategory, logMissing));

		if (includeClient)
		{
			roots.addAll(resolveSourceSetOutputRoots(projectName, module, SourceSetNames.CLIENT, logCategory, logMissing));
		}

		return roots.stream().distinct().toList();
	}

	/**
	 * Resolves the IntelliJ output root for one module source set.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param module        the module specification
	 * @param sourceSetName the source set name
	 * @param logCategory   the toolchain log category
	 * @param logMissing    whether missing outputs should be logged
	 *
	 * @return the ordered output roots
	 */
	public static List<Path> resolveSourceSetOutputRoots(
			String projectName,
			ModuleSpec module,
			String sourceSetName,
			String logCategory,
			boolean logMissing
	)
	{
		var moduleName = IntelliJModuleNames.sourceSetModuleName(projectName, module.id(), sourceSetName);
		var intellijOutput = ToolchainPaths.INTELLIJ_OUTPUT_DIRECTORY.resolve(moduleName);

		if (Files.isDirectory(intellijOutput))
		{
			ToolchainLog.info(logCategory, "Using IntelliJ output for " + module.id() + "." + sourceSetName + ": " + intellijOutput);
			return List.of(intellijOutput);
		}

		if (logMissing)
		{
			ToolchainLog.info(
					logCategory,
					"No IntelliJ output found for " + module.id() + "." + sourceSetName + ". Build the generated Fabric development configuration in IntelliJ first."
			);
		}

		return List.of();
	}
}
