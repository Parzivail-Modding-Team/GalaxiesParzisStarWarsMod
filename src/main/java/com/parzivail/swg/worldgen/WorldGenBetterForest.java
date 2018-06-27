package com.parzivail.swg.worldgen;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class WorldGenBetterForest extends WorldGenAbstractTree
{
	private final Block log;
	private final Block leaves;
	private final Block spawnOnBlock;

	public WorldGenBetterForest(Block log, Block leaves, Block spawnOnBlock)
	{
		super(false);
		this.log = log;
		this.leaves = leaves;
		this.spawnOnBlock = spawnOnBlock;
	}

	public boolean generate(World world, Random random, int x, int y, int z, int height, int heightRandomness, int metadata)
	{
		int l = height + random.nextInt(heightRandomness);

		boolean canGrow = true;

		if (y >= 1 && y + l + 1 <= 256)
		{
			int temp1;
			int temp2;

			for (int y1 = y; y1 <= y + 1 + l; ++y1)
			{
				byte radius = 1;

				if (y1 == y)
				{
					radius = 0;
				}

				if (y1 >= y + 1 + l - 2)
				{
					radius = 2;
				}

				for (temp1 = x - radius; temp1 <= x + radius && canGrow; ++temp1)
				{
					for (temp2 = z - radius; temp2 <= z + radius && canGrow; ++temp2)
					{
						if (y1 >= 0 && y1 < 256)
						{
							Block block = world.getBlock(temp1, y1, temp2);

							if (!isReplaceable(world, temp1, y1, temp2))
							{
								canGrow = false;
							}
						}
						else
						{
							canGrow = false;
						}
					}
				}
			}

			if (!canGrow)
			{
				return false;
			}
			else
			{
				Block block2 = world.getBlock(x, y - 1, z);

				boolean isSoil = block2 == spawnOnBlock;
				if (isSoil && y < 256 - l - 1)
				{
					block2.onPlantGrow(world, x, y - 1, z, x, y, z);
					int k2;

					for (k2 = y - 3 + l; k2 <= y + l; ++k2)
					{
						temp1 = k2 - (y + l);
						temp2 = 1 - temp1 / 2;

						for (int l2 = x - temp2; l2 <= x + temp2; ++l2)
						{
							int l1 = l2 - x;

							for (int i2 = z - temp2; i2 <= z + temp2; ++i2)
							{
								int j2 = i2 - z;

								if (Math.abs(l1) != temp2 || Math.abs(j2) != temp2 || random.nextInt(2) != 0 && temp1 != 0)
								{
									Block block1 = world.getBlock(l2, k2, i2);

									if (block1.isAir(world, l2, k2, i2) || block1.isLeaves(world, l2, k2, i2))
									{
										setBlockAndNotifyAdequately(world, l2, k2, i2, leaves, metadata);
									}
								}
							}
						}
					}

					for (k2 = 0; k2 < l; ++k2)
					{
						Block block3 = world.getBlock(x, y + k2, z);

						if (block3.isAir(world, x, y + k2, z) || block3.isLeaves(world, x, y + k2, z))
						{
							setBlockAndNotifyAdequately(world, x, y + k2, z, log, metadata);
						}
					}

					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean generate(World world, Random random, int x, int y, int z)
	{
		return generate(world, random, x, y, z, 5, 1, 0);
	}
}
