package dev.pswg.gametest;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * Resolves where GameTest runs write their reports, diagnostics and screenshots.
 *
 * <p>The toolchain passes the directory as the {@value #RESULTS_DIRECTORY_PROPERTY} system property so that
 * every artifact of a run ends up in one place that can be uploaded as a unit. Launches that do not set it,
 * such as a manually configured run, fall back to a directory inside the game directory.
 */
public final class TestResults
{
	/**
	 * The system property that names the results directory.
	 */
	public static final String RESULTS_DIRECTORY_PROPERTY = "toolchain.gametest.results-dir";

	/**
	 * The fallback directory name, resolved against the game directory.
	 */
	private static final String FALLBACK_DIRECTORY_NAME = "gametest-results";

	/**
	 * The sub-directory that holds one JSON snapshot per failed test.
	 */
	private static final String DIAGNOSTICS_DIRECTORY_NAME = "diagnostics";

	/**
	 * Prevents instantiation.
	 */
	private TestResults()
	{
	}

	/**
	 * Gets the results directory of the current run.
	 *
	 * @return the results directory, which may not exist yet
	 */
	public static Path resultsDirectory()
	{
		var configured = System.getProperty(RESULTS_DIRECTORY_PROPERTY);

		if (configured != null && !configured.isBlank())
		{
			return Path.of(configured);
		}

		return FabricLoader.getInstance().getGameDir().resolve(FALLBACK_DIRECTORY_NAME);
	}

	/**
	 * Gets the directory that holds failure snapshots.
	 *
	 * @return the diagnostics directory, which may not exist yet
	 */
	public static Path diagnosticsDirectory()
	{
		return resultsDirectory().resolve(DIAGNOSTICS_DIRECTORY_NAME);
	}

	/**
	 * Converts a test name into a file-name-safe stem.
	 *
	 * @param testName the test name
	 *
	 * @return the file stem, containing only letters, digits, dots, dashes and underscores
	 */
	public static String fileStem(String testName)
	{
		return testName.replaceAll("[^A-Za-z0-9._-]", "_");
	}
}
