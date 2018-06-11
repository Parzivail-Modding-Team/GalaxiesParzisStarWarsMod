package com.parzivail.swg.dimension.endor;

import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.swg.worldgen.WorldGenBetterForest;
import com.parzivail.util.world.PBiomeGenBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeEndor extends PBiomeGenBase
{
	private WorldGenBetterForest worldGenBetterForest = new WorldGenBetterForest(BlockRegister.endorLog, Blocks.leaves, BlockRegister.fastGrass);

	public BiomeEndor(int biomeId)
	{
		super(biomeId);
		this.setBiomeName("The Forest Moon of Endor");
		this.spawnableCreatureList.clear();
	}

	@Override
	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{
		StructureRegister.genTiles(world, worldX, worldZ);

		double[] weights = ((ChunkProviderEndor)provider).terrain.getBiomeWeightsAt(worldX, worldZ);
		for (int i = 0; i < weights[0] * 8; i++)
		{
			int k = worldX + rand.nextInt(16) + 8;
			int l = worldZ + rand.nextInt(16) + 8;
			worldGenBetterForest.generate(world, rand, k, world.getHeightValue(k, l), l, 15, 5, 0);
		}
	}

	public WorldGenerator getRandomWorldGenForGrass(Random p_76730_1_)
	{
		return p_76730_1_.nextInt(4) == 0 ? new WorldGenTallGrass(Blocks.tallgrass, 2) : new WorldGenTallGrass(Blocks.tallgrass, 1);
	}
}