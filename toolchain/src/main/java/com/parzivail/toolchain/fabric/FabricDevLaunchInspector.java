package com.parzivail.toolchain.fabric;

import com.parzivail.toolchain.intellij.IntelliJModuleNames;
import com.parzivail.toolchain.path.ToolchainPaths;
import com.parzivail.toolchain.project.RepositoryContext;
import com.parzivail.toolchain.runtime.LaunchEnvironment;
import com.parzivail.toolchain.util.HostPlatform;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Inspects the current repository's Fabric dev-launch contract using the generated artifacts.
 */
public final class FabricDevLaunchInspector
{
	/**
	 * Loom's fallback client main class when installer metadata does not override it.
	 */
	public static final String DEFAULT_CLIENT_MAIN_CLASS = "net.fabricmc.loader.launch.knot.KnotClient";

	/**
	 * Loom's fallback dedicated server main class when installer metadata does not override it.
	 */
	public static final String DEFAULT_SERVER_MAIN_CLASS = "net.fabricmc.loader.launch.knot.KnotServer";

	/**
	 * Inspects the current dev-launch contract for one environment.
	 *
	 * @param environment the target environment
	 *
	 * @return the collected inspection summary
	 *
	 * @throws IOException if inspection fails
	 */
	public FabricDevLaunchSummary inspect(LaunchEnvironment environment) throws IOException
	{
		var repoContext = RepositoryContext.load();
		var runConfigPath = generatedRunConfigurationPath(environment);
		var runConfiguration = parseIdeaRunConfiguration(runConfigPath);
		var runtimeMainClass = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.main");
		var dliConfigPath = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.config");
		var dliEnvironment = extractVmProperty(runConfiguration.vmParameters(), "fabric.dli.env");
		var launchConfig = parseLaunchConfig(resolveLaunchConfigPath(dliConfigPath));

		return new FabricDevLaunchSummary(
				repoContext.minecraftVersion(),
				repoContext.loaderVersion(),
				FabricDevLaunchService.DEV_LAUNCH_MAIN_CLASS,
				defaultRuntimeMainClass(environment),
				runConfiguration.mainClass(),
				runtimeMainClass,
				dliEnvironment,
				dliConfigPath,
				launchConfig
		);
	}

	/**
	 * Parses a Loom-style development launch configuration file.
	 *
	 * @param path the configuration file path
	 *
	 * @return the parsed configuration
	 *
	 * @throws IOException if the file cannot be read
	 */
	private FabricDevLaunchConfig parseLaunchConfig(Path path) throws IOException
	{
		Map<String, List<String>> sections = new LinkedHashMap<>();
		String currentSection = null;

		for (var line : Files.readAllLines(path))
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
	 *
	 * @return the parsed run configuration values
	 *
	 * @throws IOException if the file cannot be read
	 */
	private IdeaRunConfiguration parseIdeaRunConfiguration(Path path) throws IOException
	{
		try
		{
			var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile());
			var configuration = (Element)document.getElementsByTagName("configuration").item(0);
			var mainClass = optionValue(configuration, "MAIN_CLASS_NAME");
			var vmParameters = optionValue(configuration, "VM_PARAMETERS");
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
	 * @param optionName    the option name
	 *
	 * @return the option value, or {@code null}
	 */
	private String optionValue(Element configuration, String optionName)
	{
		var optionNodes = configuration.getElementsByTagName("option");

		for (var i = 0; i < optionNodes.getLength(); i++)
		{
			var option = (Element)optionNodes.item(i);

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
	 * @param key          the property key
	 *
	 * @return the extracted property value, or {@code null}
	 */
	private String extractVmProperty(String vmParameters, String key)
	{
		if (vmParameters == null)
		{
			return null;
		}

		var matcher = Pattern.compile(
				"(?:^|\\s|\")-D" + Pattern.quote(key) + "=([^\"\\s]+(?:\\s[^\"\\s]+)*)"
		).matcher(vmParameters);

		if (matcher.find())
		{
			return matcher.group(1);
		}

		return null;
	}

	/**
	 * Resolves the generated IntelliJ run configuration path for the current host platform.
	 *
	 * @return the run configuration path
	 *
	 * @throws IOException if the generated file does not exist
	 */
	private Path generatedRunConfigurationPath(LaunchEnvironment environment) throws IOException
	{
		var path = ToolchainPaths.INTELLIJ_RUN_CONFIGS_DIRECTORY.resolve(IntelliJModuleNames.fabricRunConfigurationFileName(environment.id(), HostPlatform.current().id()));

		if (!Files.isRegularFile(path))
		{
			throw new IOException("Generated Fabric run configuration not found: " + path);
		}

		return path;
	}

	/**
	 * Gets the fallback runtime main class for one environment.
	 *
	 * @param environment the inspected environment
	 *
	 * @return the fallback runtime main class
	 */
	private String defaultRuntimeMainClass(LaunchEnvironment environment)
	{
		return environment.isClient() ? DEFAULT_CLIENT_MAIN_CLASS : DEFAULT_SERVER_MAIN_CLASS;
	}

	/**
	 * Resolves the generated DLI launch config path from the IntelliJ VM properties.
	 *
	 * @param dliConfigPath the configured `fabric.dli.config` path
	 *
	 * @return the launch config path
	 *
	 * @throws IOException if the property is missing or the file does not exist
	 */
	private Path resolveLaunchConfigPath(String dliConfigPath) throws IOException
	{
		if (dliConfigPath == null || dliConfigPath.isBlank())
		{
			throw new IOException("Generated run configuration is missing -Dfabric.dli.config");
		}

		var path = Paths.get(dliConfigPath);

		if (!Files.isRegularFile(path))
		{
			throw new IOException("Generated DLI config not found: " + path);
		}

		return path;
	}

	/**
	 * Parsed IntelliJ run configuration data.
	 *
	 * @param path         the run configuration file path
	 * @param mainClass    the configured entrypoint
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
