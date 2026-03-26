package com.parzivail.toolchain.build;

import com.parzivail.toolchain.intellij.IntelliJModuleNames;

import java.nio.file.Path;

/**
 * Shared output-root naming used by IntelliJ-owned and bespoke compilation outputs.
 */
public final class CompilationOutputLayout
{
	/**
	 * Prevents construction.
	 */
	private CompilationOutputLayout()
	{
	}

	/**
	 * Resolves the output directory for one compiled module source set.
	 *
	 * @param outputRoot    the base output root
	 * @param projectName   the IntelliJ project name
	 * @param moduleId      the logical module identifier
	 * @param sourceSetName the source-set name
	 *
	 * @return the output directory
	 */
	public static Path sourceSetOutputRoot(
			Path outputRoot,
			String projectName,
			String moduleId,
			String sourceSetName
	)
	{
		return outputRoot.resolve(IntelliJModuleNames.sourceSetModuleName(projectName, moduleId, sourceSetName));
	}
}
