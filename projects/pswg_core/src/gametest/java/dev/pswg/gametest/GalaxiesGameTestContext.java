package dev.pswg.gametest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The state and helpers of one server GameTest.
 *
 * <p>The context wraps Minecraft's {@link GameTestHelper} instead of replacing it: the native helper stays
 * available for any operation the context does not cover, and every operation runs on the server thread that
 * owns the test. Time only advances through {@link #sequence()}, which compiles a readable list of steps into
 * a native {@link net.minecraft.gametest.framework.GameTestSequence}. There is no second scheduler and no
 * extra thread.
 *
 * <p>A context owns what the test creates beyond what vanilla cleans up on success. Connected test players
 * are not removed by the native lifecycle, so the context removes them when the test finishes, fails, or is
 * about to time out. When a test fails, a JSON snapshot of its world and of every registered
 * {@link DiagnosticSource} is written next to the run's reports.
 *
 * <p>Domain layers extend this class to add their own fixtures and assertions.
 */
public class GalaxiesGameTestContext
{
	/**
	 * The native helper of the running test.
	 */
	private final GameTestHelper _helper;

	/**
	 * The diagnostics collected for the running test.
	 */
	private final TestDiagnostics _diagnostics;

	/**
	 * The arena fixtures of the running test.
	 */
	private final TestArena _arena;

	/**
	 * The players this test joined to the server and must remove again.
	 */
	private final List<ConnectedTestPlayer> _players;

	/**
	 * Whether a failure was already reported; later steps of the same test are skipped once it is set.
	 */
	private boolean _failed;

	/**
	 * Whether the context already released everything it owns.
	 */
	private boolean _finished;

	/**
	 * The description and failure message of the most recent unsatisfied polled condition, kept so that a
	 * timeout can say what the test was still waiting for.
	 */
	private String _lastPollFailure;

	/**
	 * Creates the context of one test.
	 *
	 * @param helper   the native helper of the running test
	 * @param testName the human-readable test name, used for logs and the snapshot file name
	 */
	public GalaxiesGameTestContext(GameTestHelper helper, String testName)
	{
		_helper = Objects.requireNonNull(helper, "helper");
		_diagnostics = new TestDiagnostics(testName);
		_arena = new TestArena(helper);
		_players = new ArrayList<>();

		// Vanilla fails a test in the tick after its maximum, no matter what its sequences do in that tick. The
		// hook below runs in that tick, just before the failure, so a timed-out test is diagnosed and cleaned up
		// like any other failure. A test that ended earlier is no longer ticked and never reaches the hook.
		helper.runBeforeTestEnd(() -> helper.runAfterDelay(2, this::timedOut));
	}

	/**
	 * Gets the native helper for operations this context does not wrap.
	 *
	 * @return the native helper
	 */
	public final GameTestHelper helper()
	{
		return _helper;
	}

	/**
	 * Gets the level the test runs in.
	 *
	 * @return the server level
	 */
	public final ServerLevel level()
	{
		return _helper.getLevel();
	}

	/**
	 * Gets the server the test runs on.
	 *
	 * @return the server
	 */
	public final MinecraftServer server()
	{
		return level().getServer();
	}

	/**
	 * Gets the current test tick, which starts at zero when the test method runs.
	 *
	 * @return the test tick
	 */
	public final long tick()
	{
		return _helper.getTick();
	}

	/**
	 * Converts a position relative to the test origin into a world position.
	 *
	 * @param relative the relative block position
	 *
	 * @return the absolute block position
	 */
	public final BlockPos absolute(BlockPos relative)
	{
		return _helper.absolutePos(relative);
	}

	/**
	 * Gets the arena fixtures of this test.
	 *
	 * @return the arena
	 */
	public final TestArena arena()
	{
		return _arena;
	}

	/**
	 * Gets the diagnostics collector of this test.
	 *
	 * @return the diagnostics collector
	 */
	public final TestDiagnostics diagnostics()
	{
		return _diagnostics;
	}

	/**
	 * Starts a sequence of steps that advance with the server's ticks.
	 *
	 * <p>Everything before the first call runs immediately, in the tick the test starts, so a test arranges its
	 * fixtures directly in the test method and describes the timed part through the returned sequence.
	 *
	 * @return a new sequence bound to this test
	 */
	public final GalaxiesTestSequence sequence()
	{
		return new GalaxiesTestSequence(this, _helper.startSequence());
	}

	/**
	 * Joins a connected player to the server and places it in the test arena.
	 *
	 * <p>The player is removed again when the test ends.
	 *
	 * @param gameType the game mode of the player
	 * @param position the player's feet position, relative to the test origin
	 * @param yaw      the player's yaw in degrees; {@code -90} faces east, along the positive X axis
	 * @param pitch    the player's pitch in degrees
	 *
	 * @return the joined player
	 */
	public final ServerPlayer joinPlayer(GameType gameType, Vec3 position, float yaw, float pitch)
	{
		var connected = ConnectedTestPlayer.join(level(), gameType);
		var player = connected.player();
		var absolute = _helper.absoluteVec(position);

		player.snapTo(absolute.x, absolute.y, absolute.z, yaw, pitch);
		_players.add(connected);
		log("joined " + player.getPlainTextName() + " as " + gameType.getName());
		return player;
	}

	/**
	 * Records one step of the test for the failure snapshot and the harness log.
	 *
	 * @param event the event description
	 */
	public final void log(String event)
	{
		_diagnostics.record(tick(), event);
	}

	/**
	 * Fails the test when a condition does not hold.
	 *
	 * @param condition the condition that must hold
	 * @param message   the failure message
	 */
	public final void assertTrue(boolean condition, String message)
	{
		_helper.assertTrue(condition, message);
	}

	/**
	 * Fails the test when a value differs from its expectation.
	 *
	 * @param name     the human-readable name of the value
	 * @param actual   the observed value
	 * @param expected the expected value
	 * @param <T>      the value type
	 */
	public final <T> void assertValue(String name, T actual, T expected)
	{
		_helper.assertValueEqual(actual, expected, name);
	}

	/**
	 * Fails the test immediately.
	 *
	 * @param message the failure message
	 */
	public final void fail(String message)
	{
		_helper.fail(message);
	}

	/**
	 * Ends the test as passed, after releasing everything the test owns.
	 *
	 * <p>A test that arranges and asserts within its first tick calls this directly. A test with timed steps
	 * ends through {@link GalaxiesTestSequence#succeed()} instead.
	 */
	public final void succeed()
	{
		finish();
		_helper.succeed();
	}

	/**
	 * Describes an item stack as JSON with the same codec the game saves it with.
	 *
	 * @param stack the stack to describe
	 *
	 * @return the stack's identity, count and components
	 */
	public final JsonElement describeStack(ItemStack stack)
	{
		return ItemStack.OPTIONAL_CODEC
				.encodeStart(level().registryAccess().createSerializationContext(JsonOps.INSTANCE), stack)
				.getOrThrow(message -> new IllegalStateException("Cannot describe stack: " + message));
	}

	/**
	 * Checks whether a failure was already reported for this test.
	 *
	 * @return {@code true} once the test has failed
	 */
	final boolean hasFailed()
	{
		return _failed;
	}

	/**
	 * Converts an exception into the native assertion failure.
	 *
	 * <p>Vanilla only treats {@link GameTestAssertException} as a failed test step; any other exception
	 * escaping a sequence step would propagate through the server tick and crash the whole run. Wrapping it
	 * keeps one broken test from taking every other test down with it, and the original exception stays
	 * reachable as the cause.
	 *
	 * @param failure the exception that escaped a test step
	 *
	 * @return the failure to throw instead, or the failure itself when it already is an assertion failure
	 */
	public final GameTestAssertException translate(Throwable failure)
	{
		if (failure instanceof GameTestAssertException assertion)
		{
			return assertion;
		}

		var message = failure instanceof GameTestException gameTestException
		              ? gameTestException.getDescription().getString()
		              : "Unexpected " + failure.getClass().getSimpleName() + ": " + failure.getMessage();
		GameTestAssertException translated = _helper.assertionException(Component.literal(message));
		translated.initCause(failure);
		return translated;
	}

	/**
	 * Remembers what the test was still waiting for, for the timeout report.
	 *
	 * @param description what is being waited for
	 * @param failure     why the condition does not hold yet
	 */
	final void notePollFailure(String description, GameTestAssertException failure)
	{
		_lastPollFailure = "'" + description + "' (last result: " + failure.getMessage() + ")";
	}

	/**
	 * Diagnoses a test that is about to be failed by the native timeout.
	 *
	 * <p>The test is deliberately not marked as failed here. Vanilla still evaluates the pending polled
	 * condition one last time and reports that evaluation's assertion message, which says exactly what was
	 * still wrong; marking the test failed first would make that last evaluation pass vacuously and replace the
	 * message with a generic timeout.
	 */
	private void timedOut()
	{
		if (_failed || _finished)
		{
			return;
		}

		var message = _lastPollFailure == null
		              ? "Timed out before the test succeeded"
		              : "Timed out waiting for " + _lastPollFailure;

		_diagnostics.writeSnapshot(captureState(), _helper.assertionException(Component.literal(message)));
		finish();
	}

	/**
	 * Records that the test failed: writes the diagnostics snapshot and releases everything the test owns.
	 *
	 * @param failure the failure that ended the test
	 */
	final void reportFailure(Throwable failure)
	{
		_failed = true;
		_diagnostics.writeSnapshot(captureState(), failure);
		finish();
	}

	/**
	 * Releases everything the test owns. It is safe to call repeatedly.
	 */
	public final void finish()
	{
		if (_finished)
		{
			return;
		}

		_finished = true;

		for (var player : _players)
		{
			player.leave();
		}

		_players.clear();
	}

	/**
	 * Captures the shared world state for a failure snapshot: the tick, the arena bounds, the joined players
	 * and every non-player entity inside the arena.
	 *
	 * @return the world state as JSON
	 */
	private JsonObject captureState()
	{
		var state = new JsonObject();
		state.addProperty("tick", tick());
		state.add("arena", boxJson(_helper.getRelativeBounds()));
		state.add("players", playersJson());
		state.add("entities", entitiesJson());
		return state;
	}

	/**
	 * Describes the players this test joined.
	 *
	 * @return the players as a JSON array
	 */
	private JsonArray playersJson()
	{
		var players = new JsonArray();

		for (var connected : _players)
		{
			var player = connected.player();
			var description = new JsonObject();
			description.addProperty("name", player.getPlainTextName());
			description.addProperty("gameMode", String.valueOf(player.gameMode()));
			description.add("position", vectorJson(_helper.relativeVec(player.position())));
			description.addProperty("yaw", player.getYRot());
			description.addProperty("pitch", player.getXRot());
			description.addProperty("health", player.getHealth());
			description.add("mainHand", describeStack(player.getMainHandItem()));
			description.add("offHand", describeStack(player.getOffhandItem()));
			players.add(description);
		}

		return players;
	}

	/**
	 * Describes every non-player entity inside the arena.
	 *
	 * @return the entities as a JSON array
	 */
	private JsonArray entitiesJson()
	{
		var entities = new JsonArray();
		Collection<Entity> inside = level().getEntitiesOfClass(Entity.class, _helper.getBounds(), entity -> !(entity instanceof Player));

		for (var entity : inside)
		{
			var description = new JsonObject();
			description.addProperty("type", String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType())));
			description.add("position", vectorJson(_helper.relativeVec(entity.position())));

			if (entity instanceof LivingEntity living)
			{
				description.addProperty("health", living.getHealth());
			}

			entities.add(description);
		}

		return entities;
	}

	/**
	 * Converts a vector to a JSON array.
	 *
	 * @param vector the vector
	 *
	 * @return {@code [x, y, z]}
	 */
	private static JsonArray vectorJson(Vec3 vector)
	{
		var array = new JsonArray();
		array.add(vector.x);
		array.add(vector.y);
		array.add(vector.z);
		return array;
	}

	/**
	 * Converts a box to a JSON object.
	 *
	 * @param box the box
	 *
	 * @return the box's minimum and maximum corners
	 */
	private static JsonObject boxJson(AABB box)
	{
		var description = new JsonObject();
		description.add("min", vectorJson(new Vec3(box.minX, box.minY, box.minZ)));
		description.add("max", vectorJson(new Vec3(box.maxX, box.maxY, box.maxZ)));
		return description;
	}
}
