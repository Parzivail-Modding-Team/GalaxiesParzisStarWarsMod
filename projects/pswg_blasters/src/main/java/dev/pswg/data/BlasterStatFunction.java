package dev.pswg.data;

import dev.pswg.codecgenerator.GenerateEnumCodec;
import dev.pswg.generated.codecs.IBlasterStatFunctionCodec;

/**
 * Attachment stat functions.
 */
@GenerateEnumCodec
public enum BlasterStatFunction implements IBlasterStatFunctionCodec
{
	/**
	 * Aiming zoom.
	 */
	ZOOM_MULTIPLIER,

	/**
	 * Aim and presentation recoil.
	 */
	RECOIL_MULTIPLIER,

	/**
	 * Cone spread.
	 */
	SPREAD_MULTIPLIER,

	/**
	 * Heat cooling.
	 */
	COOLING_MULTIPLIER,

	/**
	 * Accepted-shot rate.
	 */
	FIRE_RATE_MULTIPLIER,

	/**
	 * Damage-falloff distance.
	 */
	DAMAGE_RANGE_MULTIPLIER
}
