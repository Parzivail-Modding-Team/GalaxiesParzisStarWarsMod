package com.parzivail.toolchain;

import com.parzivail.toolchain.artifact.ArtifactAssemblyService;
import com.parzivail.toolchain.build.CiCompilationService;
import com.parzivail.toolchain.fabric.FabricDataGenerationService;
import com.parzivail.toolchain.fabric.FabricDevLaunchInspector;
import com.parzivail.toolchain.fabric.FabricDevLaunchService;
import com.parzivail.toolchain.intellij.IntelliJProjectSyncService;
import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.DevelopmentService;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.runtime.LaunchIdentity;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Entrypoint for the standalone toolchain.
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
	static void main(String[] args)
	{
		try
		{
			if (args.length > 0)
			{
				runCommand(args);
				return;
			}

			printOverview();
		}
		catch (IOException exception)
		{
			reportIoError(exception);
		}
	}

	/**
	 * Prints the high-level toolchain overview and supported quickstart path.
	 *
	 * @throws IOException if repository discovery fails
	 */
	private static void printOverview() throws IOException
	{
		var repository = RepositoryContext.load();
		System.out.println("Toolchain");
		System.out.println("Project: " + repository.projectName());
		System.out.println("Minecraft: " + repository.minecraftVersion());
		System.out.println("Supported workflow:");
		System.out.println("  dev setup-intellij [--refresh] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("Default development module: " + repository.buildGraph().developmentModuleId());
		System.out.println("This synchronizes IntelliJ metadata and refreshes the generated Fabric client, server, and datagen run configurations.");
		System.out.println();
		printUsage();
	}

	/**
	 * Executes a command-oriented toolchain entrypoint.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if command execution fails
	 */
	private static void runCommand(String[] args) throws IOException
	{
		switch (args[0])
		{
			case "dev" -> runDevelopmentCommand(args);
			case "mojang" -> runMojangCommand(args);
			case "fabric" -> runFabricCommand(args);
			case "idea" -> runIdeaCommand(args);
			case "artifacts" -> runArtifactCommand(args);
			default ->
			{
				printUsage();
				System.exit(1);
			}
		}
	}

	/**
	 * Executes the supported development workflow commands.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if setup fails
	 */
	private static void runDevelopmentCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "setup-intellij".equals(args[1]))
		{
			var repository = RepositoryContext.load();
			var refresh = hasFlag(args, "--refresh");
			var requestedModuleId = flagValue(args, "--module");
			var identity = resolveLaunchIdentity(args);
			var effectiveModuleId = DevelopmentService.effectiveDevelopmentModuleId(
					repository.buildGraph(),
					requestedModuleId
			);
			var setup = new DevelopmentService().setupIntelliJDevelopment(
					refresh,
					requestedModuleId,
					identity
			);
			var clientLaunch = setup.launch(LaunchEnvironment.CLIENT);
			var serverLaunch = setup.launch(LaunchEnvironment.SERVER);

			System.out.println("Supported IntelliJ development workflow is ready.");
			System.out.println("Minecraft: " + clientLaunch.versionId());
			System.out.println("Injected module: " + effectiveModuleId);
			System.out.println("Client username: " + identity.username());
			System.out.println("Client UUID: " + identity.uuid());
			System.out.println("Client working directory: " + clientLaunch.workingDirectory().toAbsolutePath());
			System.out.println("Server working directory: " + serverLaunch.workingDirectory().toAbsolutePath());
			System.out.println("Datagen configs: " + setup.datagenConfigurations().size());
			System.out.println("Next step: reload IntelliJ if needed, then run the generated Fabric Client, Fabric Server, or Fabric Datagen configuration.");
			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Executes Mojang metadata commands.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if metadata resolution fails
	 */
	private static void runMojangCommand(String[] args) throws IOException
	{
		var defaultVersion = RepositoryContext.load().minecraftVersion();
		var client = new MojangMetadataClient();
		var refresh = hasFlag(args, "--refresh");

		if (args.length >= 2 && "manifest".equals(args[1]))
		{
			var manifest = client.getVersionManifest(refresh);
			System.out.println("Manifest cached at: " + ToolchainPaths.MOJANG_VERSION_MANIFEST_FILE);
			System.out.println("Latest release: " + manifest.latest().release());
			System.out.println("Latest snapshot: " + manifest.latest().snapshot());
			System.out.println("Version count: " + manifest.versions().size());
			return;
		}

		if (args.length >= 2 && "version".equals(args[1]))
		{
			var versionId = positionalVersionArg(args, 2, defaultVersion);
			var entry = client.getVersion(versionId, refresh);
			var metadata = client.getVersionMetadata(versionId, refresh);

			System.out.println("Version: " + entry.id());
			System.out.println("Type: " + entry.type());
			System.out.println("Metadata URL: " + entry.url());
			System.out.println("Metadata cached at: " + ToolchainPaths.mojangVersionMetadataFile(versionId));
			System.out.println("Main class: " + metadata.mainClass());
			System.out.println("Assets: " + metadata.assetIndex().id());
			System.out.println("Libraries: " + metadata.libraries().size());
			System.out.println("Client download: " + metadata.downloads().client().url());
			return;
		}

		if (args.length >= 2 && "download".equals(args[1]))
		{
			var versionId = positionalVersionArg(args, 2, defaultVersion);
			var metadata = client.getVersionMetadata(versionId, refresh);

			System.out.println("Version: " + versionId);
			System.out.println("Client jar: " + client.downloadClientJar(versionId, refresh));
			System.out.println("Asset index: " + client.downloadAssetIndex(versionId, refresh));
			System.out.println("Assets id: " + metadata.assetIndex().id());
			return;
		}

		if (args.length >= 2 && "runtime".equals(args[1]))
		{
			var versionId = positionalVersionArg(args, 2, defaultVersion);
			var result = client.downloadRuntime(versionId, refresh);

			System.out.println("Version: " + versionId);
			System.out.println("Libraries downloaded: " + result.libraryCount());
			System.out.println("Asset objects downloaded: " + result.assetObjectCount());
			System.out.println("Libraries root: " + result.librariesRoot());
			System.out.println("Assets root: " + result.assetsObjectsRoot());
			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Executes Fabric inspection and launch-preparation commands.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if inspection fails
	 */
	private static void runFabricCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "inspect-dev".equals(args[1]))
		{
			var environment = resolveLaunchEnvironment(args);
			var summary = new FabricDevLaunchInspector().inspect(environment);

			System.out.println("Minecraft: " + summary.minecraftVersion());
			System.out.println("Fabric Loader: " + summary.loaderVersion());
			System.out.println("Environment: " + environment.id());
			System.out.println("Default DLI main: " + summary.defaultDevLaunchMainClass());
			System.out.println("Default runtime main fallback: " + summary.defaultRuntimeMainClass());
			System.out.println("Current IntelliJ main: " + summary.currentIdeaMainClass());
			System.out.println("Current fabric.dli.main: " + summary.currentRuntimeMainClass());
			System.out.println("Current fabric.dli.env: " + summary.currentEnvironment());
			System.out.println("Current fabric.dli.config: " + summary.currentDliConfigPath());
			System.out.println("launch.cfg sections:");

			for (var entry : summary.launchConfig().sections().entrySet())
			{
				System.out.println(" - " + entry.getKey() + ": " + entry.getValue().size() + " entries");
			}

			return;
		}

		if (args.length >= 2 && "prepare-dev".equals(args[1]))
		{
			var defaultVersion = RepositoryContext.load().minecraftVersion();
			var versionId = positionalVersionArg(args, 2, defaultVersion);
			var refresh = hasFlag(args, "--refresh");
			var environment = resolveLaunchEnvironment(args);
			var moduleId = flagValue(args, "--module");
			var identity = resolveLaunchIdentity(args);
			var config = new FabricDevLaunchService().prepareLaunch(
					versionId,
					refresh,
					moduleId,
					environment,
					identity
			);

			System.out.println("Version: " + config.versionId());
			System.out.println("Environment: " + environment.id());
			System.out.println("Main class: " + config.mainClass());
			System.out.println("Working directory: " + config.workingDirectory().toAbsolutePath());
			System.out.println("Assets root: " + config.assetsRoot().toAbsolutePath());
			if (moduleId != null)
			{
				System.out.println("Injected module: " + moduleId);
			}
			if (environment.isClient())
			{
				System.out.println("Username: " + identity.username());
				System.out.println("UUID: " + identity.uuid());
			}
			System.out.println("DLI config is written beside the launch bundle.");
			return;
		}

		if (args.length >= 2 && "prepare-datagen".equals(args[1]))
		{
			var defaultVersion = RepositoryContext.load().minecraftVersion();
			var versionId = positionalVersionArg(args, 2, defaultVersion);
			var refresh = hasFlag(args, "--refresh");
			var moduleId = flagValue(args, "--module");
			var identity = resolveLaunchIdentity(args);
			var configurations = new FabricDataGenerationService()
					.prepareRunConfigurations(
							versionId,
							refresh,
							moduleId,
							identity
					);

			System.out.println("Version: " + versionId);
			System.out.println("Datagen configs generated: " + configurations.size());

			for (var configuration : configurations)
			{
				System.out.println(" - " + configuration.moduleId() + " -> " + configuration.outputDirectory().toAbsolutePath());
			}

			System.out.println("Client username: " + identity.username());
			System.out.println("Client UUID: " + identity.uuid());
			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Executes IntelliJ metadata generation commands.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if metadata generation fails
	 */
	private static void runIdeaCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "sync-project".equals(args[1]))
		{
			var refresh = hasFlag(args, "--refresh");
			new IntelliJProjectSyncService().syncProject(refresh);
			System.out.println("Synchronized IntelliJ compiler metadata into the project repo.");
			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Executes artifact assembly commands.
	 *
	 * @param args command line arguments
	 *
	 * @throws IOException if artifact assembly fails
	 */
	private static void runArtifactCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "assemble".equals(args[1]))
		{
			var moduleId = flagValue(args, "--module");
			var ciBuild = hasFlag(args, "--ci-build");
			Path compiledOutputRoot = null;

			if (ciBuild)
			{
				compiledOutputRoot = new CiCompilationService().compileArtifactInputs(moduleId, hasFlag(args, "--refresh"));
			}

			var artifacts = new ArtifactAssemblyService().assemble(moduleId, compiledOutputRoot);

			System.out.println("Assembled artifacts: " + artifacts.size());

			for (var artifact : artifacts)
			{
				System.out.println(" - " + artifact.artifactId() + " -> " + artifact.outputJar().toAbsolutePath());
			}

			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Reports a top-level I/O failure.
	 *
	 * @param exception the failure to report
	 */
	private static void reportIoError(IOException exception)
	{
		System.err.println("I/O error: " + exception.getMessage());

		if (exception.getCause() != null && exception.getCause().getMessage() != null)
		{
			System.err.println("Cause: " + exception.getCause().getMessage());
		}

		System.exit(1);
	}

	/**
	 * Checks whether a flag is present in the argument list.
	 *
	 * @param args the command line arguments
	 * @param flag the flag to search for
	 *
	 * @return {@code true} if the flag is present
	 */
	private static boolean hasFlag(String[] args, String flag)
	{
		for (var arg : args)
		{
			if (flag.equals(arg))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Resolves the value following a named flag.
	 *
	 * @param args the command line arguments
	 * @param flag the flag to search for
	 *
	 * @return the following value, or {@code null}
	 */
	private static String flagValue(String[] args, String flag)
	{
		for (var i = 0; i < args.length - 1; i++)
		{
			if (flag.equals(args[i]))
			{
				return args[i + 1];
			}
		}

		return null;
	}

	/**
	 * Resolves the launch identity from CLI flags, falling back to the default development identity.
	 *
	 * @param args the command line arguments
	 *
	 * @return the resolved launch identity
	 */
	private static LaunchIdentity resolveLaunchIdentity(String[] args)
	{
		var username = flagValue(args, "--username");
		var uuid = flagValue(args, "--uuid");

		return new LaunchIdentity(
				username == null || username.isBlank() ? LaunchIdentity.DEFAULT_USERNAME : username,
				uuid == null || uuid.isBlank() ? LaunchIdentity.DEFAULT_UUID : uuid
		);
	}

	/**
	 * Resolves the requested launch environment from CLI flags.
	 *
	 * @param args the command line arguments
	 *
	 * @return the resolved launch environment
	 */
	private static LaunchEnvironment resolveLaunchEnvironment(String[] args)
	{
		return LaunchEnvironment.fromId(flagValue(args, "--environment"));
	}

	/**
	 * Resolves an optional positional version argument, falling back to the default version.
	 *
	 * @param args           the command line arguments
	 * @param index          the version argument index
	 * @param defaultVersion the default Minecraft version
	 *
	 * @return the resolved version identifier
	 */
	private static String positionalVersionArg(String[] args, int index, String defaultVersion)
	{
		if (args.length > index && !args[index].startsWith("--"))
		{
			return args[index];
		}

		return defaultVersion;
	}

	/**
	 * Prints the supported command usage.
	 */
	private static void printUsage()
	{
		System.out.println("Commands:");
		System.out.println("  dev setup-intellij [--refresh] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("    Supported workflow. Synchronizes IntelliJ metadata and refreshes the generated Fabric client, server, and datagen launches.");
		System.out.println("  idea sync-project [--refresh]");
		System.out.println("    Low-level IntelliJ metadata generation.");
		System.out.println("  fabric prepare-dev [id] [--refresh] [--environment <client|server>] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("    Low-level Fabric launch generation.");
		System.out.println("  fabric prepare-datagen [id] [--refresh] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("    Generate module-scoped Fabric datagen run configurations.");
		System.out.println("  fabric inspect-dev [--environment <client|server>]");
		System.out.println("    Inspect the generated Fabric launch contract.");
		System.out.println("  artifacts assemble [--module <id>] [--ci-build] [--refresh]");
		System.out.println("    Assemble local project artifact jars from IntelliJ outputs, or compile into a toolchain-owned output tree first with --ci-build.");
		System.out.println("  mojang manifest [--refresh]");
		System.out.println("  mojang version [id] [--refresh]");
		System.out.println("  mojang download [id] [--refresh]");
		System.out.println("  mojang runtime [id] [--refresh]");
	}
}
