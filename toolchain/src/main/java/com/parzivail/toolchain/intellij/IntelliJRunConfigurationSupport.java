package com.parzivail.toolchain.intellij;

import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.runtime.VanillaLaunchConfig;
import com.parzivail.toolchain.source.SourceAttachmentResolver;
import com.parzivail.toolchain.template.TemplateXmlWriter;
import com.parzivail.toolchain.template.XmlEscaper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
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
	 * @param instanceRoot          the generated launch instance root
	 * @param dependencyModuleNames the IntelliJ modules that should be built before launch
	 * @param classpathEntries      the prepared runtime classpath entries
	 * @param libraryNameResolver   resolves the generated project-library name for one classpath entry
	 *
	 * @return the launch module document
	 */
	public static Document createLaunchModuleDocument(
			Path instanceRoot,
			List<String> dependencyModuleNames,
			List<Path> classpathEntries,
			Function<Path, String> libraryNameResolver
	)
	{
		var document = DocumentHelper.createDocument();
		var module = document.addElement("module");
		module.addAttribute("version", "4");

		var rootManager = module.addElement("component");
		rootManager.addAttribute("name", "NewModuleRootManager");
		rootManager.addAttribute("inherit-compiler-output", "true");
		rootManager.addElement("exclude-output");

		var content = rootManager.addElement("content");
		content.addAttribute("url", IntelliJPathMacros.fileUrl(instanceRoot));
		content.addElement("excludeFolder")
		       .addAttribute("url", IntelliJPathMacros.fileUrl(instanceRoot));

		rootManager.addElement("orderEntry").addAttribute("type", "inheritedJdk");
		rootManager.addElement("orderEntry").addAttribute("type", "sourceFolder").addAttribute("forTests", "false");

		for (var moduleName : dependencyModuleNames)
		{
			rootManager.addElement("orderEntry")
			           .addAttribute("type", "module")
			           .addAttribute("module-name", moduleName)
			           .addAttribute("scope", "PROVIDED");
		}

		for (var classpathEntry : classpathEntries)
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
	 * @param generatedPrefix          the prefix identifying generated launch-library metadata files
	 * @param classpathEntries         the prepared runtime classpath entries
	 * @param sourceAttachmentResolver resolves optional source attachments
	 * @param refresh                  whether to refresh external source attachments
	 * @param libraryNameResolver      resolves the generated project-library name for one classpath entry
	 * @param fileNameResolver         resolves the generated metadata file name for one classpath entry
	 *
	 * @throws IOException if library metadata generation fails
	 */
	public static void writeLaunchLibraries(
			String generatedPrefix,
			List<Path> classpathEntries,
			SourceAttachmentResolver sourceAttachmentResolver,
			boolean refresh,
			Function<Path, String> libraryNameResolver,
			Function<Path, String> fileNameResolver
	) throws IOException
	{
		Set<String> expectedFileNames = new LinkedHashSet<>();

		for (var classpathEntry : classpathEntries)
		{
			var fileName = fileNameResolver.apply(classpathEntry);
			expectedFileNames.add(fileName);
			var sourceArchive = sourceAttachmentResolver.resolveSourceArchive(classpathEntry, refresh);
			TemplateXmlWriter.write(
					ToolchainPaths.INTELLIJ_META_LIBRARIES_DIRECTORY.resolve(fileName),
					createLaunchLibraryDocument(libraryNameResolver.apply(classpathEntry), classpathEntry, sourceArchive)
			);
		}

		deleteObsoleteLaunchLibraries(generatedPrefix, expectedFileNames);
	}

	/**
	 * Registers one generated launch module in `.idea/modules.xml`.
	 *
	 * @param filePath the `$PROJECT_DIR$`-relative module file path
	 *
	 * @throws IOException if the project registration cannot be updated
	 */
	public static void registerModule(
			String filePath
	) throws IOException
	{
		var document = readOrCreateProjectDocument();
		var project = document.getRootElement();
		var component = firstOrCreate(project, "component", "name", "ProjectModuleManager");
		var modules = firstOrCreate(component, "modules");

		removeRegisteredModule(modules, filePath);

		modules.addElement("module")
		       .addAttribute("fileurl", "file://" + filePath)
		       .addAttribute("filepath", filePath);
		TemplateXmlWriter.write(ToolchainPaths.INTELLIJ_META_MODULES_FILE, document);
	}

	/**
	 * Extracts the effective runtime classpath that the prepared launch would pass to Java.
	 *
	 * @param launch the prepared launch configuration
	 *
	 * @return the ordered runtime classpath entries
	 */
	public static List<Path> effectiveRuntimeClasspath(VanillaLaunchConfig launch)
	{
		List<Path> entries = new ArrayList<>();
		var separator = File.pathSeparator;

		for (var index = 0; index < launch.jvmArgs().size() - 1; index++)
		{
			var argument = launch.jvmArgs().get(index);

			if (!"-cp".equals(argument) && !"-classpath".equals(argument))
			{
				continue;
			}

			var classpath = launch.jvmArgs().get(index + 1);

			for (var rawEntry : classpath.split(Pattern.quote(separator)))
			{
				if (rawEntry.isBlank())
				{
					continue;
				}

				var entry = Path.of(rawEntry);

				if (!entries.contains(entry))
				{
					entries.add(entry);
				}
			}
		}

		for (var entry : launch.classpath())
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
	 *
	 * @return the IntelliJ VM arguments
	 */
	public static List<String> ideaVmArguments(List<String> jvmArgs)
	{
		List<String> arguments = new ArrayList<>();

		for (var index = 0; index < jvmArgs.size(); index++)
		{
			var argument = jvmArgs.get(index);

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
	 *
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
	 *
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
	 *
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
	 *
	 * @return the quoted argument
	 */
	private static String quoteIdeaArgument(String argument)
	{
		return "&quot;" + XmlEscaper.escapeAttribute(argument) + "&quot;";
	}

	/**
	 * Creates a generated project-library document for one prepared runtime classpath entry.
	 *
	 * @param libraryName    the generated project-library name
	 * @param classpathEntry the prepared runtime classpath entry
	 * @param sourceArchive  the optional attached source archive
	 *
	 * @return the generated project-library document
	 */
	private static Document createLaunchLibraryDocument(
			String libraryName,
			Path classpathEntry,
			Path sourceArchive
	)
	{
		var document = DocumentHelper.createDocument();
		var component = document.addElement("component");
		component.addAttribute("name", "libraryTable");
		var library = component.addElement("library");
		library.addAttribute("name", libraryName);
		var classes = library.addElement("CLASSES");
		var url = Files.isDirectory(classpathEntry)
		          ? IntelliJPathMacros.fileUrl(classpathEntry)
		          : IntelliJPathMacros.jarUrl(classpathEntry);
		classes.addElement("root").addAttribute("url", url);
		library.addElement("JAVADOC");
		var sources = library.addElement("SOURCES");

		if (sourceArchive != null)
		{
			sources.addElement("root").addAttribute(
					"url",
					Files.isDirectory(sourceArchive)
					? IntelliJPathMacros.fileUrl(sourceArchive)
					: IntelliJPathMacros.jarUrl(sourceArchive)
			);
		}

		return document;
	}

	/**
	 * Deletes stale generated launch-library metadata files after regeneration.
	 *
	 * @param generatedPrefix   the generated launch-library prefix
	 * @param expectedFileNames the expected generated file names
	 *
	 * @throws IOException if stale files cannot be removed
	 */
	private static void deleteObsoleteLaunchLibraries(
			String generatedPrefix,
			Set<String> expectedFileNames
	) throws IOException
	{
		if (!Files.isDirectory(ToolchainPaths.INTELLIJ_META_LIBRARIES_DIRECTORY))
		{
			return;
		}

		try (var entries = Files.list(ToolchainPaths.INTELLIJ_META_LIBRARIES_DIRECTORY))
		{
			for (var entry : entries.toList())
			{
				if (!Files.isRegularFile(entry))
				{
					continue;
				}

				var fileName = entry.getFileName().toString();

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
	 * @return the existing or new document
	 *
	 * @throws IOException if the file cannot be read
	 */
	private static Document readOrCreateProjectDocument() throws IOException
	{
		if (!Files.exists(ToolchainPaths.INTELLIJ_META_MODULES_FILE))
		{
			var document = DocumentHelper.createDocument();
			document.addElement("project").addAttribute("version", "4");
			return document;
		}

		try
		{
			return DocumentHelper.parseText(Files.readString(ToolchainPaths.INTELLIJ_META_MODULES_FILE));
		}
		catch (DocumentException exception)
		{
			throw new IOException("Failed to parse IntelliJ project document: " + ToolchainPaths.INTELLIJ_META_MODULES_FILE, exception);
		}
	}

	/**
	 * Returns the first matching child element, creating it if needed.
	 *
	 * @param parent         the parent element
	 * @param elementName    the child element name
	 * @param attributeName  the identifying attribute name
	 * @param attributeValue the identifying attribute value
	 *
	 * @return the existing or new child element
	 */
	private static Element firstOrCreate(
			Element parent,
			String elementName,
			String attributeName,
			String attributeValue
	)
	{
		for (var child : parent.elements(elementName))
		{
			if (attributeValue.equals(child.attributeValue(attributeName)))
			{
				return child;
			}
		}

		var created = parent.addElement(elementName);
		created.addAttribute(attributeName, attributeValue);
		return created;
	}

	/**
	 * Returns the first matching child element, creating it if needed.
	 *
	 * @param parent      the parent element
	 * @param elementName the child element name
	 *
	 * @return the existing or new child element
	 */
	private static Element firstOrCreate(Element parent, String elementName)
	{
		var existing = parent.element(elementName);

		if (existing != null)
		{
			return existing;
		}

		return parent.addElement(elementName);
	}

	/**
	 * Removes any registered IntelliJ module entry whose path matches the provided file path.
	 *
	 * @param modules  the IntelliJ modules element
	 * @param filePath the `$PROJECT_DIR$`-relative module file path
	 */
	private static void removeRegisteredModule(Element modules, String filePath)
	{
		List<Element> matches = new ArrayList<>();

		for (var module : modules.elements("module"))
		{
			var existingFilePath = module.attributeValue("filepath");
			var existingFileUrl = module.attributeValue("fileurl");

			if (filePath.equals(existingFilePath) || ("file://" + filePath).equals(existingFileUrl))
			{
				matches.add(module);
			}
		}

		for (var match : matches)
		{
			modules.remove(match);
		}
	}
}
