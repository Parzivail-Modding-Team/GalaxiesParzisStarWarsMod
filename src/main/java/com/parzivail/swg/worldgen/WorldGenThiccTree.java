package com.parzivail.swg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenThiccTree extends WorldGenAbstractTree
{
	private final Block log;
	private final Block leaves;
	private final Block spawnOnBlock;

	public WorldGenThiccTree(Block log, Block leaves, Block spawnOnBlock)
	{
		super(false);
		this.log = log;
		this.leaves = leaves;
		this.spawnOnBlock = spawnOnBlock;
	}

	public boolean generate(World world, Random rand, int x, int y, int z, int height, int heightRandomness)
	{
		int treeHeight = rand.nextInt(heightRandomness) + height;
		//		float leavesBeginPercent = rand.nextFloat() * 0.4f + 0.2f;
		//
		//		while (!world.getBlock(x, y, z).isSideSolid(world, x, y, z, ForgeDirection.UP) && y > 0)
		//			y--;
		//
		//		if (world.getBlock(x, y, z) != spawnOnBlock)
		//			return false;
		//
		//		for (int i = 0; i < treeHeight; i++)
		//		{
		//			world.setBlock(x, y + i, z, log, 0, 2);
		//			world.setBlock(x + 1, y + i, z, log, 0, 2);
		//			world.setBlock(x, y + i, z + 1, log, 0, 2);
		//			world.setBlock(x + 1, y + i, z + 1, log, 0, 2);
		//
		//			if (i > treeHeight * leavesBeginPercent)
		//			{
		//				Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
		//				for (double angle = 0; angle < 180; angle += 45)
		//				{
		//					float rNoise = 1;//rand.nextFloat() * 0.2f + 0.8f;
		//					float width = rNoise * (float)Math.floorMod(treeHeight - i, 9);
		//					for (int radius = 0; radius < width; radius++)
		//					{
		//						int leavesX = x + Math.round(radius * MathHelper.cos((float)(angle / 180 * Math.PI)));
		//						int leavesZ = z + Math.round(radius * MathHelper.sin((float)(angle / 180 * Math.PI)));
		//						chunk.setBlockIDWithMetadata(leavesX & 15, y + i, leavesZ & 15, Blocks.gold_block, 0);
		//					}
		//				}
		//			}
		//		}
		//
		//		return true;

		boolean flag = true;

		if (y >= 1 && y + treeHeight + 1 <= 256)
		{
			int j1;
			int k1;

			for (int i1 = y; i1 <= y + 1 + treeHeight; ++i1)
			{
				byte b0 = 1;

				if (i1 == y)
					b0 = 0;

				if (i1 >= y + 1 + treeHeight - 2)
					b0 = 2;

				for (j1 = x - b0; j1 <= x + b0 && flag; ++j1)
					for (k1 = z - b0; k1 <= z + b0 && flag; ++k1)
						if (i1 >= 0 && i1 < 256)
						{
							if (!isReplaceable(world, j1, i1, k1))
								flag = false;
						}
						else
							flag = false;
			}

			if (!flag)
				return false;
			else
			{
				Block block2 = world.getBlock(x, y - 1, z);

				boolean isSoil = block2 == spawnOnBlock;
				if (isSoil && y < 256 - treeHeight - 1)
				{
					int j3 = rand.nextInt(4);
					j1 = treeHeight - rand.nextInt(4);
					k1 = 2 - rand.nextInt(3);
					int x1 = x;
					int z1 = z;
					int treeTopY = 0;
					int currentTreeY;
					int k2;

					for (currentTreeY = 0; currentTreeY < treeHeight; ++currentTreeY)
					{
						k2 = y + currentTreeY;

						if (currentTreeY >= j1 && k1 > 0)
						{
							x1 += Direction.offsetX[j3];
							z1 += Direction.offsetZ[j3];
							--k1;
						}

						Block block1 = world.getBlock(x1, k2, z1);

						if (block1.isAir(world, x1, k2, z1) || block1.isLeaves(world, x1, k2, z1))
						{
							setBlockAndNotifyAdequately(world, x1, k2, z1, log, 0);
							setBlockAndNotifyAdequately(world, x1 + 1, k2, z1, log, 0);
							setBlockAndNotifyAdequately(world, x1, k2, z1 + 1, log, 0);
							setBlockAndNotifyAdequately(world, x1 + 1, k2, z1 + 1, log, 0);
							treeTopY = k2;
						}

						if (rand.nextInt(4) == 0 && currentTreeY > treeHeight * 0.7f)
							genLeafLayer(rand, world, x1, z1, treeTopY);
					}

					genLeafLayer(rand, world, x1, z1, treeTopY);

					if (rand.nextBoolean())
					{
						setLeaves(world, x1, treeTopY + 2, z1);
						setLeaves(world, x1 + 1, treeTopY + 2, z1);
						setLeaves(world, x1 + 1, treeTopY + 2, z1 + 1);
						setLeaves(world, x1, treeTopY + 2, z1 + 1);
					}

					for (currentTreeY = -3; currentTreeY <= 4; ++currentTreeY)
						for (k2 = -3; k2 <= 4; ++k2)
							if ((currentTreeY != -3 || k2 != -3) && (currentTreeY != -3 || k2 != 4) && (currentTreeY != 4 || k2 != -3) && (currentTreeY != 4 || k2 != 4) && (Math.abs(currentTreeY) < 3 || Math.abs(k2) < 3))
								setLeaves(world, x1 + currentTreeY, treeTopY, z1 + k2);

					for (currentTreeY = -1; currentTreeY <= 2; ++currentTreeY)
						for (k2 = -1; k2 <= 2; ++k2)
							if ((currentTreeY < 0 || currentTreeY > 1 || k2 < 0 || k2 > 1) && rand.nextInt(3) <= 0)
							{
								int l3 = rand.nextInt(3) + 2;
								int l2;

								for (l2 = 0; l2 < l3; ++l2)
									setBlockAndNotifyAdequately(world, x + currentTreeY, treeTopY - l2 - 1, z + k2, log, 0);

								int i3;

								for (l2 = -1; l2 <= 1; ++l2)
									for (i3 = -1; i3 <= 1; ++i3)
										setLeaves(world, x1 + currentTreeY + l2, treeTopY, z1 + k2 + i3);

								for (l2 = -2; l2 <= 2; ++l2)
									for (i3 = -2; i3 <= 2; ++i3)
										if (Math.abs(l2) != 2 || Math.abs(i3) != 2)
											setLeaves(world, x1 + currentTreeY + l2, treeTopY - 1, z1 + k2 + i3);
							}

					return true;
				}
				else
					return false;
			}
		}
		else
			return false;
	}

	private void genLeafLayer(Random rand, World world, int x, int z, int treeTopY)
	{
		int expand = rand.nextInt(3) + 1;
		for (int dX = -expand; dX <= 0; ++dX)
			for (int dZ = -expand; dZ <= 0; ++dZ)
			{
				byte b1 = -1;
				setLeaves(world, x + dX, treeTopY + b1, z + dZ);
				setLeaves(world, 1 + x - dX, treeTopY + b1, z + dZ);
				setLeaves(world, x + dX, treeTopY + b1, 1 + z - dZ);
				setLeaves(world, 1 + x - dX, treeTopY + b1, 1 + z - dZ);

				if ((dX > -2 || dZ > -1) && (dX != -1 || dZ != -2))
				{
					byte b2 = 1;
					setLeaves(world, x + dX, treeTopY + b2, z + dZ);
					setLeaves(world, 1 + x - dX, treeTopY + b2, z + dZ);
					setLeaves(world, x + dX, treeTopY + b2, 1 + z - dZ);
					setLeaves(world, 1 + x - dX, treeTopY + b2, 1 + z - dZ);
				}
			}
	}


	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		return generate(world, random, x, y, z, 5, 1);
	}

	private void setLeaves(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);

		if (block.isAir(world, x, y, z))
		{
			Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
			//this.setBlockAndNotifyAdequately(world, x, y, z, leaves, 0);
			chunk.setBlockIDWithMetadata(x & 15, y, z & 15, leaves, 0);
		}
	}
}
