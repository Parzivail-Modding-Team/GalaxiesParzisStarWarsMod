package com.parzivail.util.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class ByteBufHelper
{
	@FunctionalInterface
	public interface ByteBufWriter<T>
	{
		void write(ByteBuf buf, T value);
	}

	@FunctionalInterface
	public interface ByteBufReader<T>
	{
		T read(ByteBuf buf);
	}

	public static <T> void writeNullable(ByteBuf buf, T value, ByteBufWriter<T> writer)
	{
		buf.writeBoolean(value != null);
		if (value != null)
			writer.write(buf, value);
	}

	public static <T> T readNullable(ByteBuf buf, ByteBufReader<T> reader)
	{
		var hasValue = buf.readBoolean();

		if (!hasValue)
			return null;

		return reader.read(buf);
	}

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

	public static Vec3d readVec3dAsSingles(ByteBuf buf)
	{
		var x = buf.readFloat();
		var y = buf.readFloat();
		var z = buf.readFloat();
		return new Vec3d(x, y, z);
	}

	public static void writeVec3dAsSingles(ByteBuf buf, Vec3d v)
	{
		writeVec3f(buf, new Vec3f(v));
	}

	public static void writeVec3f(ByteBuf buf, Vec3f v)
	{
		buf.writeFloat(v.getX());
		buf.writeFloat(v.getY());
		buf.writeFloat(v.getZ());
	}

	public static Vec3f readVec3f(ByteBuf buf)
	{
		var x = buf.readFloat();
		var y = buf.readFloat();
		var z = buf.readFloat();
		return new Vec3f(x, y, z);
	}

	public static void writeEulerAngle(ByteBuf buf, EulerAngle e)
	{
		buf.writeFloat(e.getPitch());
		buf.writeFloat(e.getYaw());
		buf.writeFloat(e.getRoll());
	}

	public static EulerAngle readEulerAngle(ByteBuf buf)
	{
		var pitch = buf.readFloat();
		var yaw = buf.readFloat();
		var roll = buf.readFloat();
		return new EulerAngle(pitch, yaw, roll);
	}
}
