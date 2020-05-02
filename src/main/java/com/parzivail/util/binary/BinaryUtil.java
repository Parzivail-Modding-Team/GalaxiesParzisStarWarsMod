package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.math.MatrixExtUtil;
import net.minecraft.client.util.math.Matrix4f;

import java.io.DataInput;
import java.io.IOException;

public class BinaryUtil
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

	public static Matrix4f readMatrix4(LittleEndianDataInputStream s) throws IOException
	{
		float a00 = s.readFloat();
		float a10 = s.readFloat();
		float a20 = s.readFloat();
		float a30 = s.readFloat();
		float a01 = s.readFloat();
		float a11 = s.readFloat();
		float a21 = s.readFloat();
		float a31 = s.readFloat();
		float a02 = s.readFloat();
		float a12 = s.readFloat();
		float a22 = s.readFloat();
		float a32 = s.readFloat();
		float a03 = s.readFloat();
		float a13 = s.readFloat();
		float a23 = s.readFloat();
		float a33 = s.readFloat();

		Matrix4f m = new Matrix4f();
		MatrixExtUtil.set(m, a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
		return m;
	}
}
