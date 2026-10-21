package dev.pswg.gametest.blasters;

import dev.pswg.Blasters;
import dev.pswg.gametest.GalaxiesGameTestSuite;
import dev.pswg.item.BlasterAmmo;
import dev.pswg.item.BlasterItem;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;

/**
 * Server-side blaster scenarios.
 *
 * <p>Each scenario drives a connected survival player through real input packets and asserts on the state
 * that production code keeps in item components and in the world. The expected numbers come from the
 * weapons' authored data, so rebalancing a weapon does not break a test that only verifies mechanics.
 */
public final class BlasterGameTests extends GalaxiesGameTestSuite<BlasterTestContext>
{
	/**
	 * The structure that provides a long, empty firing lane enclosed by the native barrier shell.
	 */
	private static final String LANE = "pswg_gametest:firing_lane";

	/**
	 * How far east of the shooter the target stands, comfortably inside every shipped weapon's spread cone.
	 */
	private static final int TARGET_DISTANCE = 8;

	@Override
	protected BlasterTestContext createContext(GameTestHelper helper, String testName)
	{
		return new BlasterTestContext(helper, testName);
	}

	/**
	 * One press fires exactly one round: the magazine is debited once, one round of heat is added, the
	 * cadence window opens, and the hitscan trace damages the target in front of the shooter.
	 *
	 * @param test the test context
	 */
	@GameTest(structure = LANE, maxTicks = 160)
	public void singleShotConsumesAmmoAndDealsDamage(BlasterTestContext test)
	{
		var shooter = test.joinShooter();
		var blaster = shooter.hold(
				InteractionHand.MAIN_HAND,
				BlasterTestStacks.loadedBlaster(test.level(), BlasterTestStacks.TEST_BLASTER)
		);
		var stats = test.stats(blaster);
		var target = test.spawnTarget(EntityTypes.COW, TARGET_DISTANCE);
		var initialRounds = BlasterItem.getLoadedRounds(blaster);
		var initialHealth = target.getHealth();

		test.assertTrue(initialRounds > 1, "the blaster starts with ammunition");
		shooter.lookAt(target);

		test.sequence()
		    .until("the world is old enough for heat to accrue (pswg-dw1)", () -> test.assertWorldOutlivedOverchargeWindow(blaster))
		    .afterTicks(test.drawTicks(blaster) + 2)
		    .then("press the trigger", () -> shooter.press(InteractionHand.MAIN_HAND))
		    .until("the shot is committed", () -> test.assertLoadedRounds("after one shot", blaster, initialRounds - 1))
		    .then("verify the shot's effects", () ->
		    {
			    var state = BlasterItem.getState(blaster);

			    test.assertValue("heat added by one round", state.lastTotalHeat(), (float)stats.heat().perRound());
			    test.assertValue("cadence window", state.fireCooldown() - state.lastFired(), (long)stats.automaticRepeatDelay());
			    test.assertValue("damage dealt", initialHealth - target.getHealth(), stats.damage());
		    })
		    .then("release the trigger", () -> shooter.release(InteractionHand.MAIN_HAND))
		    .succeed();
	}

	/**
	 * A reload fills the magazine and moves exactly the missing rounds' worth of charge out of the carried
	 * packs, drawing on several packs and leaving every remainder in place: no charge is lost or created.
	 *
	 * @param test the test context
	 */
	@GameTest(structure = LANE, maxTicks = 160)
	public void reloadConservesPackRemainder(BlasterTestContext test)
	{
		var shooter = test.joinShooter();
		var inventory = shooter.player().getInventory();
		var blaster = shooter.hold(
				InteractionHand.MAIN_HAND,
				BlasterTestStacks.blasterWithRounds(test.level(), BlasterTestStacks.TEST_BLASTER, 10)
		);
		var ammo = test.stats(blaster).ammo();
		var missingRounds = BlasterItem.getAmmoCapacity(ammo) - BlasterItem.getLoadedRounds(blaster);
		var chargeToMove = missingRounds * BlasterAmmo.unitsPerLoadedQuantity(ammo);

		inventory.setItem(1, BlasterTestStacks.pack(Blasters.SMALL_POWER_PACK, 5, 100));
		inventory.setItem(2, BlasterTestStacks.pack(Blasters.POWER_PACK, 500, 500));

		var chargeBefore = totalStoredCharge(shooter);

		test.sequence()
		    .afterTicks(test.drawTicks(blaster) + 2)
		    .then("start the reload", () -> shooter.reload(InteractionHand.MAIN_HAND))
		    .until("the magazine is full", () -> test.assertLoadedRounds("after the reload", blaster, BlasterItem.getAmmoCapacity(ammo)))
		    .then("verify charge conservation", () ->
		    {
			    test.assertValue("charge left in the carried packs", totalStoredCharge(shooter), chargeBefore - chargeToMove);
		    })
		    .succeed();
	}

	/**
	 * Two one-handed weapons are operated independently: pressing one hand's trigger fires only that hand's
	 * weapon and leaves the other weapon's ammunition and cadence untouched.
	 *
	 * @param test the test context
	 */
	@GameTest(structure = LANE, maxTicks = 100)
	public void dualWieldRoutesHandsIndependently(BlasterTestContext test)
	{
		var shooter = test.joinShooter();
		var mainWeapon = shooter.hold(InteractionHand.MAIN_HAND, BlasterTestStacks.loadedBlaster(test.level(), BlasterTestStacks.DL44));
		var offWeapon = shooter.hold(InteractionHand.OFF_HAND, BlasterTestStacks.loadedBlaster(test.level(), BlasterTestStacks.DL44));
		var capacity = BlasterItem.getLoadedRounds(mainWeapon);

		test.sequence()
		    .afterTicks(test.drawTicks(mainWeapon) + 2)
		    .then("press the main hand", () -> shooter.press(InteractionHand.MAIN_HAND))
		    .until("the main hand fired", () -> test.assertLoadedRounds("main hand", mainWeapon, capacity - 1))
		    .then("the off hand is untouched", () ->
		    {
			    test.assertLoadedRounds("off hand", offWeapon, capacity);
			    test.assertValue("off hand last fired", BlasterItem.getState(offWeapon).lastFired(), 0L);
		    })
		    .then("press the off hand", () -> shooter.press(InteractionHand.OFF_HAND))
		    .until("the off hand fired", () -> test.assertLoadedRounds("off hand", offWeapon, capacity - 1))
		    .then("the main hand fired exactly once", () -> test.assertLoadedRounds("main hand", mainWeapon, capacity - 1))
		    .succeed();
	}

	/**
	 * Adds up the charge units stored in every carried charged item.
	 *
	 * @param shooter the player whose inventory is summed
	 *
	 * @return the total stored charge units
	 */
	private static int totalStoredCharge(BlasterTestPlayer shooter)
	{
		var inventory = shooter.player().getInventory();
		var total = 0;

		for (var slot = 0; slot < inventory.getContainerSize(); slot++)
		{
			total += BlasterTestStacks.storedUnits(inventory.getItem(slot));
		}

		return total;
	}
}
