package com.parzivail.toolchain.fabric;

/**
 * Summary of the current repository's Fabric development launch contract.
 *
 * @param minecraftVersion          the repository Minecraft version
 * @param loaderVersion             the repository Fabric Loader version
 * @param defaultDevLaunchMainClass Loom's default dev-launch entrypoint
 * @param defaultRuntimeMainClass   Loom's fallback runtime main class for the inspected environment
 * @param currentIdeaMainClass      the current generated IntelliJ entrypoint
 * @param currentRuntimeMainClass   the current runtime main passed via fabric.dli.main
 * @param currentEnvironment        the current dev-launch environment
 * @param currentDliConfigPath      the current dev-launch config path
 * @param launchConfig              the parsed Loom launch configuration
 */
public record FabricDevLaunchSummary(
		String minecraftVersion,
		String loaderVersion,
		String defaultDevLaunchMainClass,
		String defaultRuntimeMainClass,
		String currentIdeaMainClass,
		String currentRuntimeMainClass,
		String currentEnvironment,
		String currentDliConfigPath,
		FabricDevLaunchConfig launchConfig
)
{
}
