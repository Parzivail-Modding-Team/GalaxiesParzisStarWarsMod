package com.parzivail.util.binary.Cdf;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.binary.PIO;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.common.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.brotli.dec.BrotliInputStream;

import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;

public class ChunkDiff
{
	public final int version;
	public final HashMap<Long, HashMap<Short, BlockInfo>> diffMap;
	public final HashMap<Short, String> idMap;

	public HashMap<Long, ArrayList<Pair<Short, NBTTagCompound>>> tileInfoCache = new HashMap<>();

	private ChunkDiff(int version, HashMap<Long, HashMap<Short, BlockInfo>> diffMap, HashMap<Short, String> idMap)
	{
		this.version = version;
		this.diffMap = diffMap;
		this.idMap = idMap;
	}

	public static ChunkDiff Load(ResourceLocation filename)
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
			InputStream fs = res.getInputStream();
			BrotliInputStream bis = new BrotliInputStream(fs);
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(bis);

			byte[] identr = new byte[3];
			int read = s.read(identr);
			String ident = new String(identr);
			if (!ident.equals("CDF") || read != identr.length)
				throw new InvalidObjectException("Input file not CDF structure");

			int version = s.readInt();
			int numChunks = s.readInt();
			int numIdMapEntries = s.readInt();

			HashMap<Short, String> idMap = new HashMap<>();

			for (int j = 0; j < numIdMapEntries; j++)
			{
				short id = s.readShort();
				String name = PIO.readNullTerminatedString(s);
				idMap.put(id, name);
			}

			HashMap<Long, HashMap<Short, BlockInfo>> diffMap = new HashMap<>();

			for (int i = 0; i < numChunks; i++)
			{
				int chunkX = s.readInt();
				int chunkZ = s.readInt();
				int numBlocks = s.readInt();

				long pos = getChunkPos(chunkX, chunkZ);
				HashMap<Short, BlockInfo> blocks = new HashMap<>();

				for (int j = 0; j < numBlocks; j++)
				{
					byte blockPos = s.readByte(); // Format:
					// 0x 0000 1111
					//    xxxx yyyy
					byte x = (byte)((blockPos & 0xF0) >> 4);
					byte z = (byte)(blockPos & 0x0F);
					byte y = s.readByte();
					short id = s.readShort();
					byte flags = s.readByte();

					byte metadata = 0;
					NBTTagCompound tileTag = null;

					if ((flags & 0b01) == 0b01) // Has metadata
						metadata = s.readByte();
					if ((flags & 0b10) == 0b10) // Has TileNBT
					{
						int len = s.readInt();
						if (len >= 0)
							tileTag = PIO.readUncompressedNbt(s, len);
					}

					if (idMap.containsKey(id))
						blocks.put(getBlockPos(x, y, z), new BlockInfo(id, metadata, tileTag));
					else
						Lumberjack.log("[CDF] Skipped block with ID %s", id);
				}

				diffMap.put(pos, blocks);
			}

			Lumberjack.log("[CDF] Built chunkdiff `%s`", filename.getResourcePath());
			return new ChunkDiff(version, diffMap, idMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Packs a chunk X and Z (in chunk coordinates) Int32s into an Int64
	 *
	 * @param x Chunk X position
	 * @param z Chunk Z position
	 * @return Packed long
	 */
	public static long getChunkPos(int x, int z)
	{
		return (((long)x) << 32) | (z & 0xffffffffL);
	}

	/**
	 * Packs a Y, chunk-local X and chunk-local Z bytes into a Int16
	 *
	 * @param x 0<=x<16 local position
	 * @param y 0<=y<256 local position
	 * @param z 0<=z<16 local position
	 * @return Packed short
	 */
	public static short getBlockPos(byte x, byte y, byte z)
	{
		if (x >= 16 || z >= 16)
			Lumberjack.err("X and Z must satisfy 0<=n<16");
		return (short)((x & 0x0F) | ((z & 0x0F) << 4) | ((y & 0xFF) << 8));
	}

}
