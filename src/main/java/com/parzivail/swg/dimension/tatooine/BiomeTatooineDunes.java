package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.util.binary.ChunkDiff;
import com.parzivail.util.common.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
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

	@Override
	public void decorate(World world, Random rand, int worldX, int worldZ)
	{
		long cPos = ChunkDiff.getChunkPos(worldX >> 4, worldZ >> 4);
		ArrayList<Pair<Integer, NBTTagCompound>> tileCache = StructureRegister.test.tileInfoCache.get(cPos);

		if (tileCache != null)
			for (Pair<Integer, NBTTagCompound> pair : tileCache)
			{
				//			int x = (pair.left >> 16) & 0xFF;
				//			int y = (pair.left >> 8) & 0xFF;
				//			int z = pair.left & 0xFF;
				TileEntity te = world.getTileEntity(pair.right.getInteger("x"), pair.right.getInteger("y"), pair.right.getInteger("z"));
				if (te != null)
					te.readFromNBT(pair.right);
			}

		//		if (rand.nextInt(1000) == 0)
		//		{
		//			int k = worldX + rand.nextInt(16) + 8;
		//			int l = worldZ + rand.nextInt(16) + 8;
		//			WorldGenDesertWells worldgendesertwells = new WorldGenDesertWells();
		//			worldgendesertwells.generate(world, rand, k, world.getHeightValue(k, l) + 1, l);
		//		}
	}
}