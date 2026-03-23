package dev.pswg.toolchain.pswg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves the current PSWG artifact version from repository state.
 */
public final class PswgVersionResolver
{
	/**
	 * Matches the historical git-describe based release format used by the Gradle build.
	 */
	private static final Pattern DESCRIBE_PATTERN = Pattern.compile(
		"^(0|[1-9][0-9]+)(?:\\.(0|[1-9][0-9]+)(?:\\.(0|[1-9][0-9]+))?)?\\+[0-9.]+((?:-[0-9]+-g[0-9a-f]+)?(?:-dirty)?)?$"
	);

	/**
	 * Resolves the current PSWG version string.
	 *
	 * @param repository the discovered PSWG repository context
	 * @return the resolved PSWG version string
	 * @throws IOException if git metadata cannot be read or does not match the expected format
	 */
	public String resolveVersion(PswgRepositoryContext repository) throws IOException
	{
		String describe = gitDescribe(repository.projectRoot());
		Matcher matcher = DESCRIBE_PATTERN.matcher(describe);

		if (!matcher.matches())
		{
			throw new IOException("Unsupported PSWG git describe format: " + describe);
		}

		int major = parseVersionComponent(matcher.group(1));
		int minor = parseVersionComponent(matcher.group(2));
		int patch = parseVersionComponent(matcher.group(3));
		String suffix = matcher.group(4);

		if (suffix != null && !suffix.isEmpty())
		{
			patch += 1;
		}

		return major + "." + minor + "." + patch + developmentSuffix(suffix) + "+" + repository.minecraftVersion();
	}

	/**
	 * Reads the repository version marker from git.
	 *
	 * @param projectRoot the tracked repository root
	 * @return the raw git-describe output
	 * @throws IOException if git cannot be executed or returns no version marker
	 */
	private String gitDescribe(Path projectRoot) throws IOException
	{
		Process process;

		try
		{
			process = new ProcessBuilder("git", "describe", "--tags", "--dirty")
				.directory(projectRoot.toFile())
				.redirectErrorStream(true)
				.start();
		}
		catch (IOException exception)
		{
			throw new IOException("Failed to execute git describe in " + projectRoot, exception);
		}

		byte[] outputBytes;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream())
		{
			process.getInputStream().transferTo(outputStream);
			outputBytes = outputStream.toByteArray();
		}

		try
		{
			int exitCode = process.waitFor();
			String output = new String(outputBytes, StandardCharsets.UTF_8).trim();

			if (exitCode != 0 || output.isBlank())
			{
				throw new IOException("git describe failed: " + output);
			}

			return output;
		}
		catch (InterruptedException exception)
		{
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while resolving PSWG version from git", exception);
		}
	}

	/**
	 * Parses one semantic version component, defaulting missing groups to zero.
	 *
	 * @param value the regex capture group value
	 * @return the parsed integer component
	 */
	private int parseVersionComponent(String value)
	{
		if (value == null || value.isBlank())
		{
			return 0;
		}

		return Integer.parseInt(value);
	}

	/**
	 * Resolves the development suffix used by the historical Gradle version contract.
	 *
	 * @param gitSuffix the raw suffix from git-describe
	 * @return the normalized PSWG development suffix
	 */
	private String developmentSuffix(String gitSuffix)
	{
		if (gitSuffix == null || gitSuffix.isEmpty())
		{
			return "";
		}

		if ("-dirty".equals(gitSuffix))
		{
			return "-dev-0-dirty";
		}

		return "-dev" + gitSuffix;
	}
}
