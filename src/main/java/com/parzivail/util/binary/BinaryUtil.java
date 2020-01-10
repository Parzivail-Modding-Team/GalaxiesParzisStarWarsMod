package com.parzivail.util.binary;

import com.google.common.io.LittleEndianDataInputStream;
import com.parzivail.util.primative.Matrix4f;

import java.io.DataInput;
import java.io.IOException;

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
}
