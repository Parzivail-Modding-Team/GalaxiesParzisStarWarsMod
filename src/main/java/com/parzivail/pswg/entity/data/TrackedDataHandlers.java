package com.parzivail.pswg.entity.data;

import com.parzivail.pswg.util.Rotation;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.PacketByteBuf;

public class TrackedDataHandlers
{
	public static final TrackedDataHandler<Rotation> ROTATION = new TrackedDataHandler<Rotation>()
	{
		@Override
		public void write(PacketByteBuf data, Rotation rotation)
		{
			rotation.getMatrix().store(data);
		}

		@Override
		public Rotation read(PacketByteBuf buffer)
		{
			Rotation rotation = new Rotation();
			rotation.getMatrix().load(buffer);
			return rotation;
		}

		@Override
		public Rotation copy(Rotation rotation)
		{
			return rotation.clone();
		}
	};

	public static void register()
	{
		TrackedDataHandlerRegistry.register(ROTATION);
	}
}
