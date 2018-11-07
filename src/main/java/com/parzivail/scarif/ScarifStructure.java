package com.parzivail.scarif;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.binary.PIO;
import com.parzivail.util.binary.brotli.BrotliInputStream;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ScarifStructure
{
	private static final String MAGIC = "SCRF";
	private static final int FLAG_HASMETADATA = 0b01;
	private static final int FLAG_HASNBT = 0b10;

	private final int version;
	private final HashMap<Long, ScarifBlock[]> chunks;
	private final HashMap<Short, String> idMap;
	private final HashMap<Long, ArrayList<NBTTagCompound>> tileInfoCache = new HashMap<>();

	private ScarifStructure(int version, HashMap<Short, String> idMap, HashMap<Long, ScarifBlock[]> chunks)
	{
		this.version = version;
		this.chunks = chunks;
		this.idMap = idMap;
	}

	public static ScarifStructure read(ResourceLocation filename) throws IOException
	{
		InputStream fs = PIO.getResource(StarWarsGalaxy.class, filename);
		BrotliInputStream bis = new BrotliInputStream(fs);
		LittleEndianDataInputStream s = new LittleEndianDataInputStream(bis);

		HashMap<Short, String> idMap = new HashMap<>();
		HashMap<Long, ScarifBlock[]> diffMap = new HashMap<>();

		byte[] identBytes = new byte[MAGIC.length()];
		int read = s.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals(MAGIC) || read != identBytes.length)
			throw new IOException("Input file not SCARIF structure");

		int version = s.readInt();
		int numChunks = s.readInt();
		int numIdMapEntries = s.readInt();

		for (int entryIdx = 0; entryIdx < numIdMapEntries; entryIdx++)
		{
			short id = s.readShort();
			String name = ScarifUtil.readNullTerminatedString(s);
			idMap.put(id, name);
		}

		for (int chunkIdx = 0; chunkIdx < numChunks; chunkIdx++)
		{
			int chunkX = s.readInt();
			int chunkZ = s.readInt();
			int numBlocks = s.readInt();

			ScarifBlock[] blocks = new ScarifBlock[numBlocks];

			for (int blockIdx = 0; blockIdx < numBlocks; blockIdx++)
			{
				// Format:
				// 0x 0000 1111
				//    xxxx yyyy
				byte xz = s.readByte();

				byte x = (byte)((xz & 0xF0) >> 4);
				byte z = (byte)(xz & 0x0F);
				byte y = s.readByte();

				short id = s.readShort();
				byte flags = s.readByte();

				byte metadata = 0;
				NBTTagCompound tileTag = null;

				if ((flags & FLAG_HASMETADATA) == FLAG_HASMETADATA)
					metadata = s.readByte();
				if ((flags & FLAG_HASNBT) == FLAG_HASNBT)
				{
					int len = s.readInt();
					if (len <= 0)
						throw new IOException("Zero-length NBT present");
					tileTag = ScarifUtil.readUncompressedNbt(s, len);
				}

				if (idMap.containsKey(id))
					blocks[blockIdx] = new ScarifBlock(ScarifUtil.encodeBlockPos(x, y, z), id, metadata, tileTag);
				else
					throw new IOException(String.format("Unknown block ID found: %s", id));
			}

			diffMap.put(ScarifUtil.encodeChunkPos(chunkX, chunkZ), blocks);
		}
		s.close();

		return new ScarifStructure(version, idMap, diffMap);
	}

	public boolean hasChunk(long chunkPos)
	{
		return chunks.containsKey(chunkPos);
	}

	public ScarifBlock[] getChunk(long chunkPos)
	{
		return chunks.get(chunkPos);
	}

	public void cacheTile(long chunkPos, NBTTagCompound tileData)
	{
		tileInfoCache.putIfAbsent(chunkPos, new ArrayList<>());
		tileInfoCache.get(chunkPos).add(tileData);
	}

	public ArrayList<NBTTagCompound> getCachedTiles(long chunkPos)
	{
		return tileInfoCache.get(chunkPos);
	}

	public String getBlockById(short id)
	{
		return idMap.get(id);
	}
}
