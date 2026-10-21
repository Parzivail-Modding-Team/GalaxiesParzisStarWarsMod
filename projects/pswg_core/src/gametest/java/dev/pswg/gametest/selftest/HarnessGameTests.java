package dev.pswg.gametest.selftest;

import dev.pswg.gametest.GalaxiesGameTestContext;
import dev.pswg.gametest.GalaxiesGameTestSuite;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Self-tests of the GameTest harness itself.
 *
 * <p>They run through the native GameTest lifecycle like any other test, so a harness regression shows up
 * in the same report as the feature tests it would otherwise silently distort.
 */
public final class HarnessGameTests extends GalaxiesGameTestSuite<GalaxiesGameTestContext>
{
	@Override
	protected GalaxiesGameTestContext createContext(GameTestHelper helper, String testName)
	{
		return new GalaxiesGameTestContext(helper, testName);
	}

	/**
	 * Steps run in order, and a pause delays the following step by exactly that many ticks.
	 *
	 * @param test the test context
	 */
	@GameTest(maxTicks = 40)
	public void sequenceStepsRunInOrderAtTheirTicks(GalaxiesGameTestContext test)
	{
		List<String> trace = new ArrayList<>();
		long[] ticks = new long[3];

		test.sequence()
		    .then("first", () ->
		    {
			    trace.add("first");
			    ticks[0] = test.tick();
		    })
		    .then("second, same tick", () ->
		    {
			    trace.add("second");
			    ticks[1] = test.tick();
		    })
		    .afterTicks(5)
		    .then("third, five ticks later", () ->
		    {
			    trace.add("third");
			    ticks[2] = test.tick();
		    })
		    .then("verify", () ->
		    {
			    test.assertValue("step order", trace, List.of("first", "second", "third"));
			    test.assertValue("steps one and two share a tick", ticks[1], ticks[0]);
			    test.assertValue("pause length", ticks[2] - ticks[1], 5L);
		    })
		    .succeed();
	}

	/**
	 * A polled condition completes in the tick it first holds, not earlier and not later.
	 *
	 * @param test the test context
	 */
	@GameTest(maxTicks = 40)
	public void polledConditionCompletesWhenItHolds(GalaxiesGameTestContext test)
	{
		var startTick = test.tick();
		long[] satisfiedAt = new long[1];

		test.sequence()
		    .until("tick 7 reached", () ->
		    {
			    test.assertTrue(test.tick() - startTick >= 7, "not yet");
			    satisfiedAt[0] = test.tick();
		    })
		    .then("verify", () -> test.assertValue("completion tick", satisfiedAt[0] - startTick, 7L))
		    .succeed();
	}

	/**
	 * An unexpected exception becomes an assertion failure with the original exception as its cause, rather
	 * than escaping through the server tick.
	 *
	 * @param test the test context
	 */
	@GameTest
	public void unexpectedExceptionsBecomeAssertionFailures(GalaxiesGameTestContext test)
	{
		var original = new IllegalStateException("boom");
		var translated = test.translate(original);

		test.assertTrue(translated instanceof GameTestAssertException, "translated to an assertion failure");
		test.assertTrue(translated.getCause() == original, "the original exception is kept as the cause");
		test.assertTrue(translated.getMessage().contains("boom"), "the message mentions the original failure");
		test.succeed();
	}

	/**
	 * A connected player joins like a real client, is placed where asked, and is removed again at the end.
	 *
	 * @param test the test context
	 */
	@GameTest(structure = "pswg_gametest:firing_lane", maxTicks = 40)
	public void connectedPlayerJoinsAndLeaves(GalaxiesGameTestContext test)
	{
		var player = test.joinPlayer(GameType.SURVIVAL, new Vec3(2.5, 0, 3.5), -90, 0);
		var expected = test.helper().absoluteVec(new Vec3(2.5, 0, 3.5));

		test.assertTrue(player.connection != null, "the player has a packet listener");
		test.assertTrue(!player.isCreative(), "the player is in survival mode");
		test.assertTrue(player.position().distanceTo(expected) < 0.01, "the player stands where it was placed");
		test.assertTrue(test.server().getPlayerList().getPlayers().contains(player), "the player is on the server's player list");

		test.sequence()
		    .afterTicks(2)
		    .then("still joined after ticking", () ->
		    {
			    test.assertTrue(test.server().getPlayerList().getPlayers().contains(player), "still on the player list");
			    test.assertTrue(player.isAlive(), "the player survived two ticks");
		    })
		    .then("leave", test::finish)
		    .then("verify removal", () -> test.assertTrue(!test.server().getPlayerList().getPlayers().contains(player), "removed from the player list"))
		    .succeed();
	}

	/**
	 * The arena fills blocks relative to the test origin, wherever the runner placed the structure.
	 *
	 * @param test the test context
	 */
	@GameTest(structure = "pswg_gametest:firing_lane")
	public void arenaFillsRelativeToTheTestOrigin(GalaxiesGameTestContext test)
	{
		test.arena().fill(new BlockPos(1, 0, 1), new BlockPos(2, 0, 2), Blocks.STONE);
		test.helper().assertBlockPresent(Blocks.STONE, new BlockPos(1, 0, 1));
		test.helper().assertBlockPresent(Blocks.STONE, new BlockPos(2, 0, 2));
		test.helper().assertBlockNotPresent(Blocks.STONE, new BlockPos(3, 0, 3));
		test.succeed();
	}
}
