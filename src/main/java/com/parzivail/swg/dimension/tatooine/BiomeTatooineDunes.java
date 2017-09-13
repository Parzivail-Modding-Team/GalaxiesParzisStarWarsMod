package com.parzivail.swg.dimension.tatooine;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeTatooineDunes extends BiomeGenBase
{
	public BiomeTatooineDunes(int biomeId)
	{
		super(biomeId);
		this.setBiomeName("Tatooine Dunes");
		this.spawnableCreatureList.clear();
	}

	public void decorate(World world, Random rand, int worldX, int worldZ)
	{
		//super.decorate(world, rand, worldX, worldZ);

		if (rand.nextInt(1000) == 0)
		{
			int k = worldX + rand.nextInt(16) + 8;
			int l = worldZ + rand.nextInt(16) + 8;
			//			WorldGenDesertWells worldgendesertwells = new WorldGenDesertWells();
			//			worldgendesertwells.generate(world, rand, k, world.getHeightValue(k, l) + 1, l);
		}
	}
}