package dev.pswg.toolchain.pswg;

import dev.pswg.toolchain.fabric.FabricDevLaunchService;
import dev.pswg.toolchain.intellij.IntelliJProjectSyncService;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.runtime.LaunchEnvironment;
import dev.pswg.toolchain.runtime.LaunchIdentity;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.util.ToolchainLog;

import java.io.IOException;

/**
 * High-level developer workflow helpers for the supported PSWG IntelliJ setup path.
 *
 * <p>This service exists so fresh-clone setup and day-to-day maintenance have one obvious entry
 * point. The lower-level `idea` and `fabric` commands still exist for diagnosis, but the normal
 * workflow is "synchronize IntelliJ metadata, then refresh the generated Fabric development launch".
 */
public final class PswgDevelopmentService
{
	/**
	 * The combined result of the supported IntelliJ setup workflow.
	 *
	 * @param clientLaunch the prepared Fabric client launch
	 * @param serverLaunch the prepared Fabric server launch
	 */
	public record SetupResult(
		VanillaLaunchConfig clientLaunch,
		VanillaLaunchConfig serverLaunch
	)
	{
	}

	/**
	 * Runs the supported IntelliJ-first setup workflow for the requested injected module.
	 *
	 * @param refresh whether external metadata and cached artifacts should be refreshed
	 * @param moduleId the optional requested injected module id
	 * @param identity the launch identity to embed in the generated run configuration
	 * @return the prepared client and server launch configurations
	 * @throws IOException if setup fails
	 */
	public SetupResult setupSupportedIntelliJDevelopment(
		boolean refresh,
		String moduleId,
		LaunchIdentity identity
	) throws IOException
	{
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		return setupSupportedIntelliJDevelopment(repository, refresh, moduleId, identity);
	}

	/**
	 * Runs the supported IntelliJ-first setup workflow for the requested injected module.
	 *
	 * @param repository the discovered PSWG repository context
	 * @param refresh whether external metadata and cached artifacts should be refreshed
	 * @param moduleId the optional requested injected module id
	 * @return the prepared client and server launch configurations
	 * @throws IOException if setup fails
	 */
	private SetupResult setupSupportedIntelliJDevelopment(
		PswgRepositoryContext repository,
		boolean refresh,
		String moduleId,
		LaunchIdentity identity
	) throws IOException
	{
		String effectiveModuleId = effectiveDevelopmentModuleId(repository.buildGraph(), moduleId);
		ToolchainLog.info("dev", "Synchronizing IntelliJ metadata for " + repository.projectName());
		new IntelliJProjectSyncService().syncPswgProject(refresh);

		FabricDevLaunchService launchService = new FabricDevLaunchService();
		ToolchainLog.info(
			"dev",
			"Preparing Fabric client launch for " + effectiveModuleId + " on Minecraft " + repository.minecraftVersion()
		);
		VanillaLaunchConfig clientLaunch = launchService.prepareLaunch(
			repository.minecraftVersion(),
			refresh,
			effectiveModuleId,
			LaunchEnvironment.CLIENT,
			identity
		);
		ToolchainLog.info(
			"dev",
			"Preparing Fabric server launch for " + effectiveModuleId + " on Minecraft " + repository.minecraftVersion()
		);
		VanillaLaunchConfig serverLaunch = launchService.prepareLaunch(
			repository.minecraftVersion(),
			refresh,
			effectiveModuleId,
			LaunchEnvironment.SERVER,
			LaunchIdentity.defaults()
		);
		return new SetupResult(clientLaunch, serverLaunch);
	}

	/**
	 * Resolves the effective module id for the supported development workflow.
	 *
	 * @param graph the authoritative build graph
	 * @param moduleId the optional requested module id
	 * @return the effective module id
	 */
	public static String effectiveDevelopmentModuleId(
		BuildGraph graph,
		String moduleId
	)
	{
		if (moduleId == null || moduleId.isBlank())
		{
			return graph.developmentModuleId();
		}

		return moduleId;
	}
}
