package dev.pswg.mixin.client.recoil;

import dev.pswg.GalaxiesClient;
import dev.pswg.interaction.IRecoilEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
	@Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V", at = @At("HEAD"))
	private void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, CallbackInfo ci)
	{
		if (!(entity instanceof IRecoilEntity recoilEntity))
			return;

		var recoilTime = entity.getEntityWorld().getTime() - recoilEntity.pswg$getRecoilTime() + GalaxiesClient.getTickDelta();
		if (recoilTime <= 0 || recoilTime >= 20)
			return;

		// TODO: configurable per-blaster?
		var effectDepth = 0.4f;
		var recoverSpeed = 0.8f;
		var recoilDuration = 1f;
		var recoilAngle = 25;

		var f = (float)(effectDepth * Math.min(Math.pow(recoilTime / recoilDuration, 2), Math.exp(-recoverSpeed * (recoilTime - recoilDuration))));

		matrices.multiply(new Quaternionf().rotateX((float)Math.toRadians(f * recoilAngle)));
		matrices.translate(0, 0, f);
	}
}
