package dev.pswg.toolchain.fabric;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Inspects the current repository's Fabric dev-launch contract using Loom-generated artifacts.
 */
public final class FabricDevLaunchInspector
{
	/**
	 * Loom's default development launcher entrypoint.
	 */
	public static final String DEFAULT_DEV_LAUNCH_MAIN_CLASS = "net.fabricmc.devlaunchinjector.Main";

	/**
	 * Loom's fallback client main class when installer metadata does not override it.
	 */
	public static final String DEFAULT_CLIENT_MAIN_CLASS = "net.fabricmc.loader.launch.knot.KnotClient";

	/**
	 * Loom's fallback dedicated server main class when installer metadata does not override it.
	 */
	public static final String DEFAULT_SERVER_MAIN_CLASS = "net.fabricmc.loader.launch.knot.KnotServer";

	/**
	 * The toolchain project root.
	 */
	private final Path _toolchainRoot;

	/**
	 * The tracked PSWG repository root.
	 */
	private final Path _projectRoot;

	/**
	 * Creates an inspector rooted at the standalone toolchain project.
	 */
	public FabricDevLaunchInspector()
	{
		_toolchainRoot = Path.of("").toAbsolutePath().normalize();
		_projectRoot = _toolchainRoot.getParent();
	}

	/**
	 * Inspects the current client dev-launch contract.
	 *
	 * @return the collected inspection summary
	 * @throws IOException if inspection fails
	 */
	public FabricDevLaunchSummary inspectClient() throws IOException
	{
		Properties properties = loadGradleProperties();
		Path launchConfigPath = _projectRoot.resolve(".gradle").resolve("loom-cache").resolve("launch.cfg");
		Path runConfigPath = _projectRoot.resolve(".idea").resolve("runConfigurations").resolve("Minecraft_Client.xml");
		FabricDevLaunchConfig launchConfig = parseLaunchConfig(launchConfigPath);
		IdeaRunConfiguration runConfiguration = parseIdeaRunConfiguration(runConfigPath);
		String runtimeMainClass = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.main");
		String dliConfigPath = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.config");
		String dliEnvironment = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.env");

		return new FabricDevLaunchSummary(
			properties.getProperty("minecraft_version"),
			properties.getProperty("loader_version"),
			properties.getProperty("fabric_version"),
			properties.getProperty("loom_version"),
			DEFAULT_DEV_LAUNCH_MAIN_CLASS,
			DEFAULT_CLIENT_MAIN_CLASS,
			runConfiguration.mainClass(),
			runtimeMainClass,
			dliEnvironment,
			dliConfigPath,
			launchConfig
		);
	}

	/**
	 * Loads the repository Gradle properties file.
	 *
	 * @return the parsed Gradle properties
	 * @throws IOException if the file cannot be read
	 */
	private Properties loadGradleProperties() throws IOException
	{
		Properties properties = new Properties();
		Path path = _projectRoot.resolve("gradle.properties");

		try (InputStream inputStream = Files.newInputStream(path))
		{
			properties.load(inputStream);
		}

		return properties;
	}

	/**
	 * Parses a Loom-style development launch configuration file.
	 *
	 * @param path the configuration file path
	 * @return the parsed configuration
	 * @throws IOException if the file cannot be read
	 */
	private FabricDevLaunchConfig parseLaunchConfig(Path path) throws IOException
	{
		Map<String, List<String>> sections = new LinkedHashMap<>();
		String currentSection = null;

		for (String line : Files.readAllLines(path))
		{
			if (line.isBlank())
			{
				continue;
			}

			if (!line.startsWith("\t"))
			{
				currentSection = line.trim();
				sections.putIfAbsent(currentSection, new ArrayList<>());
				continue;
			}

			if (currentSection != null)
			{
				sections.get(currentSection).add(line.trim());
			}
		}

		return new FabricDevLaunchConfig(path, sections);
	}

	/**
	 * Parses a generated IntelliJ Application run configuration.
	 *
	 * @param path the run configuration file path
	 * @return the parsed run configuration values
	 * @throws IOException if the file cannot be read
	 */
	private IdeaRunConfiguration parseIdeaRunConfiguration(Path path) throws IOException
	{
		try
		{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile());
			Element configuration = (Element) document.getElementsByTagName("configuration").item(0);
			String mainClass = optionValue(configuration, "MAIN_CLASS_NAME");
			String vmParameters = optionValue(configuration, "VM_PARAMETERS");
			return new IdeaRunConfiguration(path, mainClass, vmParameters);
		}
		catch (Exception exception)
		{
			throw new IOException("Failed to parse IntelliJ run configuration: " + path, exception);
		}
	}

	/**
	 * Reads a named option value from a generated IntelliJ run configuration.
	 *
	 * @param configuration the configuration element
	 * @param optionName the option name
	 * @return the option value, or {@code null}
	 */
	private String optionValue(Element configuration, String optionName)
	{
		NodeList optionNodes = configuration.getElementsByTagName("option");

		for (int i = 0; i < optionNodes.getLength(); i++)
		{
			Element option = (Element) optionNodes.item(i);

			if (optionName.equals(option.getAttribute("name")))
			{
				return option.getAttribute("value");
			}
		}

		return null;
	}

	/**
	 * Extracts a JVM system property value from an IntelliJ VM parameter string.
	 *
	 * @param vmParameters the raw VM parameter string
	 * @param key the property key
	 * @return the extracted property value, or {@code null}
	 */
	private String extractVmProperty(String vmParameters, String key)
	{
		if (vmParameters == null)
		{
			return null;
		}

		String prefix = "-D" + key + "=";

		for (String token : vmParameters.split(" "))
		{
			if (token.startsWith(prefix))
			{
				return token.substring(prefix.length());
			}
		}

		return null;
	}

	/**
	 * Parsed IntelliJ run configuration data.
	 *
	 * @param path the run configuration file path
	 * @param mainClass the configured entrypoint
	 * @param vmParameters the raw VM parameter string
	 */
	private record IdeaRunConfiguration(
		Path path,
		String mainClass,
		String vmParameters
	)
	{
	}
}
