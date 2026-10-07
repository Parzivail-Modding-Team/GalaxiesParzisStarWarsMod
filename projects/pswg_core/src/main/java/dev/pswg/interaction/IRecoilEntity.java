package dev.pswg.interaction;

import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

/**
 * Prescribes the functionality that entities must support in order to
 * respond to weapon recoil
 */
public interface IRecoilEntity
{
	/**
	 * Gets the FOV multiplier for this entity due to recoil
	 *
	 * @param entity    The entity being recoiled
	 * @param tickDelta The fractional ticks
	 *
	 * @return The FOV multiplier, where 1 is normal and smaller values increase FOV
	 */
	float pswg$getRecoilFovMultiplier(LivingEntity entity, float tickDelta);

	/**
	 * Gets the timestamp, in ticks, of the last recoil event
	 *
	 * @return The timestamp
	 */
	long pswg$getRecoilTime();

	/**
	 * Adds a finite angular displacement in degrees, distributed over the supplied recovery ticks.
	 */
	void pswg$addRecoilImpulse(Vector3f degrees, int recoveryTicks);
}
