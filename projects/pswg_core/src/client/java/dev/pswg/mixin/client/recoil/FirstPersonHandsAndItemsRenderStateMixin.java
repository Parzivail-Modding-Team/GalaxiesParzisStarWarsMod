package dev.pswg.mixin.client.recoil;

import dev.pswg.interaction.IRecoilRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Adds frame-local recoil timing to Minecraft's extracted first-person state.
 */
@Mixin(FirstPersonHandsAndItemsRenderState.class)
public abstract class FirstPersonHandsAndItemsRenderStateMixin implements IRecoilRenderState
{
	/**
	 * Elapsed recoil time for the main-hand stack.
	 */
	@Unique
	private float _pswgMainRecoilTime;

	/**
	 * Elapsed recoil time for the offhand stack.
	 */
	@Unique
	private float _pswgOffRecoilTime;

	/**
	 * Gets the elapsed recoil time captured during extraction.
	 *
	 * @return the captured recoil time in ticks
	 */
	@Override
	public float pswg$getRecoilTime(InteractionHand hand)
	{
		return hand == InteractionHand.MAIN_HAND ? _pswgMainRecoilTime : _pswgOffRecoilTime;
	}

	/**
	 * Updates the recoil timing for the next render submission.
	 *
	 * @param recoilTime the elapsed recoil time in ticks
	 */
	@Override
	public void pswg$setRecoilTime(InteractionHand hand, float recoilTime)
	{
		if (hand == InteractionHand.MAIN_HAND)
		{
			_pswgMainRecoilTime = recoilTime;
		}
		else
		{
			_pswgOffRecoilTime = recoilTime;
		}
	}
}
