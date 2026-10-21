package dev.pswg.gametest.blasters.client;

import dev.pswg.Blasters;
import dev.pswg.gametest.blasters.BlasterTestStacks;
import dev.pswg.gametest.client.PswgClientGameTest;
import dev.pswg.gametest.client.PswgClientTestContext;
import dev.pswg.gametest.client.ScenarioRegistry;
import dev.pswg.input.BlasterControls;
import dev.pswg.item.BlasterItem;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

/**
 * Client-side blaster scenarios.
 *
 * <p>Each scenario presses the weapon's real registered key bindings in a real client connected to an
 * integrated server, then checks both ends of the round trip: the server, which owns the weapon's state,
 * and the client, which has to see it synchronized back.
 */
public final class BlasterClientGameTests extends PswgClientGameTest
{
	/**
	 * Client ticks to wait after a weapon is handed over, covering its draw time and the first
	 * synchronization.
	 */
	private static final int SETTLE_TICKS = 12;

	/**
	 * Client ticks to hold the trigger of a charge weapon, comfortably past its minimum charge time and well
	 * short of its maximum.
	 */
	private static final int CHARGE_TICKS = 15;

	/**
	 * The shipped charge mode of the development blaster: fire on release after at least a minimum charge.
	 */
	private static final Identifier CHARGE_MODE = Blasters.id("charge");

	@Override
	protected void scenarios(ScenarioRegistry scenarios)
	{
		scenarios.add("cycleModeKeySelectsTheNextModeOnBothSides", test ->
		{
			giveLoadedBlaster(test);

			var defaultMode = test.readServer(server -> selectedMode(serverPlayer(server).getMainHandItem(), serverPlayer(server)));
			var nextMode = test.readServer(server -> nextMode(serverPlayer(server)));

			test.assertTrue(!defaultMode.equals(nextMode), "the weapon has a second mode to cycle to");
			test.press(BlasterControls.mode);

			test.waitForServer(
					"the server selected " + nextMode,
					server -> selectedMode(serverPlayer(server).getMainHandItem(), serverPlayer(server)).equals(nextMode)
			);
			test.waitForClient(
					"the client sees " + nextMode,
					client -> selectedMode(client.player.getMainHandItem(), client.player).equals(nextMode)
			);
		});

		scenarios.add("chargedShotFiresExactlyOnceOnRelease", test ->
		{
			giveLoadedBlaster(test);
			test.runOnServer(server ->
			{
				var player = serverPlayer(server);
				var selected = BlasterItem.selectMode(player.level(), player.getMainHandItem(), CHARGE_MODE);

				if (!selected)
				{
					throw new IllegalStateException("The development blaster has no " + CHARGE_MODE + " mode");
				}
			});

			var capacity = test.readServer(server -> BlasterItem.getLoadedRounds(serverPlayer(server).getMainHandItem()));
			var attack = test.readClient(client -> client.options.keyAttack);

			test.waitTicks(SETTLE_TICKS);
			test.hold(attack);
			test.waitTicks(CHARGE_TICKS);
			test.assertValue(
					"rounds while the trigger is still held",
					test.readServer(server -> BlasterItem.getLoadedRounds(serverPlayer(server).getMainHandItem())),
					capacity
			);

			test.release(attack);
			test.waitForServer(
					"one round was fired on release",
					server -> BlasterItem.getLoadedRounds(serverPlayer(server).getMainHandItem()) == capacity - 1
			);
			test.waitTicks(SETTLE_TICKS);
			test.assertValue(
					"rounds after the shot settled",
					test.readServer(server -> BlasterItem.getLoadedRounds(serverPlayer(server).getMainHandItem())),
					capacity - 1
			);
		});

		scenarios.add("aimKeyTogglesAimingDownSightsOnBothSides", test ->
		{
			giveLoadedBlaster(test);
			test.waitForServer("the player stands on the ground", server -> serverPlayer(server).onGround());
			test.waitTicks(SETTLE_TICKS);

			test.press(BlasterControls.aim);
			test.waitForServer("the server is aiming", server -> BlasterItem.getState(serverPlayer(server).getMainHandItem()).isAiming());
			test.waitForClient("the client is aiming", client -> BlasterItem.getState(client.player.getMainHandItem()).isAiming());

			test.press(BlasterControls.aim);
			test.waitForServer("the server stopped aiming", server -> !BlasterItem.getState(serverPlayer(server).getMainHandItem()).isAiming());
			test.waitForClient("the client stopped aiming", client -> !BlasterItem.getState(client.player.getMainHandItem()).isAiming());
		});
	}

	/**
	 * Puts the world's only player in survival mode, as a real player would be, hands them a fully loaded test
	 * blaster, and waits until the client holds it.
	 *
	 * @param test the scenario context
	 */
	private static void giveLoadedBlaster(PswgClientTestContext test)
	{
		test.waitForServer("the server has the player", server -> server.getPlayerList().getPlayerCount() == 1);
		test.runOnServer(server ->
		{
			var player = serverPlayer(server);
			player.setGameMode(GameType.SURVIVAL);
			player.setItemInHand(
					InteractionHand.MAIN_HAND,
					BlasterTestStacks.loadedBlaster(player.level(), BlasterTestStacks.TEST_BLASTER)
			);
		});
		test.waitForClient("the client holds the blaster", client -> client.player.getMainHandItem().is(Blasters.BLASTER_ITEM));
	}

	/**
	 * Gets the integrated server's only player.
	 *
	 * @param server the integrated server
	 *
	 * @return the player
	 */
	private static ServerPlayer serverPlayer(MinecraftServer server)
	{
		return server.getPlayerList().getPlayers().getFirst();
	}

	/**
	 * Reads the selected mode of a held weapon.
	 *
	 * @param weapon the weapon
	 * @param holder the player holding it, whose level resolves the weapon's definition
	 *
	 * @return the selected mode identifier
	 */
	private static Identifier selectedMode(ItemStack weapon, Player holder)
	{
		return BlasterItem.getLoadout(holder.level(), weapon)
		                  .orElseThrow(() -> new IllegalStateException("Not a resolvable blaster: " + weapon))
		                  .selectedMode()
		                  .id();
	}

	/**
	 * Finds the mode that follows the selected one in the weapon's mode cycle.
	 *
	 * @param holder the player holding the weapon in the main hand
	 *
	 * @return the next mode identifier
	 */
	private static Identifier nextMode(ServerPlayer holder)
	{
		var loadout = BlasterItem.getLoadout(holder.level(), holder.getMainHandItem()).orElseThrow();
		var modes = loadout.availableModes();
		var index = modes.indexOf(loadout.selectedMode());

		return modes.get((index + 1) % modes.size()).id();
	}
}
