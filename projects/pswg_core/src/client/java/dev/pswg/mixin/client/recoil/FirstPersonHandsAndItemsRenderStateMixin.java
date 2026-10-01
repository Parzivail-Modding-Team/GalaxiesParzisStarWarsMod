package dev.pswg.mixin.client.recoil;

import dev.pswg.interaction.IRecoilRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Adds frame-local recoil timing to Minecraft's extracted first-person state.
 */
@Mixin(FirstPersonHandsAndItemsRenderState.class)
public abstract class FirstPersonHandsAndItemsRenderStateMixin implements IRecoilRenderState
{
	@Unique
	private float _pswgRecoilTime;

	/**
	 * Gets the elapsed recoil time captured during extraction.
	 *
	 * @return the captured recoil time in ticks
	 */
	@Override
	public float pswg$getRecoilTime()
	{
		return _pswgRecoilTime;
	}

	/**
	 * Updates the recoil timing for the next render submission.
	 *
	 * @param recoilTime the elapsed recoil time in ticks
	 */
	@Override
	public void pswg$setRecoilTime(float recoilTime)
	{
		_pswgRecoilTime = recoilTime;
	}
}
