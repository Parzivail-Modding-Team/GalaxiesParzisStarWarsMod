package com.parzivail.pswg.mixin;

import com.parzivail.pswg.client.camera.CameraHelper;
import com.parzivail.pswg.client.render.features.ForceFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class PlayerEntityRendererMixin
{
	@SuppressWarnings("unchecked")
	@Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V", at = @At("TAIL"))
	private void init(CallbackInfo ci)
	{
		((LivingEntityRendererMixin<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>)this).getFeatures().add(new ForceFeatureRenderer<>((PlayerEntityRenderer)(Object)this));
	}

	@Inject(method = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
	private void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci)
	{
		CameraHelper.playerRenderHead(abstractClientPlayerEntity, ci);
	}
}
