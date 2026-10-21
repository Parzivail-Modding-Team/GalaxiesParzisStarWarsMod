package dev.pswg.gametest.client.selftest;

import dev.pswg.gametest.client.PswgClientGameTest;
import dev.pswg.gametest.client.ScenarioRegistry;

/**
 * Self-tests of the client GameTest harness itself.
 *
 * <p>Scenario order is declaration order, which the leak check below relies on.
 */
public final class HarnessClientGameTests extends PswgClientGameTest
{
	@Override
	protected void scenarios(ScenarioRegistry scenarios)
	{
		scenarios.add("freshWorldHasAPlayerOnTheServerAndTheClient", test ->
		{
			test.waitForClient("the client player exists", client -> client.player != null);
			test.waitForServer("the server has one player", server -> server.getPlayerList().getPlayerCount() == 1);

			var serverPlayerName = test.readServer(server -> server.getPlayerList().getPlayers().getFirst().getPlainTextName());
			var clientPlayerName = test.readClient(client -> client.player.getPlainTextName());

			test.assertValue("the client and server agree on the player", clientPlayerName, serverPlayerName);
		});

		scenarios.add("aHeldKeyIsStillDownWhileTheScenarioRuns", test ->
		{
			var forward = test.readClient(client -> client.options.keyUp);

			test.hold(forward);
			test.waitTicks(2);
			test.assertTrue(test.readClient(client -> forward.isDown()), "the held key is down");
		});

		scenarios.add("aHeldKeyDoesNotLeakIntoTheNextScenario", test ->
		{
			var forward = test.readClient(client -> client.options.keyUp);

			test.waitTicks(2);
			test.assertTrue(!test.readClient(client -> forward.isDown()), "the previous scenario's held key was released");
		});
	}
}
