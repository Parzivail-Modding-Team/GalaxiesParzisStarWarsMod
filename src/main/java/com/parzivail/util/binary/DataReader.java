package com.parzivail.util.binary;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class DataReader
{
	public static String readNullTerminatedString(DataInput s) throws IOException
	{
		var str = new StringBuilder();
		while (true)
		{
			var b = s.readByte();
			if (b == 0)
				return str.toString();
			str.append((char)b);
		}
	}

	public static int read7BitEncodedInt(InputStream is) throws IOException
	{
		var count = 0;
		var shift = 0;
		var more = true;
		while (more)
		{
			var b = (byte)is.read();
			count |= (b & 0x7F) << shift;
			shift += 7;
			if ((b & 0x80) == 0)
			{
				more = false;
			}
		}
		return count;
	}

	public static NbtCompound readUncompressedNbt(InputStream s, int len) throws IOException
	{
		var bytesNbt = new byte[len];
		var readNbt = s.read(bytesNbt);
		if (readNbt != bytesNbt.length)
			throw new IOException("Invalid NBT length");

		var stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
		var tag = NbtIo.read(stream);
		stream.close();
		return tag;
	}

	public static FileChannel getFile(String domain, Identifier resourceLocation)
	{
		try
		{
			var container = FabricLoader.getInstance().getModContainer(resourceLocation.getNamespace()).orElseThrow(IllegalStateException::new);
			var path = container.getPath(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());

			return FileChannel.open(path, StandardOpenOption.READ);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
