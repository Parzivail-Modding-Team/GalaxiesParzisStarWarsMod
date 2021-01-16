package com.parzivail.pswg.util;

import com.parzivail.pswg.Galaxies;
import com.parzivail.pswg.access.util.Matrix4fAccessUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

public class PIO
{
	public static InputStream getStream(String domain, Identifier resourceLocation)
	{
		return Galaxies.class.getClassLoader().getResourceAsStream(domain + "/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath());
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
		Matrix4fAccessUtil.set(mat, a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
		return mat;
	}
}
