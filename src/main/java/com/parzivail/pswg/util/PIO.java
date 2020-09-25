package com.parzivail.pswg.util;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.Resources;
import com.parzivail.util.math.MatrixExtUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

public class PIO
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

	public static UUID readGuid(InputStream s) throws IOException
	{
		byte[] id = new byte[16];
		int read = s.read(id);
		if (read != id.length)
			throw new IOException("Corrupt GUID");

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (id[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (id[i] & 0xff);

		return new UUID(msb, lsb);
	}

	public static InputStream getStream(String domain, Identifier resourceLocation)
	{
		return Galaxies.class.getClassLoader().getResourceAsStream(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
	}

	public static FileChannel getFile(String domain, Identifier resourceLocation)
	{
		try
		{
			ModContainer container = FabricLoader.getInstance().getModContainer(Resources.MODID).orElseThrow(IllegalStateException::new);
			Path path = container.getPath(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());

			return FileChannel.open(path, StandardOpenOption.READ);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static Matrix4f readMatrix4f(DataInput s) throws IOException
	{
		float a00 = s.readFloat();
		float a01 = s.readFloat();
		float a02 = s.readFloat();
		float a03 = s.readFloat();
		float a10 = s.readFloat();
		float a11 = s.readFloat();
		float a12 = s.readFloat();
		float a13 = s.readFloat();
		float a20 = s.readFloat();
		float a21 = s.readFloat();
		float a22 = s.readFloat();
		float a23 = s.readFloat();
		float a30 = s.readFloat();
		float a31 = s.readFloat();
		float a32 = s.readFloat();
		float a33 = s.readFloat();

		Matrix4f mat = new Matrix4f();
		MatrixExtUtil.set(mat, a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
		return mat;
	}
}
