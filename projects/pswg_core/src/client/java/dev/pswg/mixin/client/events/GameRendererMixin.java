package dev.pswg.mixin.client.events;

import dev.pswg.Galaxies;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Shadow
	public abstract MinecraftClient getClient();

	@Inject(at = @At("HEAD"), method = "tiltViewWhenHurt", cancellable = true)
	void tiltViewWhenHurt(MatrixStack matrices, float tickDelta, CallbackInfo ci)
	{
		if (getClient().cameraEntity instanceof LivingEntity livingEntity)
			if (livingEntity.getRecentDamageSource() != null && livingEntity.getRecentDamageSource().isIn(Galaxies.IGNORES_DAMAGE_TILT))
				ci.cancel();
	}
}
