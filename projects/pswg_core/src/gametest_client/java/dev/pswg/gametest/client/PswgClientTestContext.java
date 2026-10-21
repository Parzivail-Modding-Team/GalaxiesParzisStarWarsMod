package dev.pswg.gametest.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.pswg.gametest.TestDiagnostics;
import dev.pswg.gametest.TestResults;
import net.fabricmc.fabric.api.client.gametest.v1.TestInput;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The state and helpers of one client GameTest scenario.
 *
 * <p>The context wraps Fabric's {@link ClientGameTestContext} and the scenario's singleplayer world. Input
 * goes through the real key mappings, so a scenario exercises the game's registered controls, the client
 * tick handlers behind them and the packets they send, never a shortcut into feature code. State is read and
 * changed on the thread that owns it, the client or the integrated server, through
 * {@link #readClient}, {@link #readServer} and {@link #runOnServer}.
 *
 * <p>The context tracks every key a scenario holds and releases them when the scenario ends, so one
 * scenario cannot leak input into the next. When a scenario fails, a JSON snapshot of the client state and a
 * screenshot of what was on screen are written next to the run's other results.
 *
 * <p>All members except {@link #log} must be called on the client GameTest thread.
 */
public final class PswgClientTestContext
{
	/**
	 * The logger shared by all GameTest harness output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger("pswg_gametest");

	/**
	 * The sub-directory of the results directory that holds failure screenshots.
	 */
	private static final String SCREENSHOT_DIRECTORY_NAME = "screenshots";

	/**
	 * Fabric's context for the running client test.
	 */
	private final ClientGameTestContext _context;

	/**
	 * The scenario's singleplayer world.
	 */
	private final TestSingleplayerContext _world;

	/**
	 * The diagnostics collected for the scenario.
	 */
	private final TestDiagnostics _diagnostics;

	/**
	 * The key mappings the scenario currently holds down.
	 */
	private final Set<KeyMapping> _heldKeys;

	/**
	 * Creates the context of one scenario.
	 *
	 * @param context      Fabric's context for the running client test
	 * @param world        the scenario's singleplayer world
	 * @param scenarioName the human-readable scenario name, used for logs and file names
	 */
	PswgClientTestContext(ClientGameTestContext context, TestSingleplayerContext world, String scenarioName)
	{
		_context = Objects.requireNonNull(context, "context");
		_world = Objects.requireNonNull(world, "world");
		_diagnostics = new TestDiagnostics(scenarioName);
		_heldKeys = new LinkedHashSet<>();
	}

	/**
	 * Gets Fabric's context for operations this context does not wrap.
	 *
	 * @return the native client test context
	 */
	public ClientGameTestContext client()
	{
		return _context;
	}

	/**
	 * Gets the scenario's singleplayer world.
	 *
	 * @return the world
	 */
	public TestSingleplayerContext world()
	{
		return _world;
	}

	/**
	 * Gets the integrated server of the scenario's world.
	 *
	 * @return the server context
	 */
	public TestServerContext server()
	{
		return _world.getServer();
	}

	/**
	 * Gets the diagnostics collector of the scenario.
	 *
	 * @return the diagnostics collector
	 */
	public TestDiagnostics diagnostics()
	{
		return _diagnostics;
	}

	/**
	 * Gets the input simulator for operations this context does not wrap.
	 *
	 * <p>Keys held through it are not tracked, so a scenario that uses it must release them itself.
	 *
	 * @return the native input simulator
	 */
	public TestInput input()
	{
		return _context.getInput();
	}

	/**
	 * Records one step of the scenario for the failure snapshot and the harness log.
	 *
	 * @param event the event description
	 */
	public void log(String event)
	{
		_diagnostics.record(event);
	}

	/**
	 * Lets client ticks pass.
	 *
	 * @param ticks the number of ticks to wait
	 */
	public void waitTicks(int ticks)
	{
		log("wait " + ticks + " ticks");
		_context.waitTicks(ticks);
	}

	/**
	 * Waits until a client-side condition holds, failing the scenario after the native default timeout.
	 *
	 * @param description what is being waited for, recorded in the failure snapshot
	 * @param condition   the condition, evaluated on the client thread
	 *
	 * @return the number of ticks that passed
	 */
	public int waitForClient(String description, Predicate<Minecraft> condition)
	{
		log("wait for client: " + description);

		try
		{
			return _context.waitFor(condition);
		}
		catch (AssertionError timeout)
		{
			throw new AssertionError("Timed out waiting for the client: " + description, timeout);
		}
	}

	/**
	 * Waits until a server-side condition holds, failing the scenario after the native default timeout.
	 *
	 * @param description what is being waited for, recorded in the failure snapshot
	 * @param condition   the condition, evaluated on the server thread
	 *
	 * @return the number of ticks that passed
	 */
	public int waitForServer(String description, Predicate<MinecraftServer> condition)
	{
		log("wait for server: " + description);

		try
		{
			return server().waitFor(condition);
		}
		catch (AssertionError timeout)
		{
			throw new AssertionError("Timed out waiting for the server: " + description, timeout);
		}
	}

	/**
	 * Reads client state on the client thread.
	 *
	 * @param reader the read operation
	 * @param <T>    the result type
	 *
	 * @return what the reader returned
	 */
	public <T> T readClient(Function<Minecraft, T> reader)
	{
		return _context.computeOnClient(reader::apply);
	}

	/**
	 * Reads server state on the server thread.
	 *
	 * @param reader the read operation
	 * @param <T>    the result type
	 *
	 * @return what the reader returned
	 */
	public <T> T readServer(Function<MinecraftServer, T> reader)
	{
		return server().computeOnServer(reader::apply);
	}

	/**
	 * Changes server state on the server thread.
	 *
	 * @param action the operation
	 */
	public void runOnServer(Consumer<MinecraftServer> action)
	{
		server().runOnServer(action::accept);
	}

	/**
	 * Presses and releases a key mapping and lets a tick pass, like a tap on the key.
	 *
	 * @param key the key mapping
	 */
	public void press(KeyMapping key)
	{
		log("press " + key.getName());
		_context.getInput().pressKey(key);
	}

	/**
	 * Starts holding a key mapping down until {@link #release} or the end of the scenario.
	 *
	 * @param key the key mapping
	 */
	public void hold(KeyMapping key)
	{
		log("hold " + key.getName());
		_heldKeys.add(key);
		_context.getInput().holdKey(key);
	}

	/**
	 * Releases a key mapping this scenario holds.
	 *
	 * @param key the key mapping
	 */
	public void release(KeyMapping key)
	{
		log("release " + key.getName());
		_heldKeys.remove(key);
		_context.getInput().releaseKey(key);
	}

	/**
	 * Fails the scenario when a condition does not hold.
	 *
	 * @param condition the condition that must hold
	 * @param message   the failure message
	 */
	public void assertTrue(boolean condition, String message)
	{
		if (!condition)
		{
			throw new AssertionError(message);
		}
	}

	/**
	 * Fails the scenario when a value differs from its expectation.
	 *
	 * @param name     the human-readable name of the value
	 * @param actual   the observed value
	 * @param expected the expected value
	 * @param <T>      the value type
	 */
	public <T> void assertValue(String name, T actual, T expected)
	{
		if (!Objects.equals(actual, expected))
		{
			throw new AssertionError("Expected " + name + " to be " + expected + " but was " + actual);
		}
	}

	/**
	 * Releases every key the scenario still holds. It is safe to call repeatedly.
	 */
	void releaseHeldInput()
	{
		for (var key : _heldKeys)
		{
			_context.getInput().releaseKey(key);
		}

		_heldKeys.clear();
	}

	/**
	 * Records that the scenario failed: writes a snapshot of the client and takes a screenshot.
	 *
	 * <p>Both artifacts are best-effort. The failure that is being reported may have left the client in a
	 * state where neither can be produced, and that must never replace the original failure.
	 *
	 * @param failure the failure that ended the scenario
	 */
	void reportFailure(Throwable failure)
	{
		var state = new JsonObject();

		try
		{
			state = readClient(PswgClientTestContext::describeClient);
		}
		catch (RuntimeException exception)
		{
			state.addProperty("unavailable", exception.toString());
		}

		_diagnostics.writeSnapshot(state, failure);
		takeFailureScreenshot();
	}

	/**
	 * Saves a screenshot of the client into the results directory.
	 */
	private void takeFailureScreenshot()
	{
		try
		{
			var options = TestScreenshotOptions.of(TestResults.fileStem(_diagnostics.testName()))
			                                   .withDestinationDir(TestResults.resultsDirectory().resolve(SCREENSHOT_DIRECTORY_NAME))
			                                   .disableCounterPrefix();
			_context.takeScreenshot(options);
		}
		catch (RuntimeException exception)
		{
			LOGGER.warn("[{}] could not take a failure screenshot", _diagnostics.testName(), exception);
		}
	}

	/**
	 * Describes what the client shows and holds, on the client thread.
	 *
	 * @param client the client
	 *
	 * @return the client state as JSON
	 */
	private static JsonObject describeClient(Minecraft client)
	{
		var state = new JsonObject();
		var screen = client.gui.screen();
		state.addProperty("screen", screen == null ? "none" : screen.getClass().getName());

		var player = client.player;

		if (player == null || client.level == null)
		{
			state.addProperty("player", "absent");
			return state;
		}

		var access = client.level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		var description = new JsonObject();
		description.addProperty("name", player.getPlainTextName());
		description.addProperty("gameMode", String.valueOf(player.gameMode()));
		description.addProperty("x", player.getX());
		description.addProperty("y", player.getY());
		description.addProperty("z", player.getZ());
		description.addProperty("yaw", player.getYRot());
		description.addProperty("pitch", player.getXRot());
		description.add("mainHand", describeStack(access, player.getMainHandItem()));
		description.add("offHand", describeStack(access, player.getOffhandItem()));
		state.add("player", description);
		return state;
	}

	/**
	 * Describes a stack with the codec the game saves it with.
	 *
	 * @param context the serialization context that resolves registry entries
	 * @param stack   the stack
	 *
	 * @return the stack as JSON
	 */
	private static JsonElement describeStack(RegistryOps<JsonElement> context, ItemStack stack)
	{
		return ItemStack.OPTIONAL_CODEC.encodeStart(context, stack).result().orElseGet(JsonObject::new);
	}
}
