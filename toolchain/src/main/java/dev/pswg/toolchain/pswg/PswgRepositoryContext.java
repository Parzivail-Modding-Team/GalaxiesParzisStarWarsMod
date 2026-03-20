package dev.pswg.toolchain.pswg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Discovers and caches the tracked PSWG repository metadata that the standalone toolchain still
 * consumes while version and dependency properties remain repo-owned.
 *
 * <p>This is the main bridge between the standalone toolchain project and the tracked PSWG repo.
 * Keeping that relationship explicit makes it easier to replace the remaining `gradle.properties`
 * reads later without hunting through launch and IntelliJ generation code.
 */
public final class PswgRepositoryContext
{
	/**
	 * The shared Gradle properties file name.
	 */
	private static final String GRADLE_PROPERTIES_FILE = "gradle.properties";

	/**
	 * The standalone toolchain project root.
	 */
	private final Path _toolchainRoot;

	/**
	 * The tracked PSWG repository root.
	 */
	private final Path _projectRoot;

	/**
	 * The IntelliJ project name.
	 */
	private final String _projectName;

	/**
	 * The tracked repository Gradle properties.
	 */
	private final Properties _gradleProperties;

	/**
	 * Creates a repository context from resolved paths and metadata.
	 */
	private PswgRepositoryContext(
		Path toolchainRoot,
		Path projectRoot,
		String projectName,
		Properties gradleProperties
	)
	{
		_toolchainRoot = toolchainRoot;
		_projectRoot = projectRoot;
		_projectName = projectName;
		_gradleProperties = gradleProperties;
	}

	/**
	 * Discovers the tracked repository context from the standalone toolchain working directory.
	 *
	 * @return the discovered repository context
	 * @throws IOException if tracked metadata cannot be read
	 */
	public static PswgRepositoryContext discoverFromToolchainWorkingDirectory() throws IOException
	{
		Path toolchainRoot = Path.of("").toAbsolutePath().normalize();
		Path projectRoot = toolchainRoot.getParent();
		Properties gradleProperties = loadGradleProperties(projectRoot);
		String projectName = readProjectName(projectRoot);

		return new PswgRepositoryContext(
			toolchainRoot,
			projectRoot,
			projectName,
			gradleProperties
		);
	}

	/**
	 * Gets the standalone toolchain project root.
	 *
	 * @return the toolchain project root
	 */
	public Path toolchainRoot()
	{
		return _toolchainRoot;
	}

	/**
	 * Gets the tracked PSWG repository root.
	 *
	 * @return the PSWG repository root
	 */
	public Path projectRoot()
	{
		return _projectRoot;
	}

	/**
	 * Gets the IntelliJ project name.
	 *
	 * @return the IntelliJ project name
	 */
	public String projectName()
	{
		return _projectName;
	}

	/**
	 * Gets the tracked repository Gradle properties.
	 *
	 * @return the Gradle properties
	 */
	public Properties gradleProperties()
	{
		return _gradleProperties;
	}

	/**
	 * Loads the tracked repository Gradle properties.
	 *
	 * @param projectRoot the PSWG repository root
	 * @return the parsed Gradle properties
	 * @throws IOException if the properties file cannot be read
	 */
	private static Properties loadGradleProperties(Path projectRoot) throws IOException
	{
		Properties properties = new Properties();
		Path path = projectRoot.resolve(GRADLE_PROPERTIES_FILE);

		try (InputStream inputStream = Files.newInputStream(path))
		{
			properties.load(inputStream);
		}

		return properties;
	}

	/**
	 * Reads the IntelliJ project name, falling back to the repository directory name.
	 *
	 * @param projectRoot the PSWG repository root
	 * @return the IntelliJ project name
	 */
	private static String readProjectName(Path projectRoot)
	{
		Path projectNameFile = projectRoot.resolve(".idea").resolve(".name");

		try
		{
			if (Files.exists(projectNameFile))
			{
				String value = Files.readString(projectNameFile).trim();

				if (!value.isBlank())
				{
					return value;
				}
			}
		}
		catch (IOException ignored)
		{
		}

		return projectRoot.getFileName().toString();
	}
}
