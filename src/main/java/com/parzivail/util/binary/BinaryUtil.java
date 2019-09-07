package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.math.lwjgl.Matrix4f;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.io.*;

public class BinaryUtil
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

	public static Matrix4f readMatrix4(LittleEndianDataInputStream s) throws IOException
	{
		Matrix4f m = new Matrix4f();

		m.m00 = s.readFloat();
		m.m10 = s.readFloat();
		m.m20 = s.readFloat();
		m.m30 = s.readFloat();
		m.m01 = s.readFloat();
		m.m11 = s.readFloat();
		m.m21 = s.readFloat();
		m.m31 = s.readFloat();
		m.m02 = s.readFloat();
		m.m12 = s.readFloat();
		m.m22 = s.readFloat();
		m.m32 = s.readFloat();
		m.m03 = s.readFloat();
		m.m13 = s.readFloat();
		m.m23 = s.readFloat();
		m.m33 = s.readFloat();

		return m;
	}

	public static NBTTagCompound readUncompressedNbt(InputStream s, int len) throws IOException
	{
		byte[] bytesNbt = new byte[len];
		int readNbt = s.read(bytesNbt);
		if (readNbt != bytesNbt.length)
			return null;
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytesNbt));
		NBTTagCompound tag = CompressedStreamTools.read(stream, NBTSizeTracker.INFINITE);
		stream.close();
		return tag;
	}

	/**
	 * Packs a chunk X and Z (in chunk coordinates) Int32s into an Int64
	 *
	 * @param x Chunk X position
	 * @param z Chunk Z position
	 *
	 * @return Packed long
	 */
	public static long encodeChunkPos(int x, int z)
	{
		return (((long)x) << 32) | (z & 0xffffffffL);
	}

	/**
	 * Packs a Y, chunk-local X and chunk-local Z bytes into a Int16
	 *
	 * @param x 0<=x<16 local position
	 * @param y 0<=y<256 local position
	 * @param z 0<=z<16 local position
	 *
	 * @return Packed short
	 */
	public static short encodeBlockPos(short x, short y, short z)
	{
		return (short)((x & 0x0F) | ((z & 0x0F) << 4) | ((y & 0xFF) << 8));
	}

	public static InputStream getResource(Class reference, ResourceLocation resourceLocation)
	{
		return reference.getClassLoader().getResourceAsStream("assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
	}
}
