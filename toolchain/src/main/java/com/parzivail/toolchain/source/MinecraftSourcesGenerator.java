package com.parzivail.toolchain.source;

import com.parzivail.toolchain.mojang.MojangMetadataClient;
import com.parzivail.toolchain.mojang.model.MojangVersionMetadata;
import com.parzivail.toolchain.mojang.model.MojangVersionMetadataLibrary;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.util.ToolchainLog;

import net.fabricmc.fernflower.api.IFabricJavadocProvider;

import org.jetbrains.java.decompiler.main.Fernflower;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates cached source archives for Minecraft jars so IntelliJ can show sources for transformed
 * compile jars.
 */
public final class MinecraftSourcesGenerator
{
	/**
	 * The Mojang metadata client.
	 */
	private final MojangMetadataClient _mojangClient;

	/**
	 * The optional Parchment mappings resolver.
	 */
	private final ParchmentMappingsResolver _parchmentMappingsResolver;

	/**
	 * Creates a generator with default collaborators.
	 */
	public MinecraftSourcesGenerator()
	{
		_mojangClient = new MojangMetadataClient();
		_parchmentMappingsResolver = new ParchmentMappingsResolver();
	}

	/**
	 * Generates or reuses the source archive for one Minecraft jar.
	 *
	 * @param minecraftJar the Minecraft jar to document
	 * @param refresh whether cache refresh was requested
	 * @return the generated source archive, or {@code null} when the jar is not a Minecraft jar
	 * @throws IOException if source generation fails
	 */
	public Path generateSources(Path minecraftJar, boolean refresh) throws IOException
	{
		MinecraftJarIdentity identity = identify(minecraftJar);

		if (identity == null)
		{
			return null;
		}

		Path output = identity.sourcesArchive();

		if (!refresh
			&& Files.isRegularFile(output)
			&& Files.getLastModifiedTime(output).compareTo(Files.getLastModifiedTime(minecraftJar)) >= 0)
		{
			return output;
		}

		Files.createDirectories(output.getParent());
		ToolchainLog.info("sources", "Generating Minecraft sources " + output.getFileName());

		MojangVersionMetadata metadata = _mojangClient.getVersionMetadata(identity.version(), refresh);
		List<Path> libraries = resolveLibraries(metadata, refresh);
		ResolvedParchmentMappings parchmentMappings = _parchmentMappingsResolver.resolveMappings(identity.version(), refresh);
		Map<String, Object> options = new LinkedHashMap<>();
		populateOptions(options, parchmentMappings);
		MinecraftSourceArchiveSaver saver = new MinecraftSourceArchiveSaver(output);
		Fernflower fernflower = new Fernflower(saver, options, new MinecraftFernflowerLogger());

		for (Path library : libraries)
		{
			fernflower.addLibrary(library.toFile());
		}

		fernflower.addSource(identity.inputJar().toFile());

		try
		{
			fernflower.decompileContext();
		}
		finally
		{
			fernflower.clearContext();
		}

		return output;
	}

	/**
	 * Populates the Vineflower option map, including optional Parchment javadoc support.
	 *
	 * @param options the mutable options map
	 * @param parchmentMappings the resolved Parchment mapping inputs, or {@code null}
	 */
	private static void populateOptions(
		Map<String, Object> options,
		ResolvedParchmentMappings parchmentMappings
	)
	{
		options.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
		options.put(IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1");
		options.put(IFernflowerPreferences.DUMP_CODE_LINES, "1");
		options.put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1");
		options.put(IFernflowerPreferences.REMOVE_BRIDGE, "1");
		options.put(IFernflowerPreferences.LOG_LEVEL, "warn");
		options.put(IFernflowerPreferences.THREADS, String.valueOf(Math.max(1, Runtime.getRuntime().availableProcessors())));
		options.put(IFernflowerPreferences.INDENT_STRING, "\t");

		if (parchmentMappings != null)
		{
			options.put(
				IFabricJavadocProvider.PROPERTY_NAME,
				new ParchmentJavadocProvider(parchmentMappings.parchmentJsonFile().toFile())
			);
		}
	}

	/**
	 * Resolves the non-native Minecraft libraries needed for decompilation.
	 *
	 * @param metadata the Minecraft version metadata
	 * @param refresh whether cache refresh was requested
	 * @return the decompiler classpath libraries
	 * @throws IOException if a library cannot be downloaded
	 */
	private List<Path> resolveLibraries(MojangVersionMetadata metadata, boolean refresh) throws IOException
	{
		List<Path> libraries = new ArrayList<>();

		for (MojangVersionMetadataLibrary library : metadata.libraries())
		{
			if (!_mojangClient.isLibraryAllowed(library))
			{
				continue;
			}

			if (library.name() != null && library.name().contains(":natives-"))
			{
				continue;
			}

			if (library.downloads() == null || library.downloads().artifact() == null || library.downloads().artifact().path() == null)
			{
				continue;
			}

			Path target = ToolchainPaths.mojangLibraryFile(library.downloads().artifact().path());
			_mojangClient.download(java.net.URI.create(library.downloads().artifact().url()), target, refresh);
			libraries.add(target);
		}

		return libraries;
	}

	/**
	 * Identifies whether one jar path is a Minecraft jar that should have generated sources.
	 *
	 * @param minecraftJar the candidate jar path
	 * @return the identified jar metadata, or {@code null}
	 */
	private MinecraftJarIdentity identify(Path minecraftJar)
	{
		if (!minecraftJar.getFileName().toString().endsWith(".jar"))
		{
			return null;
		}

		Path parent = minecraftJar.getParent();

		if (parent == null)
		{
			return null;
		}

		String fileName = minecraftJar.getFileName().toString();
		String stem = fileName.substring(0, fileName.length() - 4);

		if (".intellij-transformed".equals(parent.getParent() == null ? null : parent.getParent().getFileName().toString()))
		{
			String version = parent.getFileName().toString();
			return new MinecraftJarIdentity(version, minecraftJar, parent.resolve(stem + "-sources.jar"));
		}

		Path versionDirectory = parent;
		Path versionsRoot = versionDirectory.getParent();

		if (versionsRoot == null || !"versions".equals(versionsRoot.getFileName().toString()))
		{
			return null;
		}

		String version = versionDirectory.getFileName().toString();

		if ("client.jar".equals(fileName) || "server-extracted.jar".equals(fileName) || "server.jar".equals(fileName))
		{
			return new MinecraftJarIdentity(version, minecraftJar, parent.resolve(stem + "-sources.jar"));
		}

		return null;
	}

	/**
	 * Identified Minecraft jar metadata.
	 *
	 * @param version the Minecraft version
	 * @param inputJar the input jar
	 * @param sourcesArchive the generated sources archive path
	 */
	private record MinecraftJarIdentity(
		String version,
		Path inputJar,
		Path sourcesArchive
	)
	{
	}
}
