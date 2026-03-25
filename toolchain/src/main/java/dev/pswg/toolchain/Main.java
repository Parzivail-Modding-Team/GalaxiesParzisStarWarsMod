package dev.pswg.toolchain;

import dev.pswg.toolchain.artifact.ArtifactAssemblyService;
import dev.pswg.toolchain.build.CiCompilationService;
import dev.pswg.toolchain.fabric.FabricDataGenerationService;
import dev.pswg.toolchain.fabric.FabricDevLaunchInspector;
import dev.pswg.toolchain.fabric.FabricDevLaunchService;
import dev.pswg.toolchain.fabric.FabricDevLaunchSummary;
import dev.pswg.toolchain.intellij.IntelliJProjectSyncService;
import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.model.MojangVersionManifest;
import dev.pswg.toolchain.mojang.model.MojangVersionManifestEntry;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.pswg.PswgDevelopmentService;
import dev.pswg.toolchain.pswg.PswgRepositoryContext;
import dev.pswg.toolchain.runtime.LaunchEnvironment;
import dev.pswg.toolchain.runtime.LaunchIdentity;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
		PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
		System.out.println("PSWG Toolchain");
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
	 * @throws IOException if command execution fails
	 */
	private static void runCommand(String[] args) throws IOException
	{
		switch (args[0])
		{
			case "dev" ->
			{
				runDevelopmentCommand(args);
				return;
			}

			case "mojang" ->
			{
				runMojangCommand(args);
				return;
			}

			case "fabric" ->
			{
				runFabricCommand(args);
				return;
			}

			case "idea" ->
			{
				runIdeaCommand(args);
				return;
			}

			case "artifacts" ->
			{
				runArtifactCommand(args);
				return;
			}

			default ->
			{
				printUsage();
				System.exit(1);
			}
		}
	}

	/**
	 * Executes the supported PSWG development workflow commands.
	 *
	 * @param args command line arguments
	 * @throws IOException if setup fails
	 */
	private static void runDevelopmentCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "setup-intellij".equals(args[1]))
		{
			PswgRepositoryContext repository = PswgRepositoryContext.discoverFromToolchainWorkingDirectory();
			boolean refresh = hasFlag(args, "--refresh");
			String requestedModuleId = flagValue(args, "--module");
			LaunchIdentity identity = resolveLaunchIdentity(args);
			String effectiveModuleId = PswgDevelopmentService.effectiveDevelopmentModuleId(
				repository.buildGraph(),
				requestedModuleId
			);
			PswgDevelopmentService.SetupResult setup = new PswgDevelopmentService().setupSupportedIntelliJDevelopment(
				refresh,
				requestedModuleId,
				identity
			);
			VanillaLaunchConfig clientLaunch = setup.launch(LaunchEnvironment.CLIENT);
			VanillaLaunchConfig serverLaunch = setup.launch(LaunchEnvironment.SERVER);

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
	 * @throws IOException if metadata resolution fails
	 */
	private static void runMojangCommand(String[] args) throws IOException
	{
		String defaultVersion = PswgRepositoryContext.discoverFromToolchainWorkingDirectory().minecraftVersion();
		MojangMetadataClient client = new MojangMetadataClient();
		boolean refresh = hasFlag(args, "--refresh");

		if (args.length >= 2 && "manifest".equals(args[1]))
		{
			MojangVersionManifest manifest = client.getVersionManifest(refresh);
			System.out.println("Manifest cached at: " + client.paths().versionManifestFile());
			System.out.println("Latest release: " + manifest.latest().release());
			System.out.println("Latest snapshot: " + manifest.latest().snapshot());
			System.out.println("Version count: " + manifest.versions().size());
			return;
		}

		if (args.length >= 2 && "version".equals(args[1]))
		{
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			MojangVersionManifestEntry entry = client.getVersion(versionId, refresh);
			MojangVersionMetadata metadata = client.getVersionMetadata(versionId, refresh);

			System.out.println("Version: " + entry.id());
			System.out.println("Type: " + entry.type());
			System.out.println("Metadata URL: " + entry.url());
			System.out.println("Metadata cached at: " + client.paths().versionMetadataFile(versionId));
			System.out.println("Main class: " + metadata.mainClass());
			System.out.println("Assets: " + metadata.assetIndex().id());
			System.out.println("Libraries: " + metadata.libraries().size());
			System.out.println("Client download: " + metadata.downloads().client().url());
			return;
		}

		if (args.length >= 2 && "download".equals(args[1]))
		{
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			MojangVersionMetadata metadata = client.getVersionMetadata(versionId, refresh);

			System.out.println("Version: " + versionId);
			System.out.println("Client jar: " + client.downloadClientJar(versionId, refresh));
			System.out.println("Asset index: " + client.downloadAssetIndex(versionId, refresh));
			System.out.println("Assets id: " + metadata.assetIndex().id());
			return;
		}

		if (args.length >= 2 && "runtime".equals(args[1]))
		{
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			MojangMetadataClient.RuntimeDownloadResult result = client.downloadRuntime(versionId, refresh);

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
	 * @throws IOException if inspection fails
	 */
	private static void runFabricCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "inspect-dev".equals(args[1]))
		{
			LaunchEnvironment environment = resolveLaunchEnvironment(args);
			FabricDevLaunchSummary summary = new FabricDevLaunchInspector().inspect(environment);

			System.out.println("Minecraft: " + summary.minecraftVersion());
			System.out.println("Fabric Loader: " + summary.loaderVersion());
			System.out.println("Fabric API: " + summary.fabricApiVersion());
			System.out.println("Loom: " + summary.loomVersion());
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
			String defaultVersion = PswgRepositoryContext.discoverFromToolchainWorkingDirectory().minecraftVersion();
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			boolean refresh = hasFlag(args, "--refresh");
			LaunchEnvironment environment = resolveLaunchEnvironment(args);
			String moduleId = flagValue(args, "--module");
			LaunchIdentity identity = resolveLaunchIdentity(args);
			VanillaLaunchConfig config = new FabricDevLaunchService().prepareLaunch(
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
			String defaultVersion = PswgRepositoryContext.discoverFromToolchainWorkingDirectory().minecraftVersion();
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			boolean refresh = hasFlag(args, "--refresh");
			String moduleId = flagValue(args, "--module");
			LaunchIdentity identity = resolveLaunchIdentity(args);
			List<FabricDataGenerationService.DatagenConfiguration> configurations = new FabricDataGenerationService()
				.prepareRunConfigurations(
					versionId,
					refresh,
					moduleId,
					identity
				);

			System.out.println("Version: " + versionId);
			System.out.println("Datagen configs generated: " + configurations.size());

			for (FabricDataGenerationService.DatagenConfiguration configuration : configurations)
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
	 * @throws IOException if metadata generation fails
	 */
	private static void runIdeaCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "sync-pswg".equals(args[1]))
		{
			boolean refresh = hasFlag(args, "--refresh");
			new IntelliJProjectSyncService().syncPswgProject(refresh);
			System.out.println("Synchronized IntelliJ compiler metadata into the PSWG repo.");
			return;
		}

		printUsage();
		System.exit(1);
	}

	/**
	 * Executes artifact assembly commands.
	 *
	 * @param args command line arguments
	 * @throws IOException if artifact assembly fails
	 */
	private static void runArtifactCommand(String[] args) throws IOException
	{
		if (args.length >= 2 && "assemble".equals(args[1]))
		{
			String moduleId = flagValue(args, "--module");
			boolean ciBuild = hasFlag(args, "--ci-build");
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
	 * @return {@code true} if the flag is present
	 */
	private static boolean hasFlag(String[] args, String flag)
	{
		for (String arg : args)
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
	 * @return the following value, or {@code null}
	 */
	private static String flagValue(String[] args, String flag)
	{
		for (int i = 0; i < args.length - 1; i++)
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
	 * @return the resolved launch identity
	 */
	private static LaunchIdentity resolveLaunchIdentity(String[] args)
	{
		String username = flagValue(args, "--username");
		String uuid = flagValue(args, "--uuid");

		return new LaunchIdentity(
			username == null || username.isBlank() ? LaunchIdentity.DEFAULT_USERNAME : username,
			uuid == null || uuid.isBlank() ? LaunchIdentity.DEFAULT_UUID : uuid
		);
	}

	/**
	 * Resolves the requested launch environment from CLI flags.
	 *
	 * @param args the command line arguments
	 * @return the resolved launch environment
	 */
	private static LaunchEnvironment resolveLaunchEnvironment(String[] args)
	{
		return LaunchEnvironment.fromId(flagValue(args, "--environment"));
	}

	/**
	 * Resolves an optional positional version argument, falling back to the PSWG default version.
	 *
	 * @param args the command line arguments
	 * @param index the version argument index
	 * @param defaultVersion the default PSWG Minecraft version
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
		System.out.println("  idea sync-pswg [--refresh]");
		System.out.println("    Low-level IntelliJ metadata generation.");
		System.out.println("  fabric prepare-dev [id] [--refresh] [--environment <client|server>] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("    Low-level Fabric launch generation.");
		System.out.println("  fabric prepare-datagen [id] [--refresh] [--module <id>] [--username <name>] [--uuid <uuid>]");
		System.out.println("    Generate module-scoped Fabric datagen run configurations.");
		System.out.println("  fabric inspect-dev [--environment <client|server>]");
		System.out.println("    Inspect the generated Fabric launch contract.");
		System.out.println("  artifacts assemble [--module <id>] [--ci-build] [--refresh]");
		System.out.println("    Assemble local PSWG artifact jars from IntelliJ outputs, or compile into a toolchain-owned output tree first with --ci-build.");
		System.out.println("  mojang manifest [--refresh]");
		System.out.println("  mojang version [id] [--refresh]");
		System.out.println("  mojang download [id] [--refresh]");
		System.out.println("  mojang runtime [id] [--refresh]");
	}
}
