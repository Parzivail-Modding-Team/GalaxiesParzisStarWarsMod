package dev.pswg.toolchain.intellij;

import java.util.Locale;

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

	/**
	 * Builds the IntelliJ module name for the standalone toolchain sources when they are registered
	 * into the PSWG root project.
	 *
	 * @param projectName the IntelliJ project name
	 * @return the toolchain module name
	 */
	public static String toolchainModuleName(String projectName)
	{
		return projectName + ".toolchain.main";
	}

	/**
	 * Builds the IntelliJ module file name for the standalone toolchain sources when they are
	 * registered into the PSWG root project.
	 *
	 * @param projectName the IntelliJ project name
	 * @return the toolchain module file name
	 */
	public static String toolchainModuleFileName(String projectName)
	{
		return toolchainModuleName(projectName) + ".iml";
	}

	/**
	 * Builds the IntelliJ module name for a generated Fabric launch classpath module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param platformId the target platform identifier
	 * @return the launch module name
	 */
	public static String fabricLaunchModuleName(String projectName, String platformId)
	{
		return projectName + ".launch.fabric." + platformId;
	}

	/**
	 * Builds the IntelliJ module file name for a generated Fabric launch classpath module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param platformId the target platform identifier
	 * @return the launch module file name
	 */
	public static String fabricLaunchModuleFileName(String projectName, String platformId)
	{
		return fabricLaunchModuleName(projectName, platformId) + ".iml";
	}

	/**
	 * Builds the generated IntelliJ Fabric client run-configuration file name for one platform.
	 *
	 * @param platformId the target platform identifier
	 * @return the run-configuration file name
	 */
	public static String fabricClientRunConfigurationFileName(String platformId)
	{
		return "Fabric_Client_" + platformId.toUpperCase(Locale.ROOT) + ".xml";
	}
}
