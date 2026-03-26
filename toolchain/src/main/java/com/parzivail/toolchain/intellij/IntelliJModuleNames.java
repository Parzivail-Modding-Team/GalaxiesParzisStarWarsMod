package com.parzivail.toolchain.intellij;

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
	 * Builds the IntelliJ source-set module name for a module.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param moduleId      the logical module identifier
	 * @param sourceSetName the source-set name
	 *
	 * @return the IntelliJ module name
	 */
	public static String sourceSetModuleName(String projectName, String moduleId, String sourceSetName)
	{
		return projectName + ".projects." + moduleId + "." + sourceSetName;
	}

	/**
	 * Builds the IntelliJ module file name for a source-set module.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param moduleId      the logical module identifier
	 * @param sourceSetName the source-set name
	 *
	 * @return the `.iml` file name
	 */
	public static String sourceSetModuleFileName(String projectName, String moduleId, String sourceSetName)
	{
		return sourceSetModuleName(projectName, moduleId, sourceSetName) + ".iml";
	}

	/**
	 * Builds the IntelliJ module name for the standalone toolchain sources when they are registered
	 * into the root project.
	 *
	 * @param projectName the IntelliJ project name
	 *
	 * @return the toolchain module name
	 */
	public static String toolchainModuleName(String projectName)
	{
		return projectName + ".toolchain.main";
	}

	/**
	 * Builds the IntelliJ module file name for the standalone toolchain sources when they are
	 * registered into the root project.
	 *
	 * @param projectName the IntelliJ project name
	 *
	 * @return the toolchain module file name
	 */
	public static String toolchainModuleFileName(String projectName)
	{
		return toolchainModuleName(projectName) + ".iml";
	}

	/**
	 * Builds the IntelliJ module name for a generated Fabric launch classpath module.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param environmentId the launch environment identifier
	 * @param platformId    the target platform identifier
	 *
	 * @return the launch module name
	 */
	public static String fabricLaunchModuleName(String projectName, String environmentId, String platformId)
	{
		return projectName + ".launch.fabric." + environmentId + "." + platformId;
	}

	/**
	 * Builds the IntelliJ module file name for a generated Fabric launch classpath module.
	 *
	 * @param projectName   the IntelliJ project name
	 * @param environmentId the launch environment identifier
	 * @param platformId    the target platform identifier
	 *
	 * @return the launch module file name
	 */
	public static String fabricLaunchModuleFileName(String projectName, String environmentId, String platformId)
	{
		return fabricLaunchModuleName(projectName, environmentId, platformId) + ".iml";
	}

	/**
	 * Builds the IntelliJ module name for the aggregate Fabric datagen launch classpath module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param platformId  the target platform identifier
	 *
	 * @return the datagen launch module name
	 */
	public static String fabricDatagenLaunchModuleName(String projectName, String platformId)
	{
		return projectName + ".launch.fabric.datagen." + platformId;
	}

	/**
	 * Builds the IntelliJ module file name for the aggregate Fabric datagen launch classpath module.
	 *
	 * @param projectName the IntelliJ project name
	 * @param platformId  the target platform identifier
	 *
	 * @return the datagen launch module file name
	 */
	public static String fabricDatagenLaunchModuleFileName(String projectName, String platformId)
	{
		return fabricDatagenLaunchModuleName(projectName, platformId) + ".iml";
	}

	/**
	 * Builds the generated IntelliJ Fabric run-configuration file name for one environment and
	 * platform.
	 *
	 * @param environmentId the launch environment identifier
	 * @param platformId    the target platform identifier
	 *
	 * @return the run-configuration file name
	 */
	public static String fabricRunConfigurationFileName(String environmentId, String platformId)
	{
		return "Fabric_" + environmentId.substring(0, 1).toUpperCase(Locale.ROOT)
		       + environmentId.substring(1).toLowerCase(Locale.ROOT)
		       + "_" + platformId.toUpperCase(Locale.ROOT) + ".xml";
	}

	/**
	 * Builds the generated IntelliJ Fabric datagen run-configuration file name for one module and platform.
	 *
	 * @param moduleId   the logical module identifier
	 * @param platformId the target platform identifier
	 *
	 * @return the datagen run-configuration file name
	 */
	public static String fabricDatagenRunConfigurationFileName(String moduleId, String platformId)
	{
		return "Fabric_Datagen_" + moduleId + "_" + platformId.toUpperCase(Locale.ROOT) + ".xml";
	}
}
