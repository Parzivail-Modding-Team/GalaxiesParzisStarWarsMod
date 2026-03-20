package dev.pswg.toolchain;

import dev.pswg.toolchain.definition.BuildDefinition;
import dev.pswg.toolchain.definition.PswgBuildDefinition;
import dev.pswg.toolchain.model.BuildGraph;
import dev.pswg.toolchain.model.ModuleSpec;
import dev.pswg.toolchain.mojang.MojangMetadataClient;
import dev.pswg.toolchain.mojang.model.MojangVersionManifest;
import dev.pswg.toolchain.mojang.model.MojangVersionManifestEntry;
import dev.pswg.toolchain.mojang.model.MojangVersionMetadata;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.runtime.VanillaLaunchService;

import java.io.IOException;

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
		if (args.length > 0)
		{
			runCommand(args);
			return;
		}

		BuildDefinition definition = new PswgBuildDefinition();
		BuildGraph graph = definition.define();

		System.out.println("PSWG Toolchain Bootstrap");
		System.out.println("Project: " + graph.projectId());
		System.out.println("Minecraft: " + graph.minecraftVersion());
		System.out.println("Modules:");

		for (ModuleSpec module : graph.modules())
		{
			System.out.println(" - " + module.id() + " @ " + module.paths().root());
		}
	}

	/**
	 * Executes a command-oriented toolchain entrypoint.
	 *
	 * @param args command line arguments
	 */
	private static void runCommand(String[] args)
	{
		try
		{
			if ("mojang".equals(args[0]))
			{
				runMojangCommand(args);
				return;
			}

			if ("vanilla".equals(args[0]))
			{
				runVanillaCommand(args);
				return;
			}

			printUsage();
			System.exit(1);
		}
		catch (IOException exception)
		{
			System.err.println("I/O error: " + exception.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Executes Mojang metadata commands.
	 *
	 * @param args command line arguments
	 * @throws IOException if metadata resolution fails
	 */
	private static void runMojangCommand(String[] args) throws IOException
	{
		String defaultVersion = new PswgBuildDefinition().define().minecraftVersion();
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
	 * Executes vanilla client preparation commands.
	 *
	 * @param args command line arguments
	 * @throws IOException if launch preparation fails
	 */
	private static void runVanillaCommand(String[] args) throws IOException
	{
		String defaultVersion = new PswgBuildDefinition().define().minecraftVersion();
		boolean refresh = hasFlag(args, "--refresh");

		if (args.length >= 2 && "prepare-ij".equals(args[1]))
		{
			String versionId = positionalVersionArg(args, 2, defaultVersion);
			VanillaLaunchConfig config = new VanillaLaunchService().prepareIntelliJLaunch(versionId, refresh);

			System.out.println("Version: " + config.versionId());
			System.out.println("Main class: " + config.mainClass());
			System.out.println("Launch config: " + config.workingDirectory().getParent().resolve("launch.json").toAbsolutePath());
			System.out.println("Game directory: " + config.gameDirectory().toAbsolutePath());
			System.out.println("Assets root: " + config.assetsRoot().toAbsolutePath());
			System.out.println("Natives directory: " + config.nativesDirectory().toAbsolutePath());
			return;
		}

		printUsage();
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
		System.out.println("Usage:");
		System.out.println("  mojang manifest [--refresh]");
		System.out.println("  mojang version [id] [--refresh]");
		System.out.println("  mojang download [id] [--refresh]");
		System.out.println("  mojang runtime [id] [--refresh]");
		System.out.println("  vanilla prepare-ij [id] [--refresh]");
	}
}
