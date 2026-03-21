package dev.pswg.toolchain.fabric;

import dev.pswg.toolchain.runtime.LaunchIdentity;
import dev.pswg.toolchain.runtime.VanillaLaunchConfig;
import dev.pswg.toolchain.runtime.VanillaLaunchMain;

import java.io.IOException;

/**
 * Bootstrap main that prepares a fresh Fabric dev launch bundle and immediately launches it.
 *
 * <p>This keeps PSWG-root IntelliJ runs aligned with the current module outputs instead of relying
 * on a previously serialized {@code launch.json} that may have been prepared against stale class or
 * resource roots.
 */
public final class FabricLaunchMain
{
	/**
	 * Prevents construction.
	 */
	private FabricLaunchMain()
	{
	}

	/**
	 * Prepares a Fabric client launch from CLI flags and launches it immediately.
	 *
	 * @param args command line arguments
	 * @throws IOException if launch preparation fails
	 * @throws InterruptedException if the child process is interrupted
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		String versionId = positionalValue(args, "--version");

		if (versionId == null || versionId.isBlank())
		{
			System.err.println("Usage: --version <id> [--module <id>] [--username <name>] [--uuid <uuid>] [--refresh]");
			System.exit(1);
		}

		String moduleId = positionalValue(args, "--module");
		boolean refresh = hasFlag(args, "--refresh");
		LaunchIdentity identity = new LaunchIdentity(
			valueOrDefault(positionalValue(args, "--username"), LaunchIdentity.DEFAULT_USERNAME),
			valueOrDefault(positionalValue(args, "--uuid"), LaunchIdentity.DEFAULT_UUID)
		);
		VanillaLaunchConfig config = new FabricDevLaunchService().prepareClientLaunch(
			versionId,
			refresh,
			moduleId,
			identity
		);

		VanillaLaunchMain.launch(config);
	}

	/**
	 * Resolves the value that follows a named flag.
	 *
	 * @param args the command line arguments
	 * @param flag the flag name
	 * @return the following value, or {@code null} when absent
	 */
	private static String positionalValue(String[] args, String flag)
	{
		for (int i = 0; i < args.length - 1; i++)
		{
			if (flag.equals(args[i]))
			{
				return args[i + 1];
			}
		}

		return null;
	}

	/**
	 * Checks whether a flag is present.
	 *
	 * @param args the command line arguments
	 * @param flag the flag name
	 * @return {@code true} when the flag is present
	 */
	private static boolean hasFlag(String[] args, String flag)
	{
		for (String arg : args)
		{
			if (flag.equals(arg))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Falls back to a default value when the CLI value is blank.
	 *
	 * @param value the user-provided value
	 * @param defaultValue the default fallback
	 * @return the resolved value
	 */
	private static String valueOrDefault(String value, String defaultValue)
	{
		if (value == null || value.isBlank())
		{
			return defaultValue;
		}

		return value;
	}
}
