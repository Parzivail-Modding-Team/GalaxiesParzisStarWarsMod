package com.parzivail.swg.registry;

import com.parzivail.swg.Resources;
import com.parzivail.util.binary.Cdf.BlockInfo;
import com.parzivail.util.binary.Cdf.ChunkDiff;
import com.parzivail.util.common.Pair;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class StructureRegister
{
	private static HashMap<Integer, ChunkDiff[]> structures = new HashMap<>();
	private static final ChunkDiff[] NO_STRUCTURES = new ChunkDiff[0];

	public static void register()
	{
		structures.put(Resources.dimIdTatooine, new ChunkDiff[] {
				//ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/test.cdf"))
				//ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/diff.cdf"))
				ChunkDiff.Load(new ResourceLocation(Resources.MODID, "structures/out.cdf"))
		});
	}

	private static ChunkDiff[] getStructuresForDimension(int dim)
	{
		if (!structures.containsKey(dim))
			return NO_STRUCTURES;
		return structures.get(dim);
	}

	public static boolean genStructure(int dimension, int cx, int cz, int blockX, int blockY, int blockZ, ExtendedBlockStorage extendedblockstorage)
	{
		long chunkPos = ChunkDiff.getChunkPos(cx, cz);
		boolean hadStructure = false;

		for (ChunkDiff cdiff : getStructuresForDimension(dimension))
		{
			short bPos = ChunkDiff.getBlockPos((byte)blockX, (byte)blockZ, (byte)blockY);
			BlockInfo block = cdiff.diffMap.get(chunkPos) == null ? null : cdiff.diffMap.get(chunkPos).get(bPos);
			if (block != null)
			{
				hadStructure = true;

				extendedblockstorage.setExtBlockID(blockX, blockZ & 15, blockY, Block.getBlockFromName(block.id));
				extendedblockstorage.setExtBlockMetadata(blockX, blockZ & 15, blockY, block.metadata);

				if (block.tileData != null)
				{
					cdiff.tileInfoCache.putIfAbsent(chunkPos, new ArrayList<>());
					cdiff.tileInfoCache.get(chunkPos).add(new Pair<>(bPos, block.tileData));
				}
			}
		}
		return hadStructure;
	}

	public static void genTiles(World world, int worldX, int worldZ)
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
