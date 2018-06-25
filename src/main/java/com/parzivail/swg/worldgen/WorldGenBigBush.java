package com.parzivail.swg.worldgen;

import com.parzivail.util.math.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class WorldGenBigBush
{
	private Block leaves;
	private int minWidth;
	private int maxWidth;

	public WorldGenBigBush(Block leaves, int minWidth, int maxWidth)
	{
		this.leaves = leaves;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
	}

	private void setLeaves(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, this.leaves, 2, 2);
	}

	public boolean generate(World world, Random random, int x, int y, int z)
	{
		if (!world.getBlock(x, y - 1, z).isSideSolid(world, x, y - 1, z, ForgeDirection.UP))
			return false;

		// Choose widths
		int width = random.nextInt(maxWidth - minWidth) + maxWidth;

		for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 20)
		{
			for (int radius = 0; radius < width; radius++)
			{
				int leavesX = x + Math.round(radius * MathHelper.cos((float)angle));
				int leavesZ = z + Math.round(radius * MathHelper.sin((float)angle));
				this.setLeaves(world, leavesX, y, leavesZ);
			}

			for (int radius = 0; radius < width / 2f; radius++)
				if (!MathUtil.oneIn(3))
				{
					int leavesX = x + Math.round(radius * MathHelper.cos((float)angle));
					int leavesZ = z + Math.round(radius * MathHelper.sin((float)angle));
					this.setLeaves(world, leavesX, y + 1, leavesZ);
				}
		}

		return true;
	}
}
