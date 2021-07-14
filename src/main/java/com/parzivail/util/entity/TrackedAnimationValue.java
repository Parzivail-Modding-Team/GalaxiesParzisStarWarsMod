package com.parzivail.util.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class TrackedAnimationValue
{
	public static class Handler implements TrackedDataHandler<TrackedAnimationValue>
	{
		@Override
		public void write(PacketByteBuf buf, TrackedAnimationValue value)
		{
			buf.writeByte(value.bits);
		}

		@Override
		public TrackedAnimationValue read(PacketByteBuf buf)
		{
			return new TrackedAnimationValue(buf.readByte());
		}

		@Override
		public TrackedAnimationValue copy(TrackedAnimationValue value)
		{
			return new TrackedAnimationValue(value.bits);
		}
	}

	private static final int DIRECTION_MASK = 0b10000000;
	private static final int TIMER_MASK = 0b01111111;

	private byte bits;

	public TrackedAnimationValue(byte bits)
	{
		this.bits = bits;
	}

	public TrackedAnimationValue()
	{
		this((byte)0);
	}

	public byte getTimer()
	{
		return (byte)(bits & TIMER_MASK);
	}

	public boolean isPositiveDirection()
	{
		return (bits & DIRECTION_MASK) != 0;
	}

	public boolean isStopped()
	{
		return getTimer() == 0;
	}

	public void set(boolean direction, byte timer)
	{
		if (direction)
			timer |= DIRECTION_MASK;

		bits = timer;
	}

	public void tick()
	{
		var timer = getTimer();

		if (timer != 0)
			set(isPositiveDirection(), --timer);
	}

	public void startToggled(byte value)
	{
		set(!isPositiveDirection(), value);
	}

	public void start(byte value)
	{
		set(isPositiveDirection(), value);
	}

	public void write(NbtCompound nbt, String name)
	{
		nbt.putByte(name, bits);
	}

	public static TrackedAnimationValue read(NbtCompound nbt, String name)
	{
		return new TrackedAnimationValue(nbt.getByte(name));
	}
}
