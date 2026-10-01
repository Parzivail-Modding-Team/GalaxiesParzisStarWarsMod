package dev.pswg.interaction;

/**
 * Carries recoil timing captured for one first-person render frame.
 */
public interface IRecoilRenderState
{
	/**
	 * Gets the elapsed recoil time captured during extraction.
	 *
	 * @return the captured recoil time in ticks
	 */
	float pswg$getRecoilTime();

	/**
	 * Updates the recoil timing for the next render submission.
	 *
	 * @param recoilTime the elapsed recoil time in ticks
	 */
	void pswg$setRecoilTime(float recoilTime);
}
