package com.parzivail.util.data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class PacketByteBufHelper
{
	@FunctionalInterface
	public interface PacketByteBufWriter<T>
	{
		void write(PacketByteBuf buf, T value);
	}

	@FunctionalInterface
	public interface PacketByteBufReader<T>
	{
		T read(PacketByteBuf buf);
	}

	public static <T> void writeNullable(PacketByteBuf buf, T value, PacketByteBufWriter<T> writer)
	{
		buf.writeBoolean(value != null);
		if (value != null)
			writer.write(buf, value);
	}

	public static <T> T readNullable(PacketByteBuf buf, PacketByteBufReader<T> reader)
	{
		var hasValue = buf.readBoolean();

		if (!hasValue)
			return null;

		return reader.read(buf);
	}

	public static void writeQuaternion(PacketByteBuf buf, Quaternion q)
	{
		buf.writeFloat(q.getW());
		buf.writeFloat(q.getX());
		buf.writeFloat(q.getY());
		buf.writeFloat(q.getZ());
	}

	public static Quaternion readQuaternion(PacketByteBuf buf)
	{
		var qa = buf.readFloat();
		var qb = buf.readFloat();
		var qc = buf.readFloat();
		var qd = buf.readFloat();
		return new Quaternion(qb, qc, qd, qa);
	}

	public static void writeVec3d(PacketByteBuf buf, Vec3d v)
	{
		buf.writeDouble(v.x);
		buf.writeDouble(v.y);
		buf.writeDouble(v.z);
	}

	public static Vec3d readVec3d(PacketByteBuf buf)
	{
		var x = buf.readDouble();
		var y = buf.readDouble();
		var z = buf.readDouble();
		return new Vec3d(x, y, z);
	}

	public static Vec3d readVec3dAsSingles(PacketByteBuf buf)
	{
		var x = buf.readFloat();
		var y = buf.readFloat();
		var z = buf.readFloat();
		return new Vec3d(x, y, z);
	}

	public static void writeVec3dAsSingles(PacketByteBuf buf, Vec3d v)
	{
		writeVec3f(buf, new Vec3f(v));
	}

	public static void writeVec3f(PacketByteBuf buf, Vec3f v)
	{
		buf.writeFloat(v.getX());
		buf.writeFloat(v.getY());
		buf.writeFloat(v.getZ());
	}

	public static Vec3f readVec3f(PacketByteBuf buf)
	{
		var x = buf.readFloat();
		var y = buf.readFloat();
		var z = buf.readFloat();
		return new Vec3f(x, y, z);
	}

	public static void writeEulerAngle(PacketByteBuf buf, EulerAngle e)
	{
		buf.writeFloat(e.getPitch());
		buf.writeFloat(e.getYaw());
		buf.writeFloat(e.getRoll());
	}

	public static EulerAngle readEulerAngle(PacketByteBuf buf)
	{
		var pitch = buf.readFloat();
		var yaw = buf.readFloat();
		var roll = buf.readFloat();
		return new EulerAngle(pitch, yaw, roll);
	}
}
