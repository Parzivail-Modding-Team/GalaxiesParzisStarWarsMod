package dev.pswg.toolchain.intellij;

/**
 * Shared IntelliJ module and metadata naming conventions used by the toolchain.
 */
public final class IntelliJModuleNames
{
	/**
	 * Prevents construction.
	 */
	private IntelliJModuleNames()
	{
	}

	/**
	 * Builds the IntelliJ source-set module name for a PSWG module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param moduleId the logical PSWG module identifier
	 * @param sourceSetName the source-set name
	 * @return the IntelliJ module name
	 */
	public static String sourceSetModuleName(String projectName, String moduleId, String sourceSetName)
	{
		return projectName + ".projects." + moduleId + "." + sourceSetName;
	}

	/**
	 * Builds the IntelliJ module file name for a PSWG source-set module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param moduleId the logical PSWG module identifier
	 * @param sourceSetName the source-set name
	 * @return the `.iml` file name
	 */
	public static String sourceSetModuleFileName(String projectName, String moduleId, String sourceSetName)
	{
		return sourceSetModuleName(projectName, moduleId, sourceSetName) + ".iml";
	}
}
