package dev.pswg.mixin.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.Galaxies;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow
	public abstract Minecraft getMinecraft();

	@Inject(at = @At("HEAD"), method = "bobHurt", cancellable = true)
	void tiltViewWhenHurt(PoseStack matrices, float tickDelta, CallbackInfo ci)
	{
		if (getMinecraft().getCameraEntity() instanceof LivingEntity livingEntity)
			if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(Galaxies.IGNORES_DAMAGE_TILT))
				ci.cancel();
	}
}
