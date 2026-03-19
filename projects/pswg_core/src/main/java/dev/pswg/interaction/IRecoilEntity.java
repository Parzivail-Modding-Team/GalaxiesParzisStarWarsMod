package dev.pswg.interaction;

import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

/**
 * Prescribes the functionality that entities must support in order to
 * respond to weapon recoil
 */
public interface IRecoilEntity
{
	float RECOIL_DAMPENING = 0.6f;

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
	 * Sets the timestamp, in ticks, of the last recoil event
	 *
	 * @param time The timestamp
	 */
	void pswg$setRecoilTime(long time);

	/**
	 * Gets the angular velocity of the entity's look vector due to recoil,
	 * in degrees per tick, where (x, y, z) is (pitch, yaw, roll).
	 */
	Vector3f pswg$getRecoilVelocity();

	/**
	 * Sets the angular velocity of the entity's look vector due to recoil,
	 * in degrees per tick, where (x, y, z) is (pitch, yaw, roll).
	 *
	 * @param velocity The new velocity
	 */
	void pswg$setRecoilVelocity(Vector3f velocity);

	/**
	 * Increases the angular velocity of the entity's look vector due to recoil,
	 * in degrees per tick, where (x, y, z) is (pitch, yaw, roll).
	 *
	 * @param velocity The new velocity
	 */
	void pswg$addRecoilVelocity(Vector3f velocity);
}
