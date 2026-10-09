package dev.pswg.interaction;

import net.minecraft.world.InteractionHand;

/**
 * Carries recoil timing captured for one first-person render frame.
 */
public interface IRecoilRenderState
{
	/**
	 * Gets the elapsed recoil time captured during extraction.
	 *
	 * @param hand the rendered hand
	 *
	 * @return the captured recoil time in ticks
	 */
	float pswg$getRecoilTime(InteractionHand hand);

	/**
	 * Updates the recoil timing for the next render submission.
	 *
	 * @param hand       the rendered hand
	 * @param recoilTime the elapsed recoil time in ticks
	 */
	void pswg$setRecoilTime(InteractionHand hand, float recoilTime);
}
