package dev.pswg.mixin.client.recoil;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.interaction.IRecoilRenderState;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.world.InteractionHand;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Applies weapon recoil to first-person item submission after vanilla extraction.
 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin
{
	/**
	 * Adds extracted recoil to the pose used to submit a first-person item.
	 *
	 * @param poseStack the item submission pose
	 * @param state     the first-person state snapshot
	 *
	 * @return the recoil-adjusted pose stack
	 */
	@ModifyArg(
			method = "submitArmWithItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
			),
			index = 0
	)
	private PoseStack applyRecoil(
			PoseStack poseStack,
			@Local(argsOnly = true) FirstPersonHandsAndItemsRenderState state,
			@Local(argsOnly = true) InteractionHand hand
	)
	{
		var recoilTime = ((IRecoilRenderState)state).pswg$getRecoilTime(hand);
		if (recoilTime <= 0 || recoilTime >= 20)
			return poseStack;

		// TODO: configurable per-blaster?
		var effectDepth = 0.4f;
		var recoverSpeed = 0.8f;
		var recoilDuration = 1f;
		var recoilAngle = 25;

		var f = (float)(effectDepth * Math.min(Math.pow(recoilTime / recoilDuration, 2), Math.exp(-recoverSpeed * (recoilTime - recoilDuration))));

		poseStack.rotate(new Quaternionf().rotateX((float)Math.toRadians(f * recoilAngle)));
		poseStack.translate(0, 0, f);
		return poseStack;
	}
}
