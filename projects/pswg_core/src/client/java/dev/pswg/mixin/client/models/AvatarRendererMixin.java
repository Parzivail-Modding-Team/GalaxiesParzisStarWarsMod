package dev.pswg.mixin.client.models;

import dev.pswg.rendering.g3d.G3dArmorRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures armor-driven skin overlay visibility before native player submission.
 */
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin
{
	/**
	 * Keeps vanilla's freshly extracted skin settings and suppresses flagged areas.
	 */
	@Inject(
			method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V",
			at = @At("TAIL")
	)
	private void hideArmorSkinOverlays(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci)
	{
		G3dArmorRenderer.applySkinVisibility(state);
	}
}
