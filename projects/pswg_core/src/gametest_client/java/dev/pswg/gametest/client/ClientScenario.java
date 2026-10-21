package dev.pswg.gametest.client;

/**
 * One client GameTest scenario: a complete, self-contained walk through a feature as a real client.
 *
 * <p>The scenario body runs on the client GameTest thread, which Fabric's client GameTest API owns. Blocking
 * waits on the context are therefore legal and advance real client ticks, and the body reads and mutates
 * game state only through the context's client and server accessors.
 */
@FunctionalInterface
public interface ClientScenario
{
	/**
	 * Runs the scenario in a freshly created singleplayer world.
	 *
	 * @param test the context of this scenario
	 *
	 * @throws Exception when the scenario fails; any exception ends the scenario as a failure
	 */
	void run(PswgClientTestContext test) throws Exception;
}
