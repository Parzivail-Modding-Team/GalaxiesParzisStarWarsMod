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
	 * Adds an aim impulse and advances this entity's firing-burst profile.
	 *
	 * @param degrees                 Signed pitch/yaw impulse in degrees.
	 * @param sourceSerial            Physical weapon whose burst profile is advancing.
	 * @param recoveryTicks           Quiet ticks before exponential aim recovery and a fresh burst.
	 * @param pitchMultipliers        One-based pitch ramp expanded for this burst.
	 * @param yawCycle                Repeating signed yaw multipliers.
	 */
	void pswg$addRecoilImpulse(
			Vector3f degrees,
			long sourceSerial,
			int recoveryTicks,
			float[] pitchMultipliers,
			float[] yawCycle
	);
}
