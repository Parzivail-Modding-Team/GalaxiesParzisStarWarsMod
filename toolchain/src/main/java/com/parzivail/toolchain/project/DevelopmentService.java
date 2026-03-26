package com.parzivail.toolchain.project;

import com.parzivail.toolchain.fabric.FabricDataGenerationService;
import com.parzivail.toolchain.fabric.FabricDevLaunchService;
import com.parzivail.toolchain.intellij.IntelliJProjectSyncService;
import com.parzivail.toolchain.model.BuildGraph;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.runtime.LaunchIdentity;
import com.parzivail.toolchain.runtime.VanillaLaunchConfig;
import com.parzivail.toolchain.util.ToolchainLog;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * High-level developer workflow helpers for the supported IntelliJ setup path.
 *
 * <p>This service keeps fresh-clone setup and day-to-day maintenance behind one obvious command:
 * synchronize IntelliJ metadata, then refresh the generated Fabric development launches.
 */
public final class DevelopmentService
{
	/**
	 * The combined result of the supported IntelliJ setup workflow.
	 *
	 * @param launches              the prepared client and server launches
	 * @param datagenConfigurations the prepared datagen configurations
	 */
	public record SetupResult(
			Map<LaunchEnvironment, VanillaLaunchConfig> launches,
			List<FabricDataGenerationService.DatagenConfiguration> datagenConfigurations
	)
	{
		/**
		 * Gets the prepared launch for one environment.
		 *
		 * @param environment the launch environment
		 *
		 * @return the prepared launch
		 */
		public VanillaLaunchConfig launch(LaunchEnvironment environment)
		{
			var launch = launches.get(environment);

			if (launch == null)
			{
				throw new IllegalArgumentException("Missing prepared launch for " + environment.id());
			}

			return launch;
		}
	}

	/**
	 * Runs the supported IntelliJ-first setup workflow for the requested injected module.
	 *
	 * @param refresh  whether external metadata and cached artifacts should be refreshed
	 * @param moduleId the optional requested injected module id
	 * @param identity the launch identity to embed in the generated run configurations
	 *
	 * @return the prepared launch and datagen configurations
	 *
	 * @throws IOException if setup fails
	 */
	public SetupResult setupIntelliJDevelopment(
			boolean refresh,
			String moduleId,
			LaunchIdentity identity
	) throws IOException
	{
		var repository = RepositoryContext.load();
		return setupIntelliJDevelopment(repository, refresh, moduleId, identity);
	}

	/**
	 * Runs the supported IntelliJ-first setup workflow for the requested injected module.
	 *
	 * @param repository the discovered repository context
	 * @param refresh    whether external metadata and cached artifacts should be refreshed
	 * @param moduleId   the optional requested injected module id
	 * @param identity   the launch identity to embed in the generated run configurations
	 *
	 * @return the prepared launch and datagen configurations
	 *
	 * @throws IOException if setup fails
	 */
	private SetupResult setupIntelliJDevelopment(
			RepositoryContext repository,
			boolean refresh,
			String moduleId,
			LaunchIdentity identity
	) throws IOException
	{
		var effectiveModuleId = effectiveDevelopmentModuleId(repository.buildGraph(), moduleId);
		ToolchainLog.info("dev", "Synchronizing IntelliJ metadata for " + repository.projectName());
		new IntelliJProjectSyncService().syncProject(refresh);
		var launchService = new FabricDevLaunchService();
		var datagenService = new FabricDataGenerationService();
		Map<LaunchEnvironment, VanillaLaunchConfig> launches = new EnumMap<>(LaunchEnvironment.class);

		for (var environment : LaunchEnvironment.values())
		{
			ToolchainLog.info(
					"dev",
					"Preparing Fabric " + environment.displayName().toLowerCase() + " launch for " + effectiveModuleId + " on Minecraft " + repository.minecraftVersion()
			);
			launches.put(
					environment,
					launchService.prepareLaunch(
							repository.minecraftVersion(),
							refresh,
							effectiveModuleId,
							environment,
							environment.effectiveIdentity(identity)
					)
			);
		}

		ToolchainLog.info("dev", "Preparing Fabric datagen launches");
		var datagenConfigurations = datagenService.prepareRunConfigurations(
				repository.minecraftVersion(),
				refresh,
				null,
				identity
		);

		return new SetupResult(Map.copyOf(launches), List.copyOf(datagenConfigurations));
	}

	/**
	 * Resolves the effective module id for the supported development workflow.
	 *
	 * @param graph    the authoritative build graph
	 * @param moduleId the optional requested module id
	 *
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
