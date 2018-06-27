package com.parzivail.scarif;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SCARIF: Structure Consolidation and Rapid Insertion Format
 */
public class ScarifEngine
{
	private static final ArrayList<ScarifStructure> NO_STRUCTURES = new ArrayList<>();

	private final HashMap<Integer, ArrayList<ScarifStructure>> structures = new HashMap<>();
	private final Logger logger;

	public ScarifEngine(String modid)
	{
		logger = LogManager.getLogger("SCARIF/" + modid.toUpperCase());
	}

	public void load(int dimension, ResourceLocation filename)
	{
		try
		{
			ScarifStructure s = ScarifStructure.load(filename);
			addToDimension(dimension, s);
			logger.info(String.format("Loaded %s", filename.getResourcePath()));
		}
		catch (Exception exception)
		{
			CrashReport crashreport = CrashReport.makeCrashReport(exception, "Loading SCARIF structure");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Structure being loaded");
			crashreportcategory.addCrashSection("Filename", filename.toString());
			throw new ReportedException(crashreport);
		}
	}

	private void addToDimension(int dimension, ScarifStructure s)
	{
		structures.computeIfAbsent(dimension, k -> new ArrayList<>()).add(s);
	}

	private ArrayList<ScarifStructure> getStructuresForDimension(int dim)
	{
		if (!structures.containsKey(dim))
			return NO_STRUCTURES;
		return structures.get(dim);
	}

	public boolean genStructure(Chunk chunk)
	{
		int dimension = chunk.worldObj.provider.dimensionId;
		int cx = chunk.xPosition;
		int cz = chunk.zPosition;
		long chunkPos = ScarifUtil.getChunkPos(cx, cz);
		boolean hadStructure = false;

		for (ScarifStructure structure : getStructuresForDimension(dimension))
		{
			if (!structure.chunks.containsKey(chunkPos))
				continue;

			hadStructure = true;

			HashMap<Short, ScarifBlock> blocks = structure.chunks.get(chunkPos);
			for (HashMap.Entry<Short, ScarifBlock> blockEntry : blocks.entrySet())
			{
				ScarifBlock block = blockEntry.getValue();
				short key = blockEntry.getKey();
				int blockX = key & 0x0F;
				int blockZ = (key >> 4) & 0x0F;
				int blockY = (key >> 8) & 0xFF;

				int l = blockY >> 4;
				ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

				if (extendedblockstorage == null)
				{
					extendedblockstorage = new ExtendedBlockStorage(l << 4, !chunk.worldObj.provider.hasNoSky);
					chunk.getBlockStorageArray()[l] = extendedblockstorage;
				}

				extendedblockstorage.setExtBlockID(blockX, blockY & 15, blockZ, Block.getBlockFromName(structure.idMap.get(block.id)));
				extendedblockstorage.setExtBlockMetadata(blockX, blockY & 15, blockZ, block.metadata);

				if (block.tileData != null)
				{
					structure.tileInfoCache.computeIfAbsent(chunkPos, k -> new ArrayList<>());
					structure.tileInfoCache.get(chunkPos).add(block.tileData);
				}
			}
		}
		return hadStructure;
	}

	public void genTiles(World world, int worldX, int worldZ)
	{
		long cPos = ScarifUtil.getChunkPos(worldX >> 4, worldZ >> 4);
		for (ScarifStructure diff : getStructuresForDimension(world.provider.dimensionId))
		{
			ArrayList<NBTTagCompound> tileCache = diff.tileInfoCache.get(cPos);

			if (tileCache != null)
				for (NBTTagCompound pair : tileCache)
				{
					TileEntity te = world.getTileEntity(pair.getInteger("x"), pair.getInteger("y"), pair.getInteger("z"));
					if (te != null)
						te.readFromNBT(pair);
				}
		}
	}
}
