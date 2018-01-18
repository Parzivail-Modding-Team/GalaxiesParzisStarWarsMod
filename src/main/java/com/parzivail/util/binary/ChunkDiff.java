package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.common.Pair;
import com.sun.media.sound.InvalidDataException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ChunkDiff
{
	private final int version;
	public final HashMap<Long, HashMap<Integer, BlockInfo>> diffMap;
	public HashMap<Long, ArrayList<Pair<Integer, NBTTagCompound>>> tileInfoCache = new HashMap<>();

	private ChunkDiff(int version, HashMap<Long, HashMap<Integer, BlockInfo>> diffMap)
	{
		this.version = version;
		this.diffMap = diffMap;
	}

	public static ChunkDiff Load(ResourceLocation filename)
	{
		try
		{
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
			InputStream fs = res.getInputStream();
			LittleEndianDataInputStream s = new LittleEndianDataInputStream(fs);

			byte[] identr = new byte[3];
			int read = s.read(identr);
			String ident = new String(identr);
			if (!ident.equals("CDF") || read != identr.length)
				throw new InvalidDataException("Input file not CDF structure");

			int version = s.readInt();
			int numChunks = s.readInt();

			HashMap<Long, HashMap<Integer, BlockInfo>> diffMap = new HashMap<>();

			for (int i = 0; i < numChunks; i++)
			{
				int chunkX = s.readInt();
				int chunkZ = s.readInt();
				int numBlocks = s.readInt();

				long pos = getChunkPos(chunkX, chunkZ);

				HashMap<Integer, BlockInfo> blocks = new HashMap<>();

				for (int j = 0; j < numBlocks; j++)
				{
					byte blockPos = s.readByte(); // Format:
					// 0x 0000 1111
					//    xxxx yyyy
					byte x = (byte)((blockPos & 0xF0) >> 4);
					byte z = (byte)(blockPos & 0x0F);
					byte y = s.readByte();
					int id = s.readInt();
					int flags = s.readByte();
					int metadata = 0;
					NBTTagCompound tileTag = null;
					if ((flags & 0b01) == 0b01) // Has metadata
						metadata = s.readInt();
					if ((flags & 0b10) == 0b10) // Has TileNBT
					{
						int len = s.readInt();

						if (len >= 0)
						{
							byte[] bytesNbt = new byte[len];
							int readNbt = s.read(bytesNbt);
							if (readNbt != bytesNbt.length)
								throw new InvalidDataException("Corrupt NBT tag present");
							DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
							tileTag = CompressedStreamTools.func_152456_a(stream, new NBTSizeTracker(2097152L));
							stream.close();
						}
					}
					blocks.put(getBlockPos(x, y, z), new BlockInfo(id, metadata, tileTag));
				}

				diffMap.put(pos, blocks);
			}

			Lumberjack.log("Built chunkdiff %s", filename.getResourcePath());
			return new ChunkDiff(version, diffMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static long getChunkPos(int x, int z)
	{
		return (((long)x) << 32) | (z & 0xffffffffL);
	}

	public static int getBlockPos(int x, int y, int z)
	{
		return (z & 0xFF) | ((y & 0xFF) << 8) | ((x & 0xFF) << 16);
	}

	public static class BlockInfo
	{
		public final int id;
		public final int metadata;
		public final NBTTagCompound tileData;

		public BlockInfo(int id, int metadata, NBTTagCompound tileData)
		{
			this.id = id;
			this.metadata = metadata;
			this.tileData = tileData;
		}
	}
}
