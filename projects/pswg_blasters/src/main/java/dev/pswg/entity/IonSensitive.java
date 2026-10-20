package dev.pswg.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * Addon capability for powered entities or block entities. The addon owns recovery.
 */
public interface IonSensitive
{
	/**
	 * Disables this powered object for the given server ticks.
	 */
	void disrupt(ServerLevel world, Entity owner, int durationTicks);
}
