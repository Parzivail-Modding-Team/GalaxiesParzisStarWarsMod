package dev.pswg.interaction;

import org.joml.Vector3f;

/**
 * Prescribes the functionality that entities must support in order to
 * respond to weapon recoil
 */
public interface IRecoilEntity
{
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
