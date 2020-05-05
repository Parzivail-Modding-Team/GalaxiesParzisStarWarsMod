package com.parzivail.pswg.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Quaternion;

public class TrackedDataHandlers
{
	public static final TrackedDataHandler<Quaternion> QUATERNION = new TrackedDataHandler<Quaternion>()
	{
		@Override
		public void write(PacketByteBuf data, Quaternion q)
		{
			data.writeFloat(q.getA());
			data.writeFloat(q.getB());
			data.writeFloat(q.getC());
			data.writeFloat(q.getD());
		}

		@Override
		public Quaternion read(PacketByteBuf buffer)
		{
			float a = buffer.readFloat();
			float b = buffer.readFloat();
			float c = buffer.readFloat();
			float d = buffer.readFloat();
			return new Quaternion(b, c, d, a);
		}

		@Override
		public Quaternion copy(Quaternion q)
		{
			return new Quaternion(q);
		}
	};

	public static final TrackedDataHandler<Short> SHORT = new TrackedDataHandler<Short>()
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
