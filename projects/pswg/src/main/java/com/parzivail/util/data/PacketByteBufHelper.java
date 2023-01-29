package com.parzivail.util.data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

	public static <T> void writeMany(PacketByteBuf buf, Collection<T> value, PacketByteBufWriter<T> writer)
	{
		buf.writeInt(value.size());
		for (T t : value)
			writer.write(buf, t);
	}

	public static <T> ArrayList<T> readMany(PacketByteBuf buf, PacketByteBufReader<T> reader)
	{
		var n = buf.readInt();
		var arr = new ArrayList<T>();
		for (var i = 0; i < n; i++)
			arr.add(reader.read(buf));
		return arr;
	}

	public static <TK, TV> void writeHashMap(PacketByteBuf buf, HashMap<TK, TV> map, PacketByteBufWriter<TK> keyWriter, PacketByteBufWriter<TV> valueWriter)
	{
		buf.writeInt(map.size());

		for (var pair : map.entrySet())
		{
			keyWriter.write(buf, pair.getKey());
			valueWriter.write(buf, pair.getValue());
		}
	}

	public static <TK, TV> HashMap<TK, TV> readHashMap(PacketByteBuf buf, PacketByteBufReader<TK> keyReader, PacketByteBufReader<TV> valueReader)
	{
		var size = buf.readInt();

		var map = new HashMap<TK, TV>(size);

		for (var i = 0; i < size; i++)
		{
			var k = keyReader.read(buf);
			var v = valueReader.read(buf);

			map.put(k, v);
		}

		return map;
	}

	public static void writeIdentifier(PacketByteBuf buf, Identifier i)
	{
		buf.writeString(i.toString());
	}

	public static Identifier readIdentifier(PacketByteBuf buf)
	{
		return new Identifier(buf.readString());
	}

	public static void writeQuaternion(PacketByteBuf buf, Quaternionf q)
	{
		buf.writeFloat(q.w);
		buf.writeFloat(q.x);
		buf.writeFloat(q.y);
		buf.writeFloat(q.z);
	}

	public static Quaternionf readQuaternion(PacketByteBuf buf)
	{
		var qa = buf.readFloat();
		var qb = buf.readFloat();
		var qc = buf.readFloat();
		var qd = buf.readFloat();
		return new Quaternionf(qb, qc, qd, qa);
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
		writeVec3f(buf, v.toVector3f());
	}

	public static void writeVec3f(PacketByteBuf buf, Vector3f v)
	{
		buf.writeFloat(v.x);
		buf.writeFloat(v.y);
		buf.writeFloat(v.z);
	}

	public static Vector3f readVec3f(PacketByteBuf buf)
	{
		var x = buf.readFloat();
		var y = buf.readFloat();
		var z = buf.readFloat();
		return new Vector3f(x, y, z);
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
