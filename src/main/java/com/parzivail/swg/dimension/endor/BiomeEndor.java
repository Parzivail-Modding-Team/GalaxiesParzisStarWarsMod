package com.parzivail.swg.dimension.endor;

import com.parzivail.swg.registry.BlockRegister;
import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.swg.worldgen.WorldGenFallenLog;
import com.parzivail.swg.worldgen.WorldGenThiccTree;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.world.PBiomeGenBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeEndor extends PBiomeGenBase
{
	//private final WorldGenBetterForest worldGenBetterForest = new WorldGenBetterForest(BlockRegister.endorLog, Blocks.leaves, BlockRegister.fastGrass);
	private final WorldGenThiccTree worldGenThiccTree = new WorldGenThiccTree(BlockRegister.endorLog, Blocks.leaves, BlockRegister.fastGrass);
	private final WorldGenFallenLog worldGenFallenLog = new WorldGenFallenLog(BlockRegister.endorLog);

	public BiomeEndor(int biomeId)
	{
		super(biomeId);
		setBiomeName("The Forest Moon of Endor");
		spawnableCreatureList.clear();
	}

	@Override
	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{
		StructureRegister.structureEngine.genTiles(world, worldX, worldZ);

		double[] weights = ((ChunkProviderEndor)provider).terrain.getBiomeWeightsAt(worldX, worldZ);

		for (int i = 0; i < weights[1] * 2 + 1; i++)
		{
			int k = worldX + rand.nextInt(16) + 8;
			int l = worldZ + rand.nextInt(16) + 8;
			//			if (rand.nextInt(10) == 0)
			//				worldGenBetterForest.generate(world, rand, k, world.getHeightValue(k, l), l, 15, 5, 0);
			//			else
			worldGenThiccTree.generate(world, rand, k, world.getHeightValue(k, l), l, 20, 10);
		}

		if (MathUtil.oneIn(5))
		{
			int sk = worldX + rand.nextInt(16) + 8;
			int sl = worldZ + rand.nextInt(16) + 8;

			int ek = worldX + rand.nextInt(16) + 8;
			int el = worldZ + rand.nextInt(16) + 8;

			worldGenFallenLog.generate(world, rand, sk, 255, sl, ek, 255, el);
		}

		for (int i = 0; i < 10; i++)
		{
			WorldGenerator grass = getRandomWorldGenForGrass(rand);

			int k = worldX + rand.nextInt(16) + 8;
			int l = worldZ + rand.nextInt(16) + 8;
			if (grass instanceof WorldGenDoublePlant)
				((WorldGenDoublePlant)grass).func_150548_a(rand.nextInt(2) + 2);
			grass.generate(world, rand, k, world.getHeightValue(k, l), l);
		}
	}

	public WorldGenerator getRandomWorldGenForGrass(Random rand)
	{
		return rand.nextInt(4) == 0 ? new WorldGenDoublePlant() : new WorldGenTallGrass(Blocks.tallgrass, rand.nextInt(2) + 1);
	}
}