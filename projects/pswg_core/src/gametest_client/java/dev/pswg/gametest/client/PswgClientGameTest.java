package dev.pswg.gametest.client;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The base class of PSWG client GameTests.
 *
 * <p>Fabric's client GameTest API runs one method per registered class, and the first exception aborts
 * everything after it. A subclass instead declares a list of named {@linkplain ClientScenario scenarios}.
 * Each scenario gets a fresh singleplayer world that is closed again when it ends, whatever the outcome, so
 * scenarios cannot contaminate each other. A failing scenario is recorded with a diagnostics snapshot and a
 * screenshot, and the remaining scenarios still run. The class fails at the end, naming every scenario that
 * failed, which turns the single native exit code into a per-scenario report in
 * {@code client-report.xml}.
 *
 * <p>Register the subclass under the {@code fabric-client-gametest} entrypoint of a test mod.
 *
 * <pre>{@code
 * public final class ExampleClientGameTests extends PswgClientGameTest
 * {
 *     @Override
 *     protected void scenarios(ScenarioRegistry scenarios)
 *     {
 *         scenarios.add("tapsTheKey", test -> test.press(ExampleControls.action));
 *     }
 * }
 * }</pre>
 */
public abstract class PswgClientGameTest implements FabricClientGameTest
{
	/**
	 * The logger shared by all GameTest harness output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("pswg_gametest");

	/**
	 * Declares the scenarios of this class.
	 *
	 * @param scenarios the registry to declare them in
	 */
	protected abstract void scenarios(ScenarioRegistry scenarios);

	@Override
	public final void runTest(ClientGameTestContext context)
	{
		var registry = new ScenarioRegistry();
		scenarios(registry);

		List<String> failedScenarios = new ArrayList<>();

		for (var entry : registry.entries())
		{
			var failure = runScenario(context, entry);

			if (failure != null)
			{
				failedScenarios.add(entry.name());
			}
		}

		if (!failedScenarios.isEmpty())
		{
			throw new AssertionError(
					failedScenarios.size() + " client scenario(s) of " + getClass().getSimpleName() + " failed: " + failedScenarios
			);
		}
	}

	/**
	 * Runs one scenario in its own world and records the outcome.
	 *
	 * @param context Fabric's context for the running client test
	 * @param entry   the scenario
	 *
	 * @return the failure that ended the scenario, or {@code null} when it passed
	 */
	private Throwable runScenario(ClientGameTestContext context, ScenarioRegistry.Entry entry)
	{
		var className = getClass().getSimpleName();
		var scenarioName = className + "." + entry.name();
		var started = System.nanoTime();
		Throwable failure = null;

		context.restoreDefaultGameOptions();
		LOGGER.info("[{}] starting", scenarioName);

		try (var world = context.worldBuilder().create())
		{
			var test = new PswgClientTestContext(context, world, scenarioName);

			try
			{
				entry.body().run(test);
			}
			catch (Throwable thrown)
			{
				failure = thrown;
				test.reportFailure(thrown);
			}
			finally
			{
				test.releaseHeldInput();
			}
		}
		catch (Throwable thrown)
		{
			// Creating or closing the world failed, so there is no context to report the failure through.
			failure = failure == null ? thrown : failure;
		}

		var seconds = (System.nanoTime() - started) / 1_000_000_000.0;
		record(className, entry.name(), seconds, failure);
		return failure;
	}

	/**
	 * Records one scenario outcome in the log and the report.
	 *
	 * @param className the class that declared the scenario
	 * @param name      the scenario name
	 * @param seconds   how long the scenario took
	 * @param failure   the failure that ended the scenario, or {@code null}
	 */
	private static void record(String className, String name, double seconds, Throwable failure)
	{
		var message = failure == null ? null : failure.getClass().getSimpleName() + ": " + failure.getMessage();

		if (failure == null)
		{
			LOGGER.info("[{}.{}] passed in {}s", className, name, String.format("%.1f", seconds));
		}
		else
		{
			LOGGER.error("[{}.{}] failed in {}s", className, name, String.format("%.1f", seconds), failure);
		}

		try
		{
			ClientTestReport.record(className, name, seconds, message);
		}
		catch (IOException exception)
		{
			LOGGER.error("The client test report could not be written", exception);
		}
	}
}
