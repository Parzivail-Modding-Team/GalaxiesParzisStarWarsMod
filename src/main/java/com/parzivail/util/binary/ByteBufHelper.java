package com.parzivail.util.binary;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public class ByteBufHelper
{
	public static void writeQuaternion(ByteBuf buf, Quaternion q)
	{
		buf.writeFloat(q.getW());
		buf.writeFloat(q.getX());
		buf.writeFloat(q.getY());
		buf.writeFloat(q.getZ());
	}

	public static Quaternion readQuaternion(ByteBuf buf)
	{
		var qa = buf.readFloat();
		var qb = buf.readFloat();
		var qc = buf.readFloat();
		var qd = buf.readFloat();
		return new Quaternion(qb, qc, qd, qa);
	}

	public static void writeVec3d(ByteBuf buf, Vec3d v)
	{
		buf.writeDouble(v.x);
		buf.writeDouble(v.y);
		buf.writeDouble(v.z);
	}

	public static Vec3d readVec3d(ByteBuf buf)
	{
		var x = buf.readDouble();
		var y = buf.readDouble();
		var z = buf.readDouble();
		return new Vec3d(x, y, z);
	}
}
