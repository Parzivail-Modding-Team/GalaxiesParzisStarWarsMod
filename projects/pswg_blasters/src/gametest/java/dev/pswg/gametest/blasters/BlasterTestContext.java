package dev.pswg.gametest.blasters;

import dev.pswg.data.BlasterStats;
import dev.pswg.gametest.GalaxiesGameTestContext;
import dev.pswg.gametest.GalaxiesTestSequence;
import dev.pswg.world.GameTime;
import dev.pswg.item.BlasterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

/**
 * The context of blaster GameTests.
 *
 * <p>It adds the blaster fixtures to the generic context: a shooter who operates weapons through real input
 * packets, an inert target on the firing lane, and assertions that read the state production code keeps in
 * item components. The weapon definition is the data-pack authority, so expectations are derived from it
 * instead of repeating its numbers.
 *
 * <p>The firing lane runs along the positive X axis. The shooter stands near its west end and the targets
 * stand east of the shooter on the same row.
 */
public final class BlasterTestContext extends GalaxiesGameTestContext
{
	/**
	 * The shooter's feet position on the firing lane, relative to the test origin.
	 */
	public static final Vec3 SHOOTER_POSITION = new Vec3(1.5, 0, 3.5);

	/**
	 * The yaw that faces east, along the lane.
	 */
	public static final float FACING_EAST = -90;

	/**
	 * The row of the lane that the shooter and targets stand on.
	 */
	private static final int LANE_ROW = 3;

	/**
	 * Creates the context of one blaster test.
	 *
	 * @param helper   the native helper of the running test
	 * @param testName the human-readable test name
	 */
	public BlasterTestContext(GameTestHelper helper, String testName)
	{
		super(helper, testName);
	}

	/**
	 * Joins a survival-mode shooter at the lane's firing position, facing the targets.
	 *
	 * @return the shooter
	 */
	public BlasterTestPlayer joinShooter()
	{
		return joinShooter(GameType.SURVIVAL);
	}

	/**
	 * Joins a shooter at the lane's firing position, facing the targets.
	 *
	 * @param gameType the shooter's game mode
	 *
	 * @return the shooter
	 */
	public BlasterTestPlayer joinShooter(GameType gameType)
	{
		return new BlasterTestPlayer(joinPlayer(gameType, SHOOTER_POSITION, FACING_EAST, 0));
	}

	/**
	 * Spawns an inert target on the lane.
	 *
	 * <p>The target stands still and never reacts, so a hit is the only thing that can change its health.
	 *
	 * @param type     the target's entity type
	 * @param distance how many blocks east of the shooter's position the target stands
	 * @param <M>      the target's mob type
	 *
	 * @return the spawned target
	 */
	public <M extends Mob> M spawnTarget(EntityType<M> type, int distance)
	{
		var column = (int)SHOOTER_POSITION.x + distance;
		return helper().spawnWithNoFreeWill(type, new BlockPos(column, 0, LANE_ROW));
	}

	/**
	 * Gets how many ticks a weapon needs after it is drawn before it can fire.
	 *
	 * @param blaster the weapon
	 *
	 * @return the draw time from the weapon's authored configuration
	 */
	public int drawTicks(ItemStack blaster)
	{
		return BlasterItem.getLoadout(level(), blaster)
		                  .orElseThrow(() -> new IllegalStateException("Not a resolvable blaster: " + blaster))
		                  .definition()
		                  .stats()
		                  .configuration()
		                  .drawTicks();
	}

	/**
	 * Gets the current effective stats of a weapon in its resting context.
	 *
	 * @param blaster the weapon
	 *
	 * @return the stats the weapon fires with when it is not aiming
	 */
	public BlasterStats stats(ItemStack blaster)
	{
		return BlasterItem.getStats(level(), blaster)
		                  .orElseThrow(() -> new IllegalStateException("Not a resolvable blaster: " + blaster));
	}

	/**
	 * Fails until the world has outlived the initial overcharge window of a weapon.
	 *
	 * <p>A default weapon state records an overcharge that began at game time zero, so in the first
	 * {@code overchargeBonus} ticks of a brand-new world every shot is free of heat. The GameTest world is
	 * brand new, so scenarios that assert on heat wait for this to pass inside {@link
	 * GalaxiesTestSequence#until}. This works around the production defect tracked as pswg-dw1
	 * and can be removed with it.
	 *
	 * @param blaster the weapon whose overcharge window is checked
	 */
	public void assertWorldOutlivedOverchargeWindow(ItemStack blaster)
	{
		var window = stats(blaster).heat().overchargeBonus();

		assertTrue(GameTime.now(level()) > window, "the world is still inside the initial overcharge window of " + window + " ticks");
	}

	/**
	 * Fails the test unless a weapon holds exactly the expected loaded rounds.
	 *
	 * @param description which weapon is checked
	 * @param blaster     the weapon
	 * @param expected    the expected loaded rounds
	 */
	public void assertLoadedRounds(String description, ItemStack blaster, int expected)
	{
		assertValue(description + " loaded rounds", BlasterItem.getLoadedRounds(blaster), expected);
	}
}
