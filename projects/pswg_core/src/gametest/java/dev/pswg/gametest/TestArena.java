package dev.pswg.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

/**
 * Block fixtures inside the bounds of one GameTest structure.
 *
 * <p>Every coordinate is relative to the test origin, so a test never depends on where the native runner
 * placed its structure. The structure's barrier shell already provides a floor, walls and a ceiling, so an
 * arena only adds what a scenario needs on top of that: cover, platforms, or a marked lane.
 */
public final class TestArena
{
	/**
	 * The native helper of the running test.
	 */
	private final GameTestHelper _helper;

	/**
	 * Creates the arena of one test.
	 *
	 * @param helper the native helper of the running test
	 */
	TestArena(GameTestHelper helper)
	{
		_helper = helper;
	}

	/**
	 * Gets the extent of the arena in blocks.
	 *
	 * @return the size along X, Y and Z
	 */
	public BlockPos size()
	{
		AABB bounds = _helper.getRelativeBounds();
		return new BlockPos((int)bounds.getXsize(), (int)bounds.getYsize(), (int)bounds.getZsize());
	}

	/**
	 * Fills a box of blocks, both corners included.
	 *
	 * @param from  one corner, relative to the test origin
	 * @param to    the opposite corner, relative to the test origin
	 * @param block the block to fill with
	 */
	public void fill(BlockPos from, BlockPos to, Block block)
	{
		for (var x = Math.min(from.getX(), to.getX()); x <= Math.max(from.getX(), to.getX()); x++)
		{
			for (var y = Math.min(from.getY(), to.getY()); y <= Math.max(from.getY(), to.getY()); y++)
			{
				for (var z = Math.min(from.getZ(), to.getZ()); z <= Math.max(from.getZ(), to.getZ()); z++)
				{
					_helper.setBlock(new BlockPos(x, y, z), block);
				}
			}
		}
	}
}
