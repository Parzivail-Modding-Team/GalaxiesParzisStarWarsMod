package dev.pswg.toolchain.runtime;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Bootstrap main that IntelliJ can run to launch the prepared vanilla client process.
 */
public final class VanillaLaunchMain
{
	/**
	 * Prevents construction.
	 */
	private VanillaLaunchMain()
	{
	}

	/**
	 * Launches a prepared vanilla client process from a serialized launch configuration.
	 *
	 * @param args command line arguments
	 * @throws IOException if the launch configuration cannot be read or the child process fails
	 * @throws InterruptedException if the child process is interrupted
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		if (args.length != 1)
		{
			System.err.println("Usage: <launch-config.json>");
			System.exit(1);
		}

		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Path configPath = Path.of(args[0]);
		VanillaLaunchConfig config;

		try (InputStream inputStream = Files.newInputStream(configPath))
		{
			config = mapper.readValue(inputStream, VanillaLaunchConfig.class);
		}

		launch(config);
	}

	/**
	 * Launches a child JVM from an already-resolved launch configuration.
	 *
	 * @param config the prepared launch configuration
	 * @throws IOException if the child process cannot be started
	 * @throws InterruptedException if the child process is interrupted
	 */
	public static void launch(VanillaLaunchConfig config) throws IOException, InterruptedException
	{
		List<String> command = new ArrayList<>();
		command.add(config.javaExecutable());
		command.addAll(config.jvmArgs());
		command.add(config.mainClass());
		command.addAll(config.gameArgs());

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(config.workingDirectory().toFile());
		processBuilder.inheritIO();

		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		if (exitCode != 0)
		{
			System.exit(exitCode);
		}
	}
}
