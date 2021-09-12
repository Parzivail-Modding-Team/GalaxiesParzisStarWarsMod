package com.parzivail.util.entity;

import net.minecraft.util.math.MathHelper;

public class TrackedAnimationValue
{
	private static final int DIRECTION_MASK = 0b10000000;
	private static final int TIMER_MASK = 0b01111111;

	public static byte getTimer(byte bits)
	{
		return (byte)(bits & TIMER_MASK);
	}

	public static float getTimer(byte bits, byte prevBits, float tickDelta)
	{
		if ((bits & DIRECTION_MASK) != (prevBits & DIRECTION_MASK))
			return bits & TIMER_MASK;
		return MathHelper.lerp(tickDelta, prevBits & TIMER_MASK, bits & TIMER_MASK);
	}

	public static boolean isPositiveDirection(byte bits)
	{
		return (bits & DIRECTION_MASK) != 0;
	}

	public static boolean isStopped(byte bits)
	{
		return getTimer(bits) == 0;
	}

	public static byte set(boolean direction, byte timer)
	{
		if (direction)
			timer |= DIRECTION_MASK;

		return timer;
	}

	public static byte tick(byte bits)
	{
		var timer = getTimer(bits);

		if (timer != 0)
			return set(isPositiveDirection(bits), --timer);

		return bits;
	}

	public static byte startToggled(byte bits, byte value)
	{
		return set(!isPositiveDirection(bits), value);
	}

	public static byte start(byte bits, byte value)
	{
		return set(isPositiveDirection(bits), value);
	}
}
