package com.parzivail.scarif;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;

public class ScarifIO
{
	public static String readNullTerminatedString(DataInput s)
	{
		StringBuilder str = new StringBuilder();
		try
		{
			while (true)
			{
				byte b = s.readByte();
				if (b == 0)
					return str.toString();
				str.append((char)b);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static NBTTagCompound readUncompressedNbt(InputStream s, int len) throws IOException
	{
		byte[] bytesNbt = new byte[len];
		int readNbt = s.read(bytesNbt);
		if (readNbt != bytesNbt.length)
			return null;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
		NBTTagCompound tag = CompressedStreamTools.func_152456_a(stream, NBTSizeTracker.INFINITE);
		stream.close();
		return tag;
	}
}
