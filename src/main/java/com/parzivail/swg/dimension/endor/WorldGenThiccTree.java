package com.parzivail.swg.dimension.endor;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenThiccTree extends WorldGenAbstractTree
{
	private final Block log;
	private final Block leaves;
	private final Block spawnOnBlock;

	public WorldGenThiccTree(Block log, Block leaves, Block spawnOnBlock)
	{
		super(true);
		this.log = log;
		this.leaves = leaves;
		this.spawnOnBlock = spawnOnBlock;
	}

	public boolean generate(World world, Random rand, int x, int y, int z, int height, int heightRandomness)
	{
		int treeHeight = rand.nextInt(heightRandomness) + height;
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
							Block block = world.getBlock(j1, i1, k1);

							if (!this.isReplaceable(world, j1, i1, k1))
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
					onPlantGrow(world, x, y - 1, z, x, y, z);
					onPlantGrow(world, x + 1, y - 1, z, x, y, z);
					onPlantGrow(world, x + 1, y - 1, z + 1, x, y, z);
					onPlantGrow(world, x, y - 1, z + 1, x, y, z);
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
							this.setBlockAndNotifyAdequately(world, x1, k2, z1, log, 0);
							this.setBlockAndNotifyAdequately(world, x1 + 1, k2, z1, log, 0);
							this.setBlockAndNotifyAdequately(world, x1, k2, z1 + 1, log, 0);
							this.setBlockAndNotifyAdequately(world, x1 + 1, k2, z1 + 1, log, 0);
							treeTopY = k2;
						}

						if (rand.nextInt(4) == 0 && currentTreeY > treeHeight * 0.7f)
							genLeafLayer(rand, world, x1, z1, treeTopY);
					}

					genLeafLayer(rand, world, x1, z1, treeTopY);

					if (rand.nextBoolean())
					{
						this.setLeaves(world, x1, treeTopY + 2, z1);
						this.setLeaves(world, x1 + 1, treeTopY + 2, z1);
						this.setLeaves(world, x1 + 1, treeTopY + 2, z1 + 1);
						this.setLeaves(world, x1, treeTopY + 2, z1 + 1);
					}

					for (currentTreeY = -3; currentTreeY <= 4; ++currentTreeY)
						for (k2 = -3; k2 <= 4; ++k2)
							if ((currentTreeY != -3 || k2 != -3) && (currentTreeY != -3 || k2 != 4) && (currentTreeY != 4 || k2 != -3) && (currentTreeY != 4 || k2 != 4) && (Math.abs(currentTreeY) < 3 || Math.abs(k2) < 3))
								this.setLeaves(world, x1 + currentTreeY, treeTopY, z1 + k2);

					for (currentTreeY = -1; currentTreeY <= 2; ++currentTreeY)
						for (k2 = -1; k2 <= 2; ++k2)
							if ((currentTreeY < 0 || currentTreeY > 1 || k2 < 0 || k2 > 1) && rand.nextInt(3) <= 0)
							{
								int l3 = rand.nextInt(3) + 2;
								int l2;

								for (l2 = 0; l2 < l3; ++l2)
									this.setBlockAndNotifyAdequately(world, x + currentTreeY, treeTopY - l2 - 1, z + k2, log, 0);

								int i3;

								for (l2 = -1; l2 <= 1; ++l2)
									for (i3 = -1; i3 <= 1; ++i3)
										this.setLeaves(world, x1 + currentTreeY + l2, treeTopY, z1 + k2 + i3);

								for (l2 = -2; l2 <= 2; ++l2)
									for (i3 = -2; i3 <= 2; ++i3)
										if (Math.abs(l2) != 2 || Math.abs(i3) != 2)
											this.setLeaves(world, x1 + currentTreeY + l2, treeTopY - 1, z1 + k2 + i3);
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
				this.setLeaves(world, x + dX, treeTopY + b1, z + dZ);
				this.setLeaves(world, 1 + x - dX, treeTopY + b1, z + dZ);
				this.setLeaves(world, x + dX, treeTopY + b1, 1 + z - dZ);
				this.setLeaves(world, 1 + x - dX, treeTopY + b1, 1 + z - dZ);

				if ((dX > -2 || dZ > -1) && (dX != -1 || dZ != -2))
				{
					byte b2 = 1;
					this.setLeaves(world, x + dX, treeTopY + b2, z + dZ);
					this.setLeaves(world, 1 + x - dX, treeTopY + b2, z + dZ);
					this.setLeaves(world, x + dX, treeTopY + b2, 1 + z - dZ);
					this.setLeaves(world, 1 + x - dX, treeTopY + b2, 1 + z - dZ);
				}
			}
	}


	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		return this.generate(world, random, x, y, z, 5, 1);
	}

	private void setLeaves(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);

		if (block.isAir(world, x, y, z))
			this.setBlockAndNotifyAdequately(world, x, y, z, leaves, 0);
	}

	//Just a helper macro
	private void onPlantGrow(World world, int x, int y, int z, int sourceX, int sourceY, int sourceZ)
	{
		world.getBlock(x, y, z).onPlantGrow(world, x, y, z, sourceX, sourceY, sourceZ);
	}
}
