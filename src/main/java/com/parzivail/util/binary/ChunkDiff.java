package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.common.Lumberjack;
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

public class ChunkDiff
{
	private int version;

	public ChunkDiff(int version)
	{
		this.version = version;
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

			for (int i = 0; i < numChunks; i++)
			{
				int chunkX = s.readInt();
				int chunkZ = s.readInt();
				int numBlocks = s.readInt();

				for (int j = 0; j < numBlocks; j++)
				{
					int x = s.readInt();
					int y = s.readInt();
					int z = s.readInt();
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
							Lumberjack.log(tileTag.toString());
						}
					}
				}
			}

			Lumberjack.log("Built chunkdiff %s", filename.getResourcePath());
			return new ChunkDiff(version);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
