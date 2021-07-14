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
		var a00 = s.readFloat();
		var a01 = s.readFloat();
		var a02 = s.readFloat();
		var a03 = s.readFloat();
		var a10 = s.readFloat();
		var a11 = s.readFloat();
		var a12 = s.readFloat();
		var a13 = s.readFloat();
		var a20 = s.readFloat();
		var a21 = s.readFloat();
		var a22 = s.readFloat();
		var a23 = s.readFloat();
		var a30 = s.readFloat();
		var a31 = s.readFloat();
		var a32 = s.readFloat();
		var a33 = s.readFloat();

		var mat = new Matrix4f();
		Matrix4fAccessUtil.set(mat, a00, a01, a02, a03, a10, a11, a12, a13, a20, a21, a22, a23, a30, a31, a32, a33);
		return mat;
	}
}
