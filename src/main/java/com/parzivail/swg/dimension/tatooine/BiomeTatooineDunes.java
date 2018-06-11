package com.parzivail.swg.dimension.tatooine;

import com.parzivail.swg.registry.StructureRegister;
import com.parzivail.util.binary.Cdf.ChunkDiff;
import com.parzivail.util.common.Pair;
import com.parzivail.util.world.PBiomeGenBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by colby on 9/13/2017.
 */
public class BiomeTatooineDunes extends PBiomeGenBase
{
	public BiomeTatooineDunes(int biomeId)
	{
		super(biomeId);
		this.setBiomeName("Tatooine");
		this.spawnableCreatureList.clear();

		this.enableRain = false;
		this.enableSnow = false;
	}

	@Override
	public void decorate(IChunkProvider provider, World world, Random rand, int worldX, int worldZ)
	{
		long cPos = ChunkDiff.getChunkPos(worldX >> 4, worldZ >> 4);
		for (ChunkDiff diff : StructureRegister.getStructuresForDimension(world.provider.dimensionId))
		{
			ArrayList<Pair<Short, NBTTagCompound>> tileCache = diff.tileInfoCache.get(cPos);

			if (tileCache != null)
				for (Pair<Short, NBTTagCompound> pair : tileCache)
				{
					TileEntity te = world.getTileEntity(pair.right.getInteger("x"), pair.right.getInteger("y"), pair.right.getInteger("z"));
					if (te != null)
						te.readFromNBT(pair.right);
				}
		}
	}
}