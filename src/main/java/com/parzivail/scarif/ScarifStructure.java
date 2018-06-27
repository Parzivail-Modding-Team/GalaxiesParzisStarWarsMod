package com.parzivail.scarif;

import com.google.common.io.LittleEndianDataInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.brotli.dec.BrotliInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ScarifStructure
{
	public final int version;
	public final HashMap<Long, ArrayList<ScarifBlock>> chunks;
	public final HashMap<Short, String> idMap;

	public HashMap<Long, ArrayList<NBTTagCompound>> tileInfoCache = new HashMap<>();

	private ScarifStructure(int version, HashMap<Long, ArrayList<ScarifBlock>> chunks, HashMap<Short, String> idMap)
	{
		this.version = version;
		this.chunks = chunks;
		this.idMap = idMap;
	}

	public static ScarifStructure read(ResourceLocation filename) throws IOException
	{
		IResource res = Minecraft.getMinecraft().getResourceManager().getResource(filename);
		InputStream fs = res.getInputStream();
		BrotliInputStream bis = new BrotliInputStream(fs);
		LittleEndianDataInputStream s = new LittleEndianDataInputStream(bis);

		byte[] identBytes = new byte[4];
		int read = s.read(identBytes);
		String ident = new String(identBytes);
		if (!ident.equals("SCRF") || read != identBytes.length)
			throw new IOException("Input file not SCARIF structure");

		int version = s.readInt();
		int numChunks = s.readInt();
		int numIdMapEntries = s.readInt();

		HashMap<Short, String> idMap = new HashMap<>();

		for (int entry = 0; entry < numIdMapEntries; entry++)
		{
			short id = s.readShort();
			String name = ScarifUtil.readNullTerminatedString(s);
			idMap.put(id, name);
		}

		HashMap<Long, ArrayList<ScarifBlock>> diffMap = new HashMap<>();

		for (int chunk = 0; chunk < numChunks; chunk++)
		{
			int chunkX = s.readInt();
			int chunkZ = s.readInt();
			int numBlocks = s.readInt();

			long chunkPos = ScarifUtil.encodeChunkPos(chunkX, chunkZ);
			ArrayList<ScarifBlock> blocks = new ArrayList<>();

			for (int block = 0; block < numBlocks; block++)
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
						tileTag = ScarifUtil.readUncompressedNbt(s, len);
				}

				if (idMap.containsKey(id))
					blocks.add(new ScarifBlock(ScarifUtil.encodeBlockPos(x, y, z), id, metadata, tileTag));
				else
					throw new IOException(String.format("Unknown block ID found: %s", id));
			}

			diffMap.put(chunkPos, blocks);
		}
		s.close();

		return new ScarifStructure(version, diffMap, idMap);
	}
}
