package dev.pswg.mixin.client.recoil;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.pswg.GalaxiesClient;
import dev.pswg.interaction.IRecoilEntity;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin
{
	@Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"))
	private void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, CallbackInfo ci)
	{
		if (!(entity instanceof IRecoilEntity recoilEntity))
			return;

		var recoilTime = entity.level().getGameTime() - recoilEntity.pswg$getRecoilTime() + GalaxiesClient.getTickDelta();
		if (recoilTime <= 0 || recoilTime >= 20)
			return;

		// TODO: configurable per-blaster?
		var effectDepth = 0.4f;
		var recoverSpeed = 0.8f;
		var recoilDuration = 1f;
		var recoilAngle = 25;

		var f = (float)(effectDepth * Math.min(Math.pow(recoilTime / recoilDuration, 2), Math.exp(-recoverSpeed * (recoilTime - recoilDuration))));

		matrices.mulPose(new Quaternionf().rotateX((float)Math.toRadians(f * recoilAngle)));
		matrices.translate(0, 0, f);
	}
}
