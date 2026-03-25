package dev.pswg.toolchain.intellij;

import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.source.SourceAttachmentResolver;
import dev.pswg.toolchain.template.XmlEscaper;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Shared helpers for generated IntelliJ launch modules, launch libraries, and run-configuration
 * argument rendering.
 */
public final class IntelliJRunConfigurationSupport
{
	/**
	 * Prevents construction.
	 */
	private IntelliJRunConfigurationSupport()
	{
	}

	/**
	 * Creates a generated IntelliJ launch module document with project-library and module
	 * dependencies.
	 *
	 * @param projectRoot the host-project root
	 * @param instanceRoot the generated launch instance root
	 * @param dependencyModuleNames the IntelliJ modules that should be built before launch
	 * @param classpathEntries the prepared runtime classpath entries
	 * @param libraryNameResolver resolves the generated project-library name for one classpath entry
	 * @return the launch module document
	 */
	public static Document createLaunchModuleDocument(
		Path projectRoot,
		Path instanceRoot,
		List<String> dependencyModuleNames,
		List<Path> classpathEntries,
		Function<Path, String> libraryNameResolver
	)
	{
		Document document = DocumentHelper.createDocument();
		Element module = document.addElement("module");
		module.addAttribute("version", "4");

		Element rootManager = module.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "true");
		rootManager.addElement("exclude-output");

		Element content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.fileUrl(projectRoot, instanceRoot));
		content.addElement("excludeFolder")
		       .addAttribute("url", IntelliJPathMacros.fileUrl(projectRoot, instanceRoot));

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");

		for (String moduleName : dependencyModuleNames)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", moduleName)
			           .addAttribute("scope", "PROVIDED");
		}

		for (Path classpathEntry : classpathEntries)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "library")
			           .addAttribute("name", libraryNameResolver.apply(classpathEntry))
			           .addAttribute("level", "project");
		}

		return document;
	}

	/**
	 * Writes the generated IntelliJ project libraries for one prepared runtime classpath.
	 *
	 * @param projectRoot the host-project root
	 * @param generatedPrefix the prefix identifying generated launch-library metadata files
	 * @param classpathEntries the prepared runtime classpath entries
	 * @param sourceAttachmentResolver resolves optional source attachments
	 * @param refresh whether to refresh external source attachments
	 * @param libraryNameResolver resolves the generated project-library name for one classpath entry
	 * @param fileNameResolver resolves the generated metadata file name for one classpath entry
	 * @throws IOException if library metadata generation fails
	 */
	public static void writeLaunchLibraries(
		Path projectRoot,
		String generatedPrefix,
		List<Path> classpathEntries,
		SourceAttachmentResolver sourceAttachmentResolver,
		boolean refresh,
		Function<Path, String> libraryNameResolver,
		Function<Path, String> fileNameResolver
	) throws IOException
	{
		Path librariesDirectory = projectRoot.resolve(".idea").resolve("libraries");
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (Path classpathEntry : classpathEntries)
		{
			String fileName = fileNameResolver.apply(classpathEntry);
			expectedFileNames.add(fileName);
			Path sourceArchive = sourceAttachmentResolver.resolveSourceArchive(classpathEntry, refresh);
			IntelliJXmlWriter.write(
				librariesDirectory.resolve(fileName),
				createLaunchLibraryDocument(projectRoot, libraryNameResolver.apply(classpathEntry), classpathEntry, sourceArchive)
			);
		}

		deleteObsoleteLaunchLibraries(librariesDirectory, generatedPrefix, expectedFileNames);
	}

	/**
	 * Registers one generated launch module in `.idea/modules.xml`.
	 *
	 * @param projectRoot the host-project root
	 * @param filePath the `$PROJECT_DIR$`-relative module file path
	 * @param legacyFilePaths obsolete historical paths to remove during registration
	 * @throws IOException if the project registration cannot be updated
	 */
	public static void registerModule(
		Path projectRoot,
		String filePath,
		List<String> legacyFilePaths
	) throws IOException
	{
		Path modulesXmlPath = projectRoot.resolve(".idea").resolve("modules.xml");
		Document document = readOrCreateProjectDocument(modulesXmlPath);
		Element project = document.getRootElement();
		Element component = firstOrCreate(project, "component", "name", "ProjectModuleManager");
		Element modules = firstOrCreate(component, "modules");

		removeRegisteredModule(modules, filePath);

		for (String legacyFilePath : legacyFilePaths)
		{
			removeRegisteredModule(modules, legacyFilePath);
		}

		modules.addElement("module")
		       .addAttribute("fileurl", "file://" + filePath)
		       .addAttribute("filepath", filePath);
		IntelliJXmlWriter.write(modulesXmlPath, document);
	}

	/**
	 * Extracts the effective runtime classpath that the prepared launch would pass to Java.
	 *
	 * @param launch the prepared launch configuration
	 * @return the ordered runtime classpath entries
	 */
	public static List<Path> effectiveRuntimeClasspath(VanillaLaunchConfig launch)
	{
		List<Path> entries = new ArrayList<>();
		String separator = System.getProperty("path.separator");

		for (int index = 0; index < launch.jvmArgs().size() - 1; index++)
		{
			String argument = launch.jvmArgs().get(index);

			if (!"-cp".equals(argument) && !"-classpath".equals(argument))
			{
				continue;
			}

			String classpath = launch.jvmArgs().get(index + 1);

			for (String rawEntry : classpath.split(Pattern.quote(separator)))
			{
				if (rawEntry.isBlank())
				{
					continue;
				}

				Path entry = Path.of(rawEntry);

				if (!entries.contains(entry))
				{
					entries.add(entry);
				}
			}
		}

		for (Path entry : launch.classpath())
		{
			if (!entries.contains(entry))
			{
				entries.add(entry);
			}
		}

		return entries;
	}

	/**
	 * Filters a prepared JVM argument list down to the values IntelliJ should pass directly when it
	 * launches the main class itself.
	 *
	 * @param jvmArgs the prepared launch JVM arguments
	 * @return the IntelliJ VM arguments
	 */
	public static List<String> ideaVmArguments(List<String> jvmArgs)
	{
		List<String> arguments = new ArrayList<>();

		for (int index = 0; index < jvmArgs.size(); index++)
		{
			String argument = jvmArgs.get(index);

			if ("-cp".equals(argument) || "-classpath".equals(argument))
			{
				index++;
				continue;
			}

			if (argument.startsWith("-javaagent:"))
			{
				continue;
			}

			arguments.add(argument);
		}

		return arguments;
	}

	/**
	 * Renders IntelliJ command-line arguments for XML serialization.
	 *
	 * @param arguments the command-line arguments
	 * @return the XML-safe argument string
	 */
	public static String renderIdeaArguments(List<String> arguments)
	{
		return arguments.stream()
		                .map(IntelliJRunConfigurationSupport::quoteIdeaArgument)
		                .collect(Collectors.joining(" "));
	}

	/**
	 * Converts a filesystem path to the slash-delimited form used in generated XML.
	 *
	 * @param path the filesystem path
	 * @return the XML-safe path string
	 */
	public static String xmlPath(Path path)
	{
		return XmlEscaper.escapePath(path);
	}

	/**
	 * Builds the generated IntelliJ project-library metadata file name for a prepared runtime
	 * classpath entry.
	 *
	 * @param libraryName the logical project-library name
	 * @return the generated metadata file name
	 */
	public static String libraryMetadataFileName(String libraryName)
	{
		return libraryName
			.replace(':', '_')
			.replace('/', '_')
			.replace('\\', '_')
			.replace(' ', '_')
			+ ".xml";
	}

	/**
	 * Quotes one IntelliJ command-line argument for XML serialization.
	 *
	 * @param argument the argument to quote
	 * @return the quoted argument
	 */
	private static String quoteIdeaArgument(String argument)
	{
		return "&quot;" + XmlEscaper.escapeAttribute(argument) + "&quot;";
	}

	/**
	 * Creates a generated project-library document for one prepared runtime classpath entry.
	 *
	 * @param projectRoot the host-project root
	 * @param libraryName the generated project-library name
	 * @param classpathEntry the prepared runtime classpath entry
	 * @param sourceArchive the optional attached source archive
	 * @return the generated project-library document
	 */
	private static Document createLaunchLibraryDocument(
		Path projectRoot,
		String libraryName,
		Path classpathEntry,
		Path sourceArchive
	)
	{
		Document document = DocumentHelper.createDocument();
		Element component = document.addElement("component");
		component.addAttribute("name", "libraryTable");
		Element library = component.addElement("library");
		library.addAttribute("name", libraryName);
		Element classes = library.addElement("CLASSES");
		String url = Files.isDirectory(classpathEntry)
			? IntelliJPathMacros.fileUrl(projectRoot, classpathEntry)
			: IntelliJPathMacros.jarUrl(projectRoot, classpathEntry);
		classes.addElement("root").addAttribute("url", url);
		library.addElement("JAVADOC");
		Element sources = library.addElement("SOURCES");

		if (sourceArchive != null)
		{
			sources.addElement("root").addAttribute(
				"url",
				Files.isDirectory(sourceArchive)
					? IntelliJPathMacros.fileUrl(projectRoot, sourceArchive)
					: IntelliJPathMacros.jarUrl(projectRoot, sourceArchive)
			);
		}

		return document;
	}

	/**
	 * Deletes stale generated launch-library metadata files after regeneration.
	 *
	 * @param librariesDirectory the IntelliJ libraries directory
	 * @param generatedPrefix the generated launch-library prefix
	 * @param expectedFileNames the expected generated file names
	 * @throws IOException if stale files cannot be removed
	 */
	private static void deleteObsoleteLaunchLibraries(
		Path librariesDirectory,
		String generatedPrefix,
		Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(librariesDirectory))
		{
			return;
		}

		try (var entries = Files.list(librariesDirectory))
		{
			for (Path entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				String fileName = entry.getFileName().toString();

				if (!fileName.startsWith(generatedPrefix) || expectedFileNames.contains(fileName))
				{
					continue;
				}

				Files.deleteIfExists(entry);
			}
		}
	}

	/**
	 * Reads an IntelliJ project XML document or creates a new empty project document if the file is
	 * absent.
	 *
	 * @param path the IntelliJ project file path
	 * @return the existing or new document
	 * @throws IOException if the file cannot be read
	 */
	private static Document readOrCreateProjectDocument(Path path) throws IOException
	{
		if (!Files.exists(path))
		{
			Document document = DocumentHelper.createDocument();
			document.addElement("project").addAttribute("version", "4");
			return document;
		}

		try
		{
			return DocumentHelper.parseText(Files.readString(path));
		}
		catch (DocumentException exception)
		{
			throw new IOException("Failed to parse IntelliJ project document: " + path, exception);
		}
	}

	/**
	 * Returns the first matching child element, creating it if needed.
	 *
	 * @param parent the parent element
	 * @param elementName the child element name
	 * @param attributeName the identifying attribute name
	 * @param attributeValue the identifying attribute value
	 * @return the existing or new child element
	 */
	private static Element firstOrCreate(
		Element parent,
		String elementName,
		String attributeName,
		String attributeValue
	)
	{
		for (Element child : parent.elements(elementName))
		{
			if (attributeValue.equals(child.attributeValue(attributeName)))
			{
				return child;
			}
		}

		Element created = parent.addElement(elementName);
		created.addAttribute(attributeName, attributeValue);
		return created;
	}

	/**
	 * Returns the first matching child element, creating it if needed.
	 *
	 * @param parent the parent element
	 * @param elementName the child element name
	 * @return the existing or new child element
	 */
	private static Element firstOrCreate(Element parent, String elementName)
	{
		Element existing = parent.element(elementName);

		if (existing != null)
		{
			return existing;
		}

		return parent.addElement(elementName);
	}

	/**
	 * Removes any registered IntelliJ module entry whose path matches the provided file path.
	 *
	 * @param modules the IntelliJ modules element
	 * @param filePath the `$PROJECT_DIR$`-relative module file path
	 */
	private static void removeRegisteredModule(Element modules, String filePath)
	{
		List<Element> matches = new ArrayList<>();

		for (Element module : modules.elements("module"))
		{
			String existingFilePath = module.attributeValue("filepath");
			String existingFileUrl = module.attributeValue("fileurl");

			if (filePath.equals(existingFilePath) || ("file://" + filePath).equals(existingFileUrl))
			{
				matches.add(module);
			}
		}

		for (Element match : matches)
		{
			modules.remove(match);
		}
	}
}
