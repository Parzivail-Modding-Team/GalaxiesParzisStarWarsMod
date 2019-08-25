package com.parzivail.scarif;

import com.parzivail.util.binary.BinaryUtil;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SCARIF: Structure Consolidation and Rapid Insertion Format
 */
public class ScarifEngine
{
	private static final ArrayList<ScarifStructure> NO_STRUCTURES = new ArrayList<>();

	private final HashMap<Integer, ArrayList<ScarifStructure>> structures = new HashMap<>();
	private final Logger logger;
	private List<Short> erroredBlocks = new ArrayList<>();

	public ScarifEngine(String modid)
	{
		logger = LogManager.getLogger("SCARIF/" + modid.toUpperCase());
	}

	public void load(int dimension, ResourceLocation filename)
	{
		try
		{
			ScarifStructure s = ScarifStructure.read(filename);
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

	public boolean genStructure(int dimId, int x, int z, ChunkPrimer chunk)
	{
		long chunkPos = BinaryUtil.encodeChunkPos(x, z);
		boolean hadStructure = false;

		for (ScarifStructure structure : getStructuresForDimension(dimId))
		{
			if (!structure.hasChunk(chunkPos))
				continue;

			hadStructure = true;

			ScarifBlock[] blocks = structure.getChunk(chunkPos);
			for (ScarifBlock block : blocks)
			{
				short pos = block.pos;
				int blockX = pos & 0x0F;
				int blockZ = (pos >> 4) & 0x0F;
				int blockY = (pos >> 8) & 0xFF;

				Block b = Block.getBlockFromName(structure.getBlockById(block.id));
				if (b == null)
					informBlockNotFound(structure, block);
				else
				{
					chunk.setBlockState(blockX, blockY, blockZ, b.getStateFromMeta(block.metadata));

					if (block.tileData != null)
						structure.cacheTile(chunkPos, block.tileData);
				}
			}
		}
		return hadStructure;
	}

	private void informBlockNotFound(ScarifStructure structure, ScarifBlock block)
	{
		if (erroredBlocks.contains(block.id))
			return;
		erroredBlocks.add(block.id);
		Lumberjack.err("Structure block not found: " + structure.getBlockById(block.id));
	}

	public void genTiles(World world, int worldX, int worldZ)
	{
		long cPos = BinaryUtil.encodeChunkPos(worldX >> 4, worldZ >> 4);
		for (ScarifStructure structure : getStructuresForDimension(world.provider.getDimension()))
		{
			ArrayList<NBTTagCompound> tileCache = structure.getCachedTiles(cPos);

			if (tileCache != null)
			{
				for (NBTTagCompound pair : tileCache)
				{
					TileEntity te = world.getTileEntity(new BlockPos(pair.getInteger("x"), pair.getInteger("y"), pair.getInteger("z")));
					if (te != null)
						te.readFromNBT(pair);
				}

				tileCache.clear();
			}
		}
	}
}
