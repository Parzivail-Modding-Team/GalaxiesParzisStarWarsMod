package dev.pswg.toolchain.intellij;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.fabricmc.classtweaker.api.ClassTweaker;
import net.fabricmc.classtweaker.api.ClassTweakerReader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
	 * Applies all discovered transitive class tweakers to the supplied Minecraft compile jar.
	 *
	 * @param minecraftVersion the tracked Minecraft version
	 * @param minecraftJar the raw Minecraft compile jar
	 * @param modArtifacts the mod jars whose transitive class tweakers should be applied
	 * @return the transformed Minecraft compile jar, or the original jar when no tweaks are present
	 * @throws IOException if discovery or transformation fails
	 */
	public Path transformMinecraftJar(
		String minecraftVersion,
		Path minecraftJar,
		Collection<Path> modArtifacts
	) throws IOException
	{
		List<ClassTweakerEntry> classTweakers = discoverClassTweakers(modArtifacts);

		if (classTweakers.isEmpty())
		{
			return minecraftJar;
		}

		Path outputPath = transformedJarPath(minecraftVersion, minecraftJar, classTweakers);

		if (Files.exists(outputPath))
		{
			return outputPath;
		}

		ClassTweaker classTweaker = ClassTweaker.newInstance();

		for (ClassTweakerEntry entry : classTweakers)
		{
			ClassTweakerReader.create(classTweaker).read(entry.content(), entry.modId());
		}

		writeTransformedJar(minecraftJar, outputPath, classTweaker);
		return outputPath;
	}

	/**
	 * Discovers class tweaker declarations from Fabric mod jars.
	 *
	 * @param modArtifacts the candidate mod artifacts
	 * @return the discovered class tweaker entries
	 * @throws IOException if a mod jar cannot be inspected
	 */
	private List<ClassTweakerEntry> discoverClassTweakers(Collection<Path> modArtifacts) throws IOException
	{
		List<ClassTweakerEntry> entries = new ArrayList<>();
		Set<Path> inspectedArtifacts = new LinkedHashSet<>(modArtifacts);

		for (Path artifact : inspectedArtifacts)
		{
			if (!artifact.getFileName().toString().endsWith(".jar"))
			{
				continue;
			}

			try (JarFile jarFile = new JarFile(artifact.toFile()))
			{
				JarEntry fabricModJsonEntry = jarFile.getJarEntry("fabric.mod.json");

				if (fabricModJsonEntry == null)
				{
					continue;
				}

				FabricModMetadata fabricModMetadata = readFabricModMetadata(jarFile, fabricModJsonEntry);

				if (fabricModMetadata.accessWidener() == null || fabricModMetadata.accessWidener().isBlank())
				{
					continue;
				}

				JarEntry classTweakerEntry = jarFile.getJarEntry(fabricModMetadata.accessWidener());

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

		return entries.stream()
		              .sorted(Comparator.comparing(ClassTweakerEntry::sortKey))
		              .toList();
	}

	/**
	 * Reads minimal Fabric mod metadata from a jar.
	 *
	 * @param jarFile the containing jar
	 * @param fabricModJsonEntry the `fabric.mod.json` entry
	 * @return the parsed Fabric mod metadata
	 * @throws IOException if the metadata cannot be read
	 */
	private FabricModMetadata readFabricModMetadata(JarFile jarFile, JarEntry fabricModJsonEntry) throws IOException
	{
		try (InputStream inputStream = jarFile.getInputStream(fabricModJsonEntry))
		{
			return _mapper.readValue(inputStream, FabricModMetadata.class);
		}
	}

	/**
	 * Writes a transformed Minecraft jar to the IntelliJ transform cache.
	 *
	 * @param inputJar the raw Minecraft compile jar
	 * @param outputJar the transformed jar target
	 * @param classTweaker the loaded class tweaker rules
	 * @throws IOException if the transformed jar cannot be written
	 */
	private void writeTransformedJar(
		Path inputJar,
		Path outputJar,
		ClassTweaker classTweaker
	) throws IOException
	{
		Files.createDirectories(outputJar.getParent());
		Path temporaryOutput = outputJar.resolveSibling(outputJar.getFileName() + ".part");
		Set<String> targets = classTweaker.getTargets();

		try (JarFile jarFile = new JarFile(inputJar.toFile());
		     OutputStream fileOutputStream = Files.newOutputStream(temporaryOutput);
		     JarOutputStream outputStream = new JarOutputStream(fileOutputStream))
		{
			Enumeration<JarEntry> entries = jarFile.entries();

			while (entries.hasMoreElements())
			{
				JarEntry inputEntry = entries.nextElement();
				JarEntry outputEntry = new JarEntry(inputEntry.getName());
				outputEntry.setTime(inputEntry.getTime());
				outputStream.putNextEntry(outputEntry);

				if (!inputEntry.isDirectory())
				{
					byte[] content = readBytes(jarFile, inputEntry);

					if (inputEntry.getName().endsWith(".class"))
					{
						String className = inputEntry.getName().substring(0, inputEntry.getName().length() - 6);

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

		try
		{
			Files.move(temporaryOutput, outputJar, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		}
		catch (IOException ignored)
		{
			Files.move(temporaryOutput, outputJar, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Transforms one targeted class with the loaded class tweaker rules.
	 *
	 * @param classTweaker the loaded class tweaker
	 * @param className the internal JVM class name
	 * @param input the original class bytes
	 * @return the transformed class bytes
	 */
	private byte[] transformClass(ClassTweaker classTweaker, String className, byte[] input)
	{
		ClassReader reader = new ClassReader(input);

		if (!reader.getClassName().equals(className))
		{
			throw new IllegalStateException("Class name mismatch: expected " + className + " but found " + reader.getClassName());
		}

		ClassWriter writer = new ClassWriter(0);
		ClassVisitor classVisitor = classTweaker.createClassVisitor(Opcodes.ASM9, writer, null);
		reader.accept(classVisitor, 0);
		return writer.toByteArray();
	}

	/**
	 * Computes a stable transformed-jar path for a Minecraft version plus class tweaker set.
	 *
	 * @param minecraftVersion the tracked Minecraft version
	 * @param minecraftJar the raw Minecraft compile jar
	 * @param classTweakers the discovered class tweaker entries
	 * @return the transformed jar cache path
	 * @throws IOException if the cache key cannot be computed
	 */
	private Path transformedJarPath(
		String minecraftVersion,
		Path minecraftJar,
		List<ClassTweakerEntry> classTweakers
	) throws IOException
	{
		String cacheKey = cacheKey(classTweakers);
		Path cacheDirectory = minecraftJar.getParent()
		                                 .resolve(".intellij-transformed")
		                                 .resolve(minecraftVersion);

		return cacheDirectory.resolve("minecraft-client-" + cacheKey + ".jar");
	}

	/**
	 * Computes a stable cache key for a set of class tweaker inputs.
	 *
	 * @param classTweakers the discovered class tweaker entries
	 * @return the cache key
	 * @throws IOException if hashing is unavailable
	 */
	private String cacheKey(List<ClassTweakerEntry> classTweakers) throws IOException
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");

			for (ClassTweakerEntry classTweaker : classTweakers)
			{
				digest.update(classTweaker.sortKey().getBytes(StandardCharsets.UTF_8));
				digest.update((byte) '\n');
				digest.update(classTweaker.content());
			}

			byte[] bytes = digest.digest();
			StringBuilder builder = new StringBuilder(bytes.length * 2);

			for (byte value : bytes)
			{
				builder.append(Character.forDigit((value >> 4) & 0xF, 16));
				builder.append(Character.forDigit(value & 0xF, 16));
			}

			return builder.toString();
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new IOException("SHA-1 hashing is not available", exception);
		}
	}

	/**
	 * Reads a jar entry into memory.
	 *
	 * @param jarFile the containing jar
	 * @param jarEntry the entry to read
	 * @return the entry bytes
	 * @throws IOException if the entry cannot be read
	 */
	private byte[] readBytes(JarFile jarFile, JarEntry jarEntry) throws IOException
	{
		try (InputStream inputStream = jarFile.getInputStream(jarEntry))
		{
			return inputStream.readAllBytes();
		}
	}

	/**
	 * Minimal Fabric mod metadata needed for class tweaker discovery.
	 *
	 * @param id the Fabric mod identifier
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
	 * @param modId the declaring Fabric mod identifier
	 * @param artifact the containing artifact
	 * @param path the declared class tweaker path inside the jar
	 * @param content the raw class tweaker bytes
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
