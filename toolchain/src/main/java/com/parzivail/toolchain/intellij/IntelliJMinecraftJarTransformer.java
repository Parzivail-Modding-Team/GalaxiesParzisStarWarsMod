package com.parzivail.toolchain.intellij;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parzivail.toolchain.util.DigestUtilities;
import com.parzivail.toolchain.util.ToolchainLog;
import net.fabricmc.classtweaker.api.ClassTweaker;
import net.fabricmc.classtweaker.api.ClassTweakerReader;
import net.fabricmc.classtweaker.api.ClassTweakerWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * Materializes IntelliJ-facing Minecraft compile jars after applying transitive class tweakers from
 * Fabric compile dependencies.
 *
 * <p>This is the compile-time parity layer that replaces the old "Loom already transformed the
 * Minecraft jar for us" assumption. IntelliJ should see the same widened and interface-injected
 * Minecraft signatures that Gradle/Loom used to hand it implicitly.
 */
public final class IntelliJMinecraftJarTransformer
{
	/**
	 * The shared JSON mapper used to read `fabric.mod.json`.
	 */
	private final ObjectMapper _mapper;

	/**
	 * Creates a new IntelliJ Minecraft jar transformer.
	 */
	public IntelliJMinecraftJarTransformer()
	{
		_mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Applies all discovered dependency and local-module class tweakers to the supplied Minecraft
	 * compile jar.
	 *
	 * @param minecraftVersion    the tracked Minecraft version
	 * @param minecraftJar        the raw Minecraft compile jar
	 * @param modArtifacts        the mod jars whose transitive class tweakers should be applied
	 * @param localFabricModJsons the local Fabric mod metadata files whose injected interfaces should
	 *                            also be reflected in the compile jar
	 *
	 * @return the transformed Minecraft compile jar, or the original jar when no tweaks are present
	 *
	 * @throws IOException if discovery or transformation fails
	 */
	public Path transformMinecraftJar(
			String minecraftVersion,
			Path minecraftJar,
			Collection<Path> modArtifacts,
			Collection<Path> localFabricModJsons
	) throws IOException
	{
		List<ClassTweakerEntry> classTweakers = new ArrayList<>();
		classTweakers.addAll(discoverClassTweakers(modArtifacts));
		classTweakers.addAll(discoverLocalInjectedInterfaces(localFabricModJsons));
		classTweakers = classTweakers.stream()
		                             .sorted(Comparator.comparing(ClassTweakerEntry::sortKey))
		                             .toList();

		if (classTweakers.isEmpty())
		{
			ToolchainLog.info("transform", "No class tweaker inputs discovered for " + minecraftVersion + "; using raw Minecraft jar");
			return minecraftJar;
		}

		var outputPath = transformedJarPath(minecraftVersion, minecraftJar, classTweakers);
		ToolchainLog.info(
				"transform",
				"Discovered " + classTweakers.size() + " class tweaker inputs for " + minecraftVersion
		);

		if (isUsableTransformedJar(outputPath))
		{
			ToolchainLog.info("transform", "Reusing transformed Minecraft jar " + outputPath.getFileName());
			return outputPath;
		}

		deleteExistingCacheTarget(outputPath);

		var classTweaker = ClassTweaker.newInstance();

		for (var entry : classTweakers)
		{
			ToolchainLog.info("transform", "Applying class tweaker input " + entry.sortKey());
			ClassTweakerReader.create(classTweaker).read(entry.content(), entry.modId());
		}

		ToolchainLog.info("transform", "Writing transformed Minecraft jar to " + outputPath);
		writeTransformedJar(minecraftJar, outputPath, classTweaker);
		ToolchainLog.info("transform", "Finished transformed Minecraft jar " + outputPath.getFileName());
		return outputPath;
	}

	/**
	 * Checks whether a cached transformed jar can actually be consumed.
	 *
	 * <p>Mounted filesystems may report a ghost entry as existing after an interrupted write. Treat
	 * those as cache misses so the jar is rebuilt instead of being handed to later IntelliJ metadata
	 * steps as if it were valid.
	 *
	 * @param outputPath the candidate transformed jar path
	 *
	 * @return whether the cached jar is usable
	 */
	private boolean isUsableTransformedJar(Path outputPath)
	{
		if (!Files.isRegularFile(outputPath))
		{
			return false;
		}

		try (var ignored = new JarFile(outputPath.toFile()))
		{
			return true;
		}
		catch (IOException ignored)
		{
			return false;
		}
	}

	/**
	 * Discovers class tweaker declarations from dependency Fabric mod jars.
	 *
	 * @param modArtifacts the candidate mod artifacts
	 *
	 * @return the discovered class tweaker entries
	 *
	 * @throws IOException if a mod jar cannot be inspected
	 */
	private List<ClassTweakerEntry> discoverClassTweakers(Collection<Path> modArtifacts) throws IOException
	{
		List<ClassTweakerEntry> entries = new ArrayList<>();
		Set<Path> inspectedArtifacts = new LinkedHashSet<>(modArtifacts);
		ToolchainLog.info("transform", "Scanning " + inspectedArtifacts.size() + " candidate mod artifacts for class tweakers");

		for (var artifact : inspectedArtifacts)
		{
			if (!artifact.getFileName().toString().endsWith(".jar"))
			{
				continue;
			}

			try (var jarFile = new JarFile(artifact.toFile()))
			{
				var fabricModJsonEntry = jarFile.getJarEntry("fabric.mod.json");

				if (fabricModJsonEntry == null)
				{
					continue;
				}

				var fabricModMetadata = readFabricModMetadata(jarFile, fabricModJsonEntry);

				if (fabricModMetadata.accessWidener() == null || fabricModMetadata.accessWidener().isBlank())
				{
					continue;
				}

				var classTweakerEntry = jarFile.getJarEntry(fabricModMetadata.accessWidener());

				if (classTweakerEntry == null)
				{
					continue;
				}

				entries.add(new ClassTweakerEntry(
						fabricModMetadata.id(),
						artifact,
						fabricModMetadata.accessWidener(),
						readBytes(jarFile, classTweakerEntry)
				));
			}
		}

		return entries;
	}

	/**
	 * Synthesizes class tweaker inputs from local `loom:injected_interfaces` declarations.
	 *
	 * @param localFabricModJsons the local Fabric mod metadata files
	 *
	 * @return the synthesized class tweaker entries
	 *
	 * @throws IOException if a metadata file cannot be read
	 */
	private List<ClassTweakerEntry> discoverLocalInjectedInterfaces(Collection<Path> localFabricModJsons) throws IOException
	{
		List<ClassTweakerEntry> entries = new ArrayList<>();

		for (var fabricModJsonPath : new LinkedHashSet<>(localFabricModJsons))
		{
			if (!Files.exists(fabricModJsonPath))
			{
				continue;
			}

			var root = _mapper.readTree(Files.newInputStream(fabricModJsonPath));
			var injectedInterfacesNode = root.path("custom").path("loom:injected_interfaces");

			if (injectedInterfacesNode.isMissingNode() || !injectedInterfacesNode.isObject())
			{
				continue;
			}

			var modId = root.path("id").asText("unknown");
			var writer = ClassTweakerWriter.create(3);
			writer.visitHeader("official");
			var hasEntries = false;

			for (var fields = injectedInterfacesNode.fields(); fields.hasNext(); )
			{
				var entry = fields.next();
				var className = entry.getKey();
				var interfacesNode = entry.getValue();

				if (!interfacesNode.isArray())
				{
					continue;
				}

				for (var interfaceNode : interfacesNode)
				{
					if (!interfaceNode.isTextual())
					{
						continue;
					}

					writer.visitInjectedInterface(className, interfaceNode.asText(), false);
					hasEntries = true;
				}
			}

			if (hasEntries)
			{
				entries.add(new ClassTweakerEntry(
						modId,
						fabricModJsonPath,
						"loom:injected_interfaces",
						writer.getOutputAsString().getBytes(StandardCharsets.UTF_8)
				));
			}
		}

		return entries;
	}

	/**
	 * Reads minimal Fabric mod metadata from a jar.
	 *
	 * @param jarFile            the containing jar
	 * @param fabricModJsonEntry the `fabric.mod.json` entry
	 *
	 * @return the parsed Fabric mod metadata
	 *
	 * @throws IOException if the metadata cannot be read
	 */
	private FabricModMetadata readFabricModMetadata(JarFile jarFile, JarEntry fabricModJsonEntry) throws IOException
	{
		try (var inputStream = jarFile.getInputStream(fabricModJsonEntry))
		{
			return _mapper.readValue(inputStream, FabricModMetadata.class);
		}
	}

	/**
	 * Writes a transformed Minecraft jar to the IntelliJ transform cache.
	 *
	 * @param inputJar     the raw Minecraft compile jar
	 * @param outputJar    the transformed jar target
	 * @param classTweaker the loaded class tweaker rules
	 *
	 * @throws IOException if the transformed jar cannot be written
	 */
	private void writeTransformedJar(
			Path inputJar,
			Path outputJar,
			ClassTweaker classTweaker
	) throws IOException
	{
		Files.createDirectories(outputJar.getParent());
		var temporaryOutput = Files.createTempFile("minecraft-transform-", ".jar.part");
		var targets = classTweaker.getTargets();

		try
		{
			try (var jarFile = new JarFile(inputJar.toFile());
			     var fileOutputStream = Files.newOutputStream(temporaryOutput);
			     var outputStream = new JarOutputStream(fileOutputStream))
			{
				var entries = jarFile.entries();

				while (entries.hasMoreElements())
				{
					var inputEntry = entries.nextElement();
					var outputEntry = new JarEntry(inputEntry.getName());
					outputEntry.setTime(inputEntry.getTime());
					outputStream.putNextEntry(outputEntry);

					if (!inputEntry.isDirectory())
					{
						var content = readBytes(jarFile, inputEntry);

						if (inputEntry.getName().endsWith(".class"))
						{
							var className = inputEntry.getName().substring(0, inputEntry.getName().length() - 6);

							if (targets.contains(className))
							{
								content = transformClass(classTweaker, className, content);
							}
						}

						outputStream.write(content);
					}

					outputStream.closeEntry();
				}
			}

			finalizeTransformedJar(temporaryOutput, outputJar);
		}
		finally
		{
			Files.deleteIfExists(temporaryOutput);
		}
	}

	/**
	 * Finalizes a freshly written transformed jar into its cache location.
	 *
	 * <p>Workspace-mounted filesystems in the sandbox have proven unreliable for `move`-based
	 * replacement here, even when the temporary jar was already fully written. A copy-and-delete
	 * finalize step is slower, but it is predictable across the sandbox and the user's Windows
	 * workspace.
	 *
	 * @param temporaryOutput the completed temporary jar
	 * @param outputJar       the cache target
	 *
	 * @throws IOException if the cache target cannot be finalized
	 */
	private void finalizeTransformedJar(
			Path temporaryOutput,
			Path outputJar
	) throws IOException
	{
		deleteExistingCacheTarget(outputJar);
		Files.copy(temporaryOutput, outputJar);
		Files.deleteIfExists(outputJar.resolveSibling(outputJar.getFileName() + ".part"));
		Files.deleteIfExists(temporaryOutput);
	}

	/**
	 * Removes any pre-existing cache target before writing a replacement jar.
	 *
	 * <p>The mounted workspace filesystem can leave behind broken target entries after interrupted
	 * writes. Deleting the target first is more reliable here than expecting `REPLACE_EXISTING` to
	 * recover from that state.
	 *
	 * @param outputJar the cache target
	 *
	 * @throws IOException if an existing target cannot be removed
	 */
	private void deleteExistingCacheTarget(Path outputJar) throws IOException
	{
		try
		{
			Files.deleteIfExists(outputJar);
		}
		catch (IOException exception)
		{
			throw new IOException("Failed to clear existing transformed jar target: " + outputJar, exception);
		}
	}

	/**
	 * Transforms one targeted class with the loaded class tweaker rules.
	 *
	 * @param classTweaker the loaded class tweaker
	 * @param className    the internal JVM class name
	 * @param input        the original class bytes
	 *
	 * @return the transformed class bytes
	 */
	private byte[] transformClass(ClassTweaker classTweaker, String className, byte[] input)
	{
		var reader = new ClassReader(input);

		if (!reader.getClassName().equals(className))
		{
			throw new IllegalStateException("Class name mismatch: expected " + className + " but found " + reader.getClassName());
		}

		var writer = new ClassWriter(0);
		var classVisitor = classTweaker.createClassVisitor(Opcodes.ASM9, writer, null);
		reader.accept(classVisitor, 0);
		return writer.toByteArray();
	}

	/**
	 * Computes a stable transformed-jar path for a Minecraft version plus class tweaker set.
	 *
	 * @param minecraftVersion the tracked Minecraft version
	 * @param minecraftJar     the raw Minecraft compile jar
	 * @param classTweakers    the discovered class tweaker entries
	 *
	 * @return the transformed jar cache path
	 *
	 * @throws IOException if the cache key cannot be computed
	 */
	private Path transformedJarPath(
			String minecraftVersion,
			Path minecraftJar,
			List<ClassTweakerEntry> classTweakers
	) throws IOException
	{
		var cacheKey = cacheKey(classTweakers);
		var cacheDirectory = minecraftJar.getParent()
		                                 .resolve(".intellij-transformed")
		                                 .resolve(minecraftVersion);
		var jarName = minecraftJar.getFileName().toString();
		var stem = jarName.endsWith(".jar") ? jarName.substring(0, jarName.length() - 4) : jarName;

		return cacheDirectory.resolve(stem + "-" + cacheKey + ".jar");
	}

	/**
	 * Computes a stable cache key for a set of class tweaker inputs.
	 *
	 * @param classTweakers the discovered class tweaker entries
	 *
	 * @return the cache key
	 *
	 * @throws IOException if hashing is unavailable
	 */
	private String cacheKey(List<ClassTweakerEntry> classTweakers) throws IOException
	{
		try
		{
			var digest = MessageDigest.getInstance("SHA-1");

			for (var classTweaker : classTweakers)
			{
				digest.update(classTweaker.sortKey().getBytes(StandardCharsets.UTF_8));
				digest.update((byte)'\n');
				digest.update(classTweaker.content());
			}

			var bytes = digest.digest();
			return DigestUtilities.formatHex(bytes);
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new IOException("SHA-1 hashing is not available", exception);
		}
	}

	/**
	 * Reads a jar entry into memory.
	 *
	 * @param jarFile  the containing jar
	 * @param jarEntry the entry to read
	 *
	 * @return the entry bytes
	 *
	 * @throws IOException if the entry cannot be read
	 */
	private byte[] readBytes(JarFile jarFile, JarEntry jarEntry) throws IOException
	{
		try (var inputStream = jarFile.getInputStream(jarEntry))
		{
			return inputStream.readAllBytes();
		}
	}

	/**
	 * Minimal Fabric mod metadata needed for class tweaker discovery.
	 *
	 * @param id            the Fabric mod identifier
	 * @param accessWidener the declared class tweaker path
	 */
	private record FabricModMetadata(
			String id,
			String accessWidener
	)
	{
	}

	/**
	 * One discovered class tweaker input.
	 *
	 * @param modId    the declaring Fabric mod identifier
	 * @param artifact the containing artifact or metadata file
	 * @param path     the declared class tweaker origin within that artifact
	 * @param content  the raw class tweaker bytes
	 */
	private record ClassTweakerEntry(
			String modId,
			Path artifact,
			String path,
			byte[] content
	)
	{
		/**
		 * Builds a stable sort key for deterministic application order.
		 *
		 * @return the sort key
		 */
		public String sortKey()
		{
			return modId + ":" + artifact.getFileName() + ":" + path;
		}
	}
}
