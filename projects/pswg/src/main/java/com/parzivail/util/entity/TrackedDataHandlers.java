package com.parzivail.util.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import org.joml.Quaternionf;

public class TrackedDataHandlers
{
	public static final TrackedDataHandler<Quaternionf> QUATERNION = new TrackedDataHandler<>()
	{
		@Override
		public void write(PacketByteBuf data, Quaternionf q)
		{
			data.writeFloat(q.w);
			data.writeFloat(q.x);
			data.writeFloat(q.y);
			data.writeFloat(q.z);
		}

		@Override
		public Quaternionf read(PacketByteBuf buffer)
		{
			var a = buffer.readFloat();
			var b = buffer.readFloat();
			var c = buffer.readFloat();
			var d = buffer.readFloat();
			return new Quaternionf(b, c, d, a);
		}

		@Override
		public Quaternionf copy(Quaternionf q)
		{
			return new Quaternionf(q);
		}
	};

	public static final TrackedDataHandler<Short> SHORT = new TrackedDataHandler<>()
	{
		@Override
		public void write(PacketByteBuf data, Short x)
		{
			data.writeShort(x);
		}

		@Override
		public Short read(PacketByteBuf buffer)
		{
			return buffer.readShort();
		}

		@Override
		public Short copy(Short x)
		{
			return x;
		}
	};

	public static void register()
	{
		TrackedDataHandlerRegistry.register(QUATERNION);
		TrackedDataHandlerRegistry.register(SHORT);
	}
}
