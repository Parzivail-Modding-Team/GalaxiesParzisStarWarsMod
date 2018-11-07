package com.parzivail.util.binary;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.UUID;

/**
 * Created by colby on 12/25/2017.
 */
public class PIO
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

	/**
	 * Get string from binary stream. >So, if len < 0x7F, it is encoded on one
	 * byte as b0 = len >if len < 0x3FFF, is is encoded on 2 bytes as b0 = (len
	 * & 0x7F) | 0x80, b1 = len >> 7 >if len < 0x 1FFFFF, it is encoded on 3
	 * bytes as b0 = (len & 0x7F) | 0x80, b1 = ((len >> 7) & 0x7F) | 0x80, b2 =
	 * len >> 14 etc.
	 *
	 * @param is
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	public static String readLengthPrefixedString(InputStream is) throws IOException
	{
		int val = getStringLength(is);

		byte[] buffer = new byte[val];
		if (is.read(buffer) < 0)
		{
			throw new IOException("EOF");
		}
		return new String(buffer);
	}

	/**
	 * Binary files are encoded with a variable length prefix that tells you
	 * the size of the string. The prefix is encoded in a 7bit format where the
	 * 8th bit tells you if you should continue. If the 8th bit is set it means
	 * you need to read the next byte.
	 *
	 * @param is
	 *
	 * @return
	 */
	private static int getStringLength(InputStream is) throws IOException
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

	public static NBTTagCompound readUncompressedNbt(InputStream s, int len) throws IOException
	{
		byte[] bytesNbt = new byte[len];
		int readNbt = s.read(bytesNbt);
		if (readNbt != bytesNbt.length)
			return null;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
		NBTTagCompound tag = CompressedStreamTools.func_152456_a(stream, new NBTSizeTracker(2097152L));
		stream.close();
		return tag;
	}

	public static UUID readGuid(InputStream s) throws IOException
	{
		byte[] id = new byte[16];
		int read = s.read(id);
		if (read != id.length)
			throw new InvalidObjectException("Corrupt GUID");

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (id[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (id[i] & 0xff);

		return new UUID(msb, lsb);
	}

	public static InputStream getResource(Class reference, ResourceLocation resourceLocation)
	{
		return reference.getClassLoader().getResourceAsStream("assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
	}
}
