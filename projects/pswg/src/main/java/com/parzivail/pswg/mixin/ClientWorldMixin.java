package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.render.sky.SpaceSkyRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public class ClientWorldMixin
{
	@Inject(method = "getSkyColor(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"), cancellable = true)
	public void getSkyColor(Vec3d cameraPos, float tickDelta, CallbackInfoReturnable<Vec3d> cir)
	{
		@SuppressWarnings("ConstantConditions")
		var self = (ClientWorld)(Object)this;
		SpaceSkyRenderer.clientWorld$getSkyColor(self, cameraPos, tickDelta, cir);
	}

	@Inject(method = "method_23787(F)F", at = @At("HEAD"), cancellable = true)
	public void getStarColor(float tickDelta, CallbackInfoReturnable<Float> cir)
	{
		@SuppressWarnings("ConstantConditions")
		var self = (ClientWorld)(Object)this;
		SpaceSkyRenderer.clientWorld$getStarColor(self, tickDelta, cir);
	}
}
