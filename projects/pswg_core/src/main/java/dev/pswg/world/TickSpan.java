package dev.pswg.world;

import net.minecraft.world.level.Level;

/**
 * A conversion utility to convert between real-world time spans
 * and durations in ticks, which are set per-world
 */
public final class TickSpan
{
	/**
	 * Converts a time in seconds to a time in ticks, given a world
	 * which defines the tick rate, in ticks per second
	 *
	 * @param world   The world that defines the tick rate
	 * @param seconds The amount of seconds to convert to ticks
	 *
	 * @return A time span, in ticks
	 */
	public static int fromSeconds(Level world, float seconds)
	{
		return (int)(seconds * world.tickRateManager().tickrate());
	}
}
