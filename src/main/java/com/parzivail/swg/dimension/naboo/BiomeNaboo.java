package com.parzivail.swg.dimension.naboo;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeNaboo extends BiomeGenBase
{
	public BiomeNaboo(int biomeId)
	{
		super(biomeId);
		this.setBiomeName("Naboo");
		this.spawnableCreatureList.clear();
	}

	@Override
	public void decorate(World world, Random rand, int worldX, int worldZ)
	{
		//		long cPos = ChunkDiff.getChunkPos(worldX >> 4, worldZ >> 4);
		//		ArrayList<Pair<Short, NBTTagCompound>> tileCache = StructureRegister.test.tileInfoCache.get(cPos);
		//
		//		if (tileCache != null)
		//			for (Pair<Short, NBTTagCompound> pair : tileCache)
		//			{
		//				TileEntity te = world.getTileEntity(pair.right.getInteger("x"), pair.right.getInteger("y"), pair.right.getInteger("z"));
		//				if (te != null)
		//					te.readFromNBT(pair.right);
		//			}

		//		if (rand.nextInt(1000) == 0)
		//		{
		//			int k = worldX + rand.nextInt(16) + 8;
		//			int l = worldZ + rand.nextInt(16) + 8;
		//			WorldGenDesertWells worldgendesertwells = new WorldGenDesertWells();
		//			worldgendesertwells.generate(world, rand, k, world.getHeightValue(k, l) + 1, l);
		//		}
	}
}