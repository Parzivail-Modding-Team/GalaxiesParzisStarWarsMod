package com.parzivail.util.common;

import com.parzivail.util.math.lwjgl.Matrix4f;
import net.minecraft.nbt.NBTTagCompound;

public class PNbtUtil
{
	public static Matrix4f getMatrix4(NBTTagCompound nbt, String key)
	{
		if (!nbt.hasKey(key))
			return new Matrix4f();
		NBTTagCompound tag = nbt.getCompoundTag(key);
		Matrix4f mat = new Matrix4f();
		mat.m00 = tag.getFloat("m00");
		mat.m01 = tag.getFloat("m01");
		mat.m02 = tag.getFloat("m02");
		mat.m03 = tag.getFloat("m03");
		mat.m10 = tag.getFloat("m10");
		mat.m11 = tag.getFloat("m11");
		mat.m12 = tag.getFloat("m12");
		mat.m13 = tag.getFloat("m13");
		mat.m20 = tag.getFloat("m20");
		mat.m21 = tag.getFloat("m21");
		mat.m22 = tag.getFloat("m22");
		mat.m23 = tag.getFloat("m23");
		mat.m30 = tag.getFloat("m30");
		mat.m31 = tag.getFloat("m31");
		mat.m32 = tag.getFloat("m32");
		mat.m33 = tag.getFloat("m33");
		return mat;
	}

	public static void setMatrix4(NBTTagCompound nbt, String key, Matrix4f mat)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setFloat("m00", mat.m00);
		tag.setFloat("m01", mat.m01);
		tag.setFloat("m02", mat.m02);
		tag.setFloat("m03", mat.m03);
		tag.setFloat("m10", mat.m10);
		tag.setFloat("m11", mat.m11);
		tag.setFloat("m12", mat.m12);
		tag.setFloat("m13", mat.m13);
		tag.setFloat("m20", mat.m20);
		tag.setFloat("m21", mat.m21);
		tag.setFloat("m22", mat.m22);
		tag.setFloat("m23", mat.m23);
		tag.setFloat("m30", mat.m30);
		tag.setFloat("m31", mat.m31);
		tag.setFloat("m32", mat.m32);
		tag.setFloat("m33", mat.m33);
		nbt.setTag(key, tag);
	}
}
