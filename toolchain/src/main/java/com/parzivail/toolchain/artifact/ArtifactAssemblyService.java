package com.parzivail.toolchain.artifact;

import com.parzivail.toolchain.build.CompilationOutputLayout;
import com.parzivail.toolchain.model.ModuleAggregationResolver;
import com.parzivail.toolchain.model.ModuleSpec;
import com.parzivail.toolchain.model.SourceSetNames;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.project.VersionResolver;
import com.parzivail.toolchain.util.ToolchainLog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

/**
 * Assembles release-oriented artifact jars from IntelliJ-owned outputs.
 */
public final class ArtifactAssemblyService
{
	/**
	 * The tracked LICENSE file name.
	 */
	private static final String LICENSE_FILE_NAME = "LICENSE";

	/**
	 * Shared JSON serializer.
	 */
	private final ObjectMapper _mapper;

	/**
	 * Resolves the current project artifact version.
	 */
	private final VersionResolver _versionResolver;

	/**
	 * Creates the artifact assembly service.
	 */
	public ArtifactAssemblyService()
	{
		_mapper = new ObjectMapper();
		_versionResolver = new VersionResolver();
	}

	/**
	 * The result of one assembled module artifact.
	 *
	 * @param moduleId the logical module identifier
	 * @param artifactId the artifact file stem
	 * @param outputJar the assembled jar path
	 * @param nested whether the artifact was nested into another jar
	 */
	public record AssembledArtifact(
		String moduleId,
		String artifactId,
		Path outputJar,
		boolean nested
	)
	{
	}

	/**
	 * Assembles artifacts for the requested module.
	 *
	 * @param requestedModuleId the optional requested root module id
	 * @return the assembled artifact list
	 * @throws IOException if assembly fails
	 */
	public List<AssembledArtifact> assemble(String requestedModuleId) throws IOException
	{
		return assemble(requestedModuleId, null);
	}

	/**
	 * Assembles artifacts for the requested module from a selected compiled output root.
	 *
	 * @param requestedModuleId the optional requested root module id
	 * @param compiledOutputRoot the optional compiled output root override
	 * @return the assembled artifact list
	 * @throws IOException if assembly fails
	 */
	public List<AssembledArtifact> assemble(String requestedModuleId, Path compiledOutputRoot) throws IOException
	{
		RepositoryContext repository = RepositoryContext.discoverFromWorkingDirectory();
		String rootModuleId = requestedModuleId == null || requestedModuleId.isBlank()
			? repository.buildGraph().developmentModuleId()
			: requestedModuleId;
		String version = _versionResolver.resolveVersion(repository);
		ModuleSpec rootModule = ModuleAggregationResolver.requireModule(repository.buildGraph(), rootModuleId);
		Path outputDirectory = createOutputDirectory(version);
		List<AssembledArtifact> artifacts = new ArrayList<>();

		if (!rootModule.aggregateMembers().isEmpty())
		{
			List<Path> nestedJars = new ArrayList<>();

			for (ModuleSpec member : aggregatedArtifactMembers(repository, rootModuleId))
			{
				AssembledArtifact nestedArtifact = assembleStandaloneArtifact(repository, compiledOutputRoot, member, version, outputDirectory, true);
				artifacts.add(nestedArtifact);
				nestedJars.add(nestedArtifact.outputJar());
			}

			artifacts.add(assembleAggregateArtifact(repository, compiledOutputRoot, rootModule, version, outputDirectory, nestedJars));
			return List.copyOf(artifacts);
		}

		if (!isPackagedFabricModule(rootModule))
		{
			throw new IOException("Module does not describe a packaged Fabric artifact: " + rootModuleId);
		}

		artifacts.add(assembleStandaloneArtifact(repository, compiledOutputRoot, rootModule, version, outputDirectory, false));
		return List.copyOf(artifacts);
	}

	/**
	 * Collects the packaged aggregate members for one bundle root.
	 *
	 * @param repository the discovered repository context
	 * @param rootModuleId the aggregate root module identifier
	 * @return the packaged aggregate members
	 */
	private List<ModuleSpec> aggregatedArtifactMembers(
		RepositoryContext repository,
		String rootModuleId
	)
	{
		List<ModuleSpec> members = new ArrayList<>();

		for (ModuleSpec module : ModuleAggregationResolver.aggregatedDependencies(repository.buildGraph(), rootModuleId))
		{
			if (isPackagedFabricModule(module))
			{
				members.add(module);
			}
		}

		return members;
	}

	/**
	 * Assembles one ordinary module artifact.
	 *
	 * @param repository the discovered repository context
	 * @param module the packaged module
	 * @param version the resolved project version
	 * @param outputDirectory the target artifact directory
	 * @param nested whether the artifact will be nested into another jar
	 * @return the assembled artifact
	 * @throws IOException if assembly fails
	 */
	private AssembledArtifact assembleStandaloneArtifact(
		RepositoryContext repository,
		Path compiledOutputRoot,
		ModuleSpec module,
		String version,
		Path outputDirectory,
		boolean nested
	) throws IOException
	{
		String artifactId = artifactId(module);
		Path outputJar = outputDirectory.resolve(artifactId + "-" + version + ".jar");
		Map<String, byte[]> entries = new LinkedHashMap<>();
		Path mainOutput = outputRoot(repository, compiledOutputRoot, module, SourceSetNames.MAIN);
		Path clientOutput = outputRoot(repository, compiledOutputRoot, module, SourceSetNames.CLIENT);

		addOutputDirectory(entries, mainOutput, version);
		addOutputDirectory(entries, clientOutput, version);
		addLicense(entries, artifactId);
		writeJar(outputJar, entries);
		ToolchainLog.info("artifacts", "Assembled " + outputJar.getFileName());

		return new AssembledArtifact(
			module.id(),
			artifactId,
			outputJar,
			nested
		);
	}

	/**
	 * Assembles one aggregate bundle artifact with nested module jars.
	 *
	 * @param repository the discovered repository context
	 * @param rootModule the aggregate root module
	 * @param version the resolved project version
	 * @param outputDirectory the target artifact directory
	 * @param nestedJars the already assembled nested module jars
	 * @return the assembled aggregate artifact
	 * @throws IOException if assembly fails
	 */
	private AssembledArtifact assembleAggregateArtifact(
		RepositoryContext repository,
		Path compiledOutputRoot,
		ModuleSpec rootModule,
		String version,
		Path outputDirectory,
		List<Path> nestedJars
	) throws IOException
	{
		String artifactId = artifactId(rootModule);
		Path outputJar = outputDirectory.resolve(artifactId + "-" + version + ".jar");
		Map<String, byte[]> entries = new LinkedHashMap<>();
		Path mainOutput = outputRoot(repository, compiledOutputRoot, rootModule, SourceSetNames.MAIN);
		Path clientOutput = outputRoot(repository, compiledOutputRoot, rootModule, SourceSetNames.CLIENT);

		addOutputDirectory(entries, mainOutput, version);
		addOutputDirectory(entries, clientOutput, version);
		addLicense(entries, artifactId);

		for (Path nestedJar : nestedJars)
		{
			entries.put("META-INF/jars/" + nestedJar.getFileName(), Files.readAllBytes(nestedJar));
		}

		addNestedJarMetadata(entries, nestedJars);
		writeJar(outputJar, entries);
		ToolchainLog.info("artifacts", "Assembled bundle " + outputJar.getFileName());

		return new AssembledArtifact(
			rootModule.id(),
			artifactId,
			outputJar,
			false
		);
	}

	/**
	 * Adds the Loom-style nested-jar metadata to a bundle `fabric.mod.json`.
	 *
	 * @param entries the jar entries being assembled
	 * @param nestedJars the nested jar paths
	 * @throws IOException if the descriptor cannot be updated
	 */
	private void addNestedJarMetadata(Map<String, byte[]> entries, List<Path> nestedJars) throws IOException
	{
		byte[] descriptorBytes = entries.get("fabric.mod.json");

		if (descriptorBytes == null)
		{
			throw new IOException("Aggregate bundle is missing fabric.mod.json");
		}

		JsonNode descriptor = _mapper.readTree(descriptorBytes);

		if (!(descriptor instanceof ObjectNode objectNode))
		{
			throw new IOException("fabric.mod.json is not a JSON object");
		}

		ArrayNode jarsNode = objectNode.withArray("jars");
		Set<String> existingPaths = new LinkedHashSet<>();

		for (JsonNode jarNode : jarsNode)
		{
			JsonNode fileNode = jarNode.get("file");

			if (fileNode != null && fileNode.isTextual())
			{
				existingPaths.add(fileNode.asText());
			}
		}

		for (Path nestedJar : nestedJars)
		{
			String nestedPath = "META-INF/jars/" + nestedJar.getFileName();

			if (existingPaths.add(nestedPath))
			{
				ObjectNode jarNode = jarsNode.addObject();
				jarNode.put("file", nestedPath);
			}
		}

		entries.put("fabric.mod.json", _mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(objectNode));
	}

	/**
	 * Adds one compiled IntelliJ output directory to the jar entry set.
	 *
	 * @param entries the accumulated jar entries
	 * @param outputRoot the IntelliJ output directory
	 * @param version the resolved project version
	 * @throws IOException if output files cannot be read
	 */
	private void addOutputDirectory(
		Map<String, byte[]> entries,
		Path outputRoot,
		String version
	) throws IOException
	{
		if (!Files.isDirectory(outputRoot))
		{
			return;
		}

		try (var paths = Files.walk(outputRoot))
		{
			for (Path path : paths.filter(Files::isRegularFile).sorted().toList())
			{
				String relativePath = outputRoot.relativize(path).toString().replace('\\', '/');

				if ("fabric.mod.json".equals(relativePath))
				{
					entries.put(relativePath, expandedFabricModJson(path, version));
					continue;
				}

				entries.put(relativePath, Files.readAllBytes(path));
			}
		}
	}

	/**
	 * Adds the tracked LICENSE file to one assembled artifact.
	 *
	 * @param entries the accumulated jar entries
	 * @param artifactId the artifact identifier
	 * @throws IOException if the LICENSE file cannot be read
	 */
	private void addLicense(
		Map<String, byte[]> entries,
		String artifactId
	) throws IOException
	{
		Path licensePath = ToolchainPaths.PROJECT_ROOT.resolve(LICENSE_FILE_NAME);

		if (!Files.isRegularFile(licensePath))
		{
			return;
		}

		entries.put(LICENSE_FILE_NAME + "_" + artifactId, Files.readAllBytes(licensePath));
	}

	/**
	 * Expands the `fabric.mod.json` version placeholder for one packaged artifact.
	 *
	 * @param path the descriptor path
	 * @param version the resolved project version
	 * @return the expanded descriptor bytes
	 * @throws IOException if the descriptor cannot be read
	 */
	private byte[] expandedFabricModJson(Path path, String version) throws IOException
	{
		String content = Files.readString(path, StandardCharsets.UTF_8);
		return content.replace("${version}", version).getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Writes one jar file from normalized entries.
	 *
	 * @param outputJar the output jar path
	 * @param entries the normalized jar entries
	 * @throws IOException if the jar cannot be written
	 */
	private void writeJar(Path outputJar, Map<String, byte[]> entries) throws IOException
	{
		Files.createDirectories(outputJar.getParent());

		try (
			OutputStream fileStream = Files.newOutputStream(
				outputJar,
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.WRITE
			);
			JarOutputStream jarStream = new JarOutputStream(fileStream)
		)
		{
			for (Map.Entry<String, byte[]> entry : entries.entrySet())
			{
				JarEntry jarEntry = new JarEntry(entry.getKey());
				jarEntry.setTime(0L);
				jarStream.putNextEntry(jarEntry);
				jarStream.write(entry.getValue());
				jarStream.closeEntry();
			}
		}
	}

	/**
	 * Resolves the IntelliJ output root for one module source set.
	 *
	 * @param repository the discovered repository context
	 * @param module the module
	 * @param sourceSetName the source-set name
	 * @return the IntelliJ output root
	 */
	private Path outputRoot(
		RepositoryContext repository,
		Path compiledOutputRoot,
		ModuleSpec module,
		String sourceSetName
	)
	{
		Path baseOutputRoot = compiledOutputRoot != null
			? compiledOutputRoot
			: ToolchainPaths.INTELLIJ_OUTPUT_DIRECTORY;
		return CompilationOutputLayout.sourceSetOutputRoot(
			baseOutputRoot,
			repository.projectName(),
			module.id(),
			sourceSetName
		);
	}

	/**
	 * Creates the artifact output directory for one resolved version.
	 *
	 * @param version the resolved version
	 * @return the created output directory
	 * @throws IOException if the directory cannot be created
	 */
	private Path createOutputDirectory(String version) throws IOException
	{
		Path outputDirectory = ToolchainPaths.ARTIFACTS_ROOT.resolve(version);
		Files.createDirectories(outputDirectory);
		return outputDirectory;
	}

	/**
	 * Checks whether one module contributes a packaged Fabric artifact.
	 *
	 * @param module the candidate module
	 * @return whether the module contributes a packaged Fabric artifact
	 */
	private boolean isPackagedFabricModule(ModuleSpec module)
	{
		return module.fabricModJson() != null
			&& module.fabricModId() != null
			&& !module.fabricModId().isBlank();
	}

	/**
	 * Resolves the artifact identifier for one module.
	 *
	 * @param module the module
	 * @return the artifact identifier
	 */
	private String artifactId(ModuleSpec module)
	{
		return module.artifactId();
	}

}
