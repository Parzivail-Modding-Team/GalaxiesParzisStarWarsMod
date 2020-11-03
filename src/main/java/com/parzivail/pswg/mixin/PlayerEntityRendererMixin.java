package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.features.ForceFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityRendererMixin
{
	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;<init>(Lnet/minecraft/client/render/entity/EntityRenderDispatcher;Z)V", at = @At("TAIL"))
	private void init(CallbackInfo ci)
	{
		((LivingEntityRendererAccessor<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getFeatures().add(new ForceFeatureRenderer<>((PlayerEntityRenderer)(Object)this));
	}
}
