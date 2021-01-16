package com.parzivail.util.binary;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DataReader
{
	public static String readNullTerminatedString(DataInput s) throws IOException
	{
		StringBuilder str = new StringBuilder();
		while (true)
		{
			byte b = s.readByte();
			if (b == 0)
				return str.toString();
			str.append((char)b);
		}
	}

	public static float readHalf(DataInput s) throws IOException
	{
		HalfFloat hf = new HalfFloat(s.readShort());
		return hf.getFullFloat();
	}

	public static int read7BitEncodedInt(InputStream is) throws IOException
	{
		int count = 0;
		int shift = 0;
		boolean more = true;
		while (more)
		{
			byte b = (byte)is.read();
			count |= (b & 0x7F) << shift;
			shift += 7;
			if ((b & 0x80) == 0)
			{
				more = false;
			}
		}
		return count;
	}

	public static CompoundTag readUncompressedNbt(InputStream s, int len) throws IOException
	{
		byte[] bytesNbt = new byte[len];
		int readNbt = s.read(bytesNbt);
		if (readNbt != bytesNbt.length)
			throw new IOException("Invalid NBT length");

		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
		CompoundTag tag = NbtIo.read(stream);
		stream.close();
		return tag;
	}

	public static FileChannel getFile(String domain, Identifier resourceLocation)
	{
		try
		{
			ModContainer container = FabricLoader.getInstance().getModContainer(resourceLocation.getNamespace()).orElseThrow(IllegalStateException::new);
			Path path = container.getPath(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());

			return FileChannel.open(path, StandardOpenOption.READ);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
